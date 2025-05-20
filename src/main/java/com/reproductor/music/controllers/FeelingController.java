package com.reproductor.music.controllers;

import com.reproductor.music.dto.request.FeelingsRequest;
import com.reproductor.music.services.feelings.FeelingsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/feelings")
public class FeelingController {

    private final FeelingsService feelingsService;

    @GetMapping
    public ResponseEntity<?> getSongFeelings(@RequestParam String song) {
        return ResponseEntity.ok(feelingsService.searchByName(song));
    }

    @GetMapping("/test")
    public ResponseEntity<?> test() {
        return ResponseEntity.ok(feelingsService.findMostSimilarSongs());
    }
    @GetMapping("/avg")
    public ResponseEntity<?> avg() {
        return ResponseEntity.ok(feelingsService.getCurrentFeelingsByUser());
    }


    @GetMapping("/recommendation")
    public List<?> getSimilarFeelings(@RequestParam String song) {
        return feelingsService.searchSongBySimilarFeelings(song);
    }

    @PostMapping("/add")
    public ResponseEntity<?> addSongFeeling(@RequestBody FeelingsRequest request) {
        feelingsService.addFeelings(request);
        return  ResponseEntity.ok("Feelings added successfully to " + request.getSongName());
    }
    @GetMapping("/songs")
    public ResponseEntity<?> searchSongByUsername(){
        return ResponseEntity.ok(feelingsService.searchByUsername());
    }
}
