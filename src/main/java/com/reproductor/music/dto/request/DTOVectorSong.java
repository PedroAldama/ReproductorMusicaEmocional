package com.reproductor.music.dto.request;

import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Builder
@Data
public class DTOVectorSong {
    private String title;
    private List<Integer> feelings = new ArrayList<>();
}
