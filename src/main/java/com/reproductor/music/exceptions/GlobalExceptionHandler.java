package com.reproductor.music.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final String ERROR = "error";
    private static final String MESSAGE = "message";

    @ExceptionHandler(SongExceptions.SongNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleSongNotFound(SongExceptions.SongNotFoundException ex) {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put(ERROR, "Song Not Found");
        errorResponse.put(MESSAGE, ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @ExceptionHandler(SongExceptions.ArtistNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleArtistNotFound(SongExceptions.ArtistNotFoundException ex) {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put(ERROR, "Artist Not Found");
        errorResponse.put(MESSAGE, ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @ExceptionHandler(SongExceptions.AlbumNotFoundException.class)
    public ResponseEntity<Map<String,String>> handleAlbumNotFound(SongExceptions.AlbumNotFoundException ex) {
        Map<String,String> map = new HashMap<>();
        map.put(ERROR, "Album Not Found");
        map.put(MESSAGE, ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(map);
    }

    @ExceptionHandler(ListException.ListNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleListNotFound(ListException.ListNotFoundException ex) {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put(ERROR, "List Not Found");
        errorResponse.put(MESSAGE, ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }
}