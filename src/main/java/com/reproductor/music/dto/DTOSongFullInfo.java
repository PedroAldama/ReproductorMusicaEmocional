package com.reproductor.music.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DTOSongFullInfo {
    private String name;
    private String src;
    private double duration;
    private String album;
    private String artist;
}
