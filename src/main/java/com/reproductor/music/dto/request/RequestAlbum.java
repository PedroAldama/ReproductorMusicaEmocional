package com.reproductor.music.dto.request;

import lombok.Builder;
import lombok.Data;


@Data
@Builder
public class RequestAlbum {
    private String name;
    private String artist;
    private int release;
}
