package com.reproductor.music.controllers;

import com.reproductor.music.entities.History;
import com.reproductor.music.services.HistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/history")
@RequiredArgsConstructor
public class HistoryController {
    private final HistoryService historyService;

    @GetMapping
    public List<History> getAll(){
        return historyService.getAllHistory();
    }

    @GetMapping("/user")
    public History getUserHistory(@RequestParam @DateTimeFormat(pattern = "dd-MM-yyyy") Date date, @RequestParam String user){
        return historyService.getHistory(user,date);
    }

    @PostMapping("/add")
    public void addHistory(@RequestParam String name){
         historyService.createHistory(name);
    }
}
