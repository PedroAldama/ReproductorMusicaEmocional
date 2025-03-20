package com.reproductor.music.controllers;

import com.reproductor.music.dto.DTOAlbum;
import com.reproductor.music.dto.request.RequestAlbum;
import com.reproductor.music.services.AlbumService;
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
    @GetMapping("/name/{name}")
    public ResponseEntity<DTOAlbum> getByName(@PathVariable String name) {
        return ResponseEntity.ok(albumService.getAlbumByTitleResponse(name));
    }

    @GetMapping("/artist/{artist}")
    public ResponseEntity<List<DTOAlbum>> getAllByArtist(@PathVariable String artist) {
        return ResponseEntity.ok(albumService.getAlbumsByArtistResponse(artist));
    }

    @GetMapping("/release/{start}/{end}")
    public ResponseEntity<List<DTOAlbum>> getAllByRelease(@PathVariable int start, @PathVariable int end) {
        return ResponseEntity.ok(albumService.getAlbumsByRangeResponse(start,end));
    }
    @GetMapping("/{artist}/{start}/{end}")
    public ResponseEntity<List<DTOAlbum>> getAllByNameAndRelease(@PathVariable String artist, @PathVariable int start, @PathVariable int end) {
        return ResponseEntity.ok(albumService.getAlbumsByArtistAndRangeResponse(artist,start,end));
    }

    @PostMapping("/add")
    public ResponseEntity<RequestAlbum> add(@RequestBody RequestAlbum album) {
        albumService.addAlbum(album);
        return ResponseEntity.status(HttpStatus.CREATED).body(album);
    }

    @PostMapping("/add/{album}")
    public ResponseEntity<DTOAlbum> addSongToAlbum(@PathVariable String album, @RequestBody List<String> songs) {
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(albumService.addSongsToAlbum(album, songs));
    }
}
