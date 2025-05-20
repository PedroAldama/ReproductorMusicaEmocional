package com.reproductor.music.controllers;

import com.reproductor.music.dto.response.DTOSong;
import com.reproductor.music.dto.request.RequestSong;
import com.reproductor.music.security.JwtTokenProvider;
import com.reproductor.music.services.song.SongService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RequestMapping("/api/song")
@RestController
@RequiredArgsConstructor
public class SongController {

    private final SongService songService;
    private final JwtTokenProvider tokenProvider;

    @GetMapping
    public ResponseEntity<List<DTOSong>> getAllSongs() {
        return ResponseEntity.ok(songService.getAllSongsResponse());
    }

    @GetMapping("/play")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_USER','ROLE_GUESS')")
    public ResponseEntity<DTOSong> getSongByName(@RequestParam String song) {
        DTOSong response = songService.getSongByNameResponse(song);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/add")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<DTOSong>addSong( @RequestParam("file") MultipartFile file, @RequestParam String song, @RequestParam double duration) throws IOException {
        return ResponseEntity.status(HttpStatus.CREATED).body(songService.addSong(song,duration,file));
    }
}
