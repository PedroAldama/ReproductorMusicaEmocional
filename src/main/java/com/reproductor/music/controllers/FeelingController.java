package com.reproductor.music.controllers;

import com.reproductor.music.dto.request.FeelingsRequest;
import com.reproductor.music.dto.response.DTOSong;
import com.reproductor.music.dto.response.DTOSongFeelings;
import com.reproductor.music.dto.response.DTOVectorSong;
import com.reproductor.music.services.feelings.FeelingsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/feelings")
public class FeelingController {

    private final FeelingsService feelingsService;

    @GetMapping
    public ResponseEntity<DTOSongFeelings> getSongFeelings(@RequestParam String song) {
        return ResponseEntity.ok(feelingsService.searchByName(song));
    }

    @GetMapping("/similarSongs")
    public ResponseEntity<List<DTOVectorSong>> test(Principal principal) {
        return ResponseEntity.ok(feelingsService.findMostSimilarSongs(principal.getName()));
    }
    @GetMapping("/currentValues")
    public ResponseEntity<List<Double>> avg(Principal principal) {
        return ResponseEntity.ok(feelingsService.getCurrentFeelingsByUser(principal.getName()));
    }


    @GetMapping("/recommendation")
    public ResponseEntity<List<DTOVectorSong>> getSimilarFeelings(@RequestParam String song) {
        return ResponseEntity.ok().body(feelingsService.searchSongBySimilarFeelings(song));
    }

    @PostMapping("/add")
    public ResponseEntity<String> addSongFeeling(@RequestBody List<FeelingsRequest> request) {
        feelingsService.addFeelings(request);
        return  ResponseEntity.ok("Feelings added successfully");
    }
    @GetMapping("/songs")
    public ResponseEntity<List<DTOSong>> searchSongByUsername(){
        return ResponseEntity.ok(feelingsService.searchByUsername());
    }
}
