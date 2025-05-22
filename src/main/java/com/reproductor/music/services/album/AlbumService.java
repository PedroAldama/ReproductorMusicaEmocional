package com.reproductor.music.services.album;

import com.reproductor.music.dto.response.DTOAlbum;
import com.reproductor.music.dto.request.RequestAlbum;
import com.reproductor.music.entities.Album;
import com.reproductor.music.entities.Group;

import java.util.List;
import java.util.Optional;

public interface AlbumService {
     List<DTOAlbum> getAllAlbums();
     Album getAlbumByTitle(String name);
     DTOAlbum getAlbumByTitleResponse(String name);
     List<Album> getAlbumsByArtist(String artist);
     List<DTOAlbum> getAlbumsByArtistResponse(String artist);
     List<DTOAlbum> getAlbumsByRangeResponse(int start, int end);
     List<DTOAlbum> getAlbumsByArtistAndRangeResponse(String artist, int start, int end);
     void save(Album album);
     void addAlbum(RequestAlbum album);
     DTOAlbum addSongsToAlbum(String albumName, List<String> songs);
     List<Object[]> findAllAlbumGroupPairs();
     Optional<Album> searchAlbumByName(String name);
}
