package com.reproductor.music.controllers;

import com.reproductor.music.dto.request.DTOVectorSong;
import com.reproductor.music.dto.request.FeelingsRequest;
import com.reproductor.music.dto.request.TestSimilarity;
import com.reproductor.music.services.FeelingsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/feelings")
public class FeelingController {

    private final FeelingsService feelingsService;

    @GetMapping
    public ResponseEntity<?> getSongFeelings(@RequestParam String name) {
        return ResponseEntity.ok(feelingsService.searchByName(name));
    }

    @PostMapping("/recomendation")
    public DTOVectorSong getSimilarFeelings(@RequestBody TestSimilarity request) {
        System.out.println(request.getName());
        return feelingsService.searchSongBySimilarFeelings(request.getVector(),request.getName());
    }

    @PostMapping("/add")
    public ResponseEntity<?> addSongFeeling(@RequestBody FeelingsRequest request) {
        feelingsService.addFeelings(request);
        return  ResponseEntity.ok("Feelings added successfully to " + request.getSongName());
    }
}
