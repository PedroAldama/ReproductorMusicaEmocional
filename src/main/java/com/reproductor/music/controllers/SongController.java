package com.reproductor.music.controllers;

import com.reproductor.music.dto.DTOSong;
import com.reproductor.music.dto.request.RequestSong;
import com.reproductor.music.services.SongService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RequestMapping("/api/song")
@RestController
@RequiredArgsConstructor
public class SongController {

    private final SongService songService;

    @GetMapping
    public ResponseEntity<List<DTOSong>> getAllSongs() {
        return ResponseEntity.ok(songService.getAllSongsResponse());
    }

    @GetMapping("/name")
    public ResponseEntity<DTOSong> getSongByName(@RequestParam String name) {
        return ResponseEntity.ok(songService.getSongByNameResponse(name));
    }

    @PostMapping("/add")
    public ResponseEntity<DTOSong>addSong( @RequestParam("file") MultipartFile file, @RequestBody RequestSong song) throws IOException {
        return ResponseEntity.status(HttpStatus.CREATED).body(songService.addSong(song,file));
    }
}
