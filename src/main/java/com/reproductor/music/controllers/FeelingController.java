package com.reproductor.music.controllers;

import com.reproductor.music.dto.request.DTOVectorSong;
import com.reproductor.music.dto.request.FeelingsRequest;
import com.reproductor.music.dto.request.TestSimilarity;
import com.reproductor.music.services.FeelingsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/feelings")
public class FeelingController {

    private final FeelingsService feelingsService;

    @GetMapping("/test")
    public ResponseEntity<?> test(@RequestParam String user) {
        return ResponseEntity.ok(feelingsService.findMostSimilarSongs(user));
    }
    @GetMapping("/avg")
    public ResponseEntity<?> avg(@RequestParam String user) {
        return ResponseEntity.ok(feelingsService.getCurrentFeelingsByUser(user));
    }
    @GetMapping
    public ResponseEntity<?> getSongFeelings(@RequestParam String name, @RequestParam String user) {
        return ResponseEntity.ok(feelingsService.searchByName(name,user));
    }

    @PostMapping("/recommendation")
    public DTOVectorSong getSimilarFeelings(@RequestParam String user) {
        return feelingsService.searchSongBySimilarFeelings(user);
    }

    @PostMapping("/add")
    public ResponseEntity<?> addSongFeeling(@RequestBody FeelingsRequest request,@RequestParam String user) {
        feelingsService.addFeelings(request,user);
        return  ResponseEntity.ok("Feelings added successfully to " + request.getSongName());
    }
    @GetMapping("/songs/{user}")
    public ResponseEntity<?> searchSongByUsername(@PathVariable String user){
        return ResponseEntity.ok(feelingsService.searchByUsername(user));
    }
}
