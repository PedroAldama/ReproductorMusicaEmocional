package com.reproductor.music.controllers;

import com.reproductor.music.dto.response.DTOGroup;
import com.reproductor.music.dto.request.RequestGroup;
import com.reproductor.music.services.group.GroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/artist")
@RequiredArgsConstructor
public class GroupController {
    private final GroupService groupService;

    @PostMapping("/register")
    public ResponseEntity<String> registerGroup(@RequestParam String group) {
        groupService.save(group);
        return ResponseEntity.status(HttpStatus.CREATED).body(group);
    }

    @GetMapping
    public ResponseEntity<List<DTOGroup>> getAllGroups() {
        return ResponseEntity.ok(groupService.findAll());
    }
}
