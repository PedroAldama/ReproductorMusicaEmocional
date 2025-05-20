package com.reproductor.music.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DTOSong {
    private String name;
    private String src;
    private double duration;
}
