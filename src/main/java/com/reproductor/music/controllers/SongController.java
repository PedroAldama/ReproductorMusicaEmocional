package com.reproductor.music.controllers;

import com.reproductor.music.dto.DTOSong;
import com.reproductor.music.dto.request.RequestSong;
import com.reproductor.music.entities.ERole;
import com.reproductor.music.entities.Song;
import com.reproductor.music.services.redis.RedisServiceImp;
import com.reproductor.music.services.song.SongService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
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
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<DTOSong> getSongByName(@RequestParam String song, @AuthenticationPrincipal UserDetails user) {
        DTOSong response = songService.getSongByNameResponse(song, user.getUsername());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/add")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<DTOSong>addSong( @RequestParam("file") MultipartFile file, @RequestBody RequestSong song) throws IOException {
        return ResponseEntity.status(HttpStatus.CREATED).body(songService.addSong(song,file));
    }
    @GetMapping("/get")
    public ResponseEntity<Song>getSong(@RequestParam String songName){
        return ResponseEntity.ok(songService.getSongByName(songName));
    }
}
