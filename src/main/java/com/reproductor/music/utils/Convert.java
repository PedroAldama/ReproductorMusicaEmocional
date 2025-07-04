package com.reproductor.music.utils;

import com.reproductor.music.dto.response.DTOVectorSong;
import com.reproductor.music.dto.response.*;
import com.reproductor.music.entities.*;

import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.List;

public class Convert {
    private Convert(){
        //Private constructor
    }

    public static DTOSong convertSongToDto(Song song){
        return DTOSong.builder()
                .name(song.getName())
                .src(song.getSrc())
                .duration(song.getDuration())
                .build();
    }
    public static DTOAlbum convertAlbumToDto(Album album){
        return DTOAlbum.builder()
                .name(album.getTitle())
                .release(album.getReleaseDate())
                .artist(album.getGroup().getName())
                .songs(convertSongList(album.getSongs()))
                .build();
    }
    public static DTOAlbumsGroup convertAlbumToDtoGroup(Album album){
        return DTOAlbumsGroup.builder()
                .name(album.getTitle())
                .release(album.getReleaseDate())
                .build();
    }
    public static DTOGroup convertGroupToDto(Group group){
        return DTOGroup.builder()
                .name(group.getName())
                .albums(convertOnlyAlbumList(group.getAlbum()))
                .build();
    }
    public static DTOLIST convertListToDTO(Lista lista){
        return DTOLIST.builder()
                .name(lista.getName())
                .data(new SimpleDateFormat("dd/MM/yyyy").format(lista.getCreation()))
                .songs(lista.getSongs())
                .build();
    }

    public static List<DTOSong> convertSongList(List<Song> songs){
        return songs.stream().map(Convert::convertSongToDto).toList();
    }
    public static List<DTOAlbum> convertAlbumList(List<Album> albums){
        return albums.stream().map(Convert::convertAlbumToDto).toList();
    }
    public static List<DTOAlbumsGroup> convertOnlyAlbumList(List<Album> albums){
        return albums.stream().map(Convert::convertAlbumToDtoGroup).toList();
    }
    public static List<DTOGroup> convertGroupDtoList(List<Group> groups){
        return groups.stream().map(Convert::convertGroupToDto).toList();
    }
    public static List<DTOLIST> convertListDtoList(List<Lista> lists){
        return lists.stream().map(Convert::convertListToDTO).toList();
    }

    public static List<DTOVectorSong> getAllSongsWithFeelings(List<Song> songs  ) {
        return songs.stream()
                .map(song -> {
                    List<Double> values = song.getSongFeelings().stream()
                            .sorted(Comparator.comparing(sf -> sf.getFeeling().getId()))
                            .map(SongFeelings::getValue)
                            .map(v-> (double)v)
                            .toList();
                    return  DTOVectorSong.builder().title(song.getName()).feelings(values).build();
                })
                .toList();
    }
}
