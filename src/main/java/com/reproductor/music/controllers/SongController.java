package com.reproductor.music.controllers;

import com.reproductor.music.dto.DTOSong;
import com.reproductor.music.dto.request.RequestSong;
import com.reproductor.music.services.RedisServiceImp;
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
    private final RedisServiceImp redisService;

    @GetMapping
    public ResponseEntity<List<DTOSong>> getAllSongs() {
        return ResponseEntity.ok(songService.getAllSongsResponse());
    }

    @GetMapping("/play")
    public ResponseEntity<DTOSong> getSongByName(@RequestParam String name) {
        DTOSong song = songService.getSongByNameResponse(name);
        redisService.addSongToList(song.getName());
        return ResponseEntity.ok(song);
    }

    @PostMapping("/add")
    public ResponseEntity<DTOSong>addSong( @RequestParam("file") MultipartFile file, @RequestBody RequestSong song) throws IOException {
        return ResponseEntity.status(HttpStatus.CREATED).body(songService.addSong(song,file));
    }
}
