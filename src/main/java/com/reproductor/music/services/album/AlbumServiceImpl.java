package com.reproductor.music.services.album;

import com.reproductor.music.dto.response.DTOAlbum;
import com.reproductor.music.dto.request.RequestAlbum;
import com.reproductor.music.entities.Album;
import com.reproductor.music.entities.Group;
import com.reproductor.music.entities.Song;
import com.reproductor.music.exceptions.SongExceptions;
import com.reproductor.music.repositories.AlbumRepository;
import com.reproductor.music.services.group.GroupService;
import com.reproductor.music.services.song.SongService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static com.reproductor.music.utils.Convert.*;

@Service
@RequiredArgsConstructor
public class AlbumServiceImpl implements AlbumService {

    private final AlbumRepository albumRepository;
    private final GroupService groupService;
    private final SongService songService;

    @Override
    public List<DTOAlbum> getAllAlbums() {
        return convertAlbumList(albumRepository.findAll());
    }

    @Override
    public Album getAlbumByTitle(String name) {
        return albumRepository.findByTitle(name)
                .orElseThrow(() -> new SongExceptions.AlbumNotFoundException(name));
    }

    @Override
    public DTOAlbum getAlbumByTitleResponse(String name) {
        return convertAlbumToDto(albumRepository.findByTitle(name)
                .orElseThrow(() -> new SongExceptions.AlbumNotFoundException(name)));
    }

    @Override
    public List<Album> getAlbumsByArtist(String artist) {
        return albumRepository.findByGroup_Name(artist);
    }

    @Override
    public List<DTOAlbum> getAlbumsByArtistResponse(String artist) {
        return convertAlbumList(albumRepository.findByGroup_Name(artist));
    }

    @Override
    public List<DTOAlbum> getAlbumsByRangeResponse(int start, int end) {
        return convertAlbumList(albumRepository.findByReleaseDateIsBetween(start, end));
    }

    @Override
    public List<DTOAlbum> getAlbumsByArtistAndRangeResponse(String artist, int start, int end) {
        return convertAlbumList(albumRepository.findByGroup_NameAndReleaseDateIsBetween(artist, start, end));
    }

    @Override
    @Transactional
    public void save(Album album){
        albumRepository.save(album);
    }

    @Override
    public void addAlbum(RequestAlbum album) {
        Album albumToSave = Album.builder()
                .title(album.getName())
                .group(groupService.findByName(album.getArtist()))
                .releaseDate(album.getRelease())
                .build();
        save(albumToSave);
    }

    @Override
    @Transactional
    public DTOAlbum addSongsToAlbum(String albumName, List<String> songs) {
        Album album = getAlbumByTitle(albumName);
        List<Song> songList =  songs.stream().map(songService::getSongByName).toList();
        for(Song song: songList){
            if(!album.getSongs().contains(song) && song.getAlbum()  == null){
                album.getSongs().add(song);
                song.setAlbum(album);
                songService.save(song);
            }
        }
        albumRepository.save(album);

        return getAlbumByTitleResponse(albumName);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Object[]> findAllAlbumGroupPairs() {
        return albumRepository.findAllTitleGroupPairs();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Album> searchAlbumByName(String name) {
        return albumRepository.findByTitle(name);
    }


}
