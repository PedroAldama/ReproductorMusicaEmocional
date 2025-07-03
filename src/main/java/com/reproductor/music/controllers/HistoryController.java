package com.reproductor.music.controllers;

import com.reproductor.music.entities.History;
import com.reproductor.music.services.history.HistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
    public History getUserHistory(@RequestParam @DateTimeFormat(pattern = "dd-MM-yyyy") Date date){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return historyService.getHistory(date, auth.getName());
    }

    @PostMapping("/add")
    public void addHistory(){
        historyService.createHistory("Hikari");
    }
}
