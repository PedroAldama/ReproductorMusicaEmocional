package com.reproductor.music.dto;

import lombok.Builder;
import lombok.Data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Builder
@Data
public class DTOSongFeelings {
    private String name;
    private Map<String,Double> feelings;
}
