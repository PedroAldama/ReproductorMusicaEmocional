package com.reproductor.music.controllers;

import com.reproductor.music.dto.response.DTOLIST;
import com.reproductor.music.dto.request.RemoveSongRequest;
import com.reproductor.music.dto.request.SongToListRequest;
import com.reproductor.music.services.list.ListService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("api/list")
public class ListController {
    private final ListService listService;

    @GetMapping
    public ResponseEntity<List<DTOLIST>> list() {
        return ResponseEntity.ok(listService.getAllList());
    }

    @GetMapping("/name")
    public ResponseEntity<DTOLIST> listByName(@RequestParam String name) {
        return ResponseEntity.ok(listService.getListByName(name));
    }

    @PostMapping("/create")
    public ResponseEntity<DTOLIST> create(@RequestParam String listName) {
        return ResponseEntity.status(HttpStatus.CREATED).body(listService.createList(listName));
    }

    @PostMapping("/addToList")
    public ResponseEntity<String> addToList(@RequestBody SongToListRequest list) {
        return ResponseEntity.ok(listService.addToList(list));
    }

    @PostMapping("/removeFromList")
    public ResponseEntity<String> removeFromList(@RequestBody RemoveSongRequest list) {
        return ResponseEntity.ok(listService.removeFromList(list));
    }

    @GetMapping("/creationDates")
    public ResponseEntity<List<DTOLIST>> getCreationDates(
            @RequestParam("start") @DateTimeFormat(pattern = "dd-MM-yyyy") Date startDate,
            @RequestParam("end") @DateTimeFormat(pattern = "dd-MM-yyyy") Date endDate) {
        return ResponseEntity.ok(listService.getListByDate(startDate,endDate));
    }
}
