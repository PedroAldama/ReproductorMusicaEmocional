package com.reproductor.music.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
public class DTOAlbum {
    private String name;
    private String artist;
    private int release;
    private List<DTOSong> songs = new ArrayList<>();
}
