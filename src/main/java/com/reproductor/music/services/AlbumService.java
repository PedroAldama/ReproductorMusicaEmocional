package com.reproductor.music.services;

import com.reproductor.music.dto.DTOAlbum;
import com.reproductor.music.dto.request.RequestAlbum;
import com.reproductor.music.entities.Album;

import java.util.List;

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
}
