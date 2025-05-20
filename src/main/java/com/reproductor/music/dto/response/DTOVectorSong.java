package com.reproductor.music.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Builder
@Data
public class DTOVectorSong {
    private String title;
    private List<Double> feelings = new ArrayList<>();
    private double similarity;
}
