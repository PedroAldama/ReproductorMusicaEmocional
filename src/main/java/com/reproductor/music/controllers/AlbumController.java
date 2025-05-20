package com.reproductor.music.controllers;

import com.reproductor.music.dto.response.DTOAlbum;
import com.reproductor.music.dto.request.RequestAlbum;
import com.reproductor.music.services.album.AlbumService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/album")
@RequiredArgsConstructor
public class AlbumController {
    private final AlbumService albumService;

    @GetMapping
    public ResponseEntity<List<DTOAlbum>> getAll() {
        return ResponseEntity.ok(albumService.getAllAlbums());
    }

    @GetMapping("/name")
    public ResponseEntity<DTOAlbum> getByName(@RequestParam String name) {
        return ResponseEntity.ok(albumService.getAlbumByTitleResponse(name));
    }

    @GetMapping("/artist")
    public ResponseEntity<List<DTOAlbum>> getAllByArtist(@RequestParam String artist) {
        return ResponseEntity.ok(albumService.getAlbumsByArtistResponse(artist));
    }

    @GetMapping("/release")
    public ResponseEntity<List<DTOAlbum>> getAllByRelease(@RequestParam int start, @RequestParam int end) {
        return ResponseEntity.ok(albumService.getAlbumsByRangeResponse(start,end));
    }
    @GetMapping("/artist/date")
    public ResponseEntity<List<DTOAlbum>> getAllByNameAndRelease(@RequestParam String artist, @RequestParam int start, @RequestParam int end) {
        return ResponseEntity.ok(albumService.getAlbumsByArtistAndRangeResponse(artist,start,end));
    }

    @PostMapping("/add")
    public ResponseEntity<RequestAlbum> add(@RequestBody RequestAlbum album) {
        albumService.addAlbum(album);
        return ResponseEntity.status(HttpStatus.CREATED).body(album);
    }

    @PostMapping("/addToAlbum")
    public ResponseEntity<DTOAlbum> addSongToAlbum(@RequestParam String album, @RequestBody List<String> songs) {
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(albumService.addSongsToAlbum(album, songs));
    }
}
