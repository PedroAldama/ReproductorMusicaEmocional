package com.reproductor.music.dto.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RequestSong {
    private String title;
    private double duration;
}
