package com.reproductor.music.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Builder
@Data
public class DTOSongFeelings {
    private String name;
    private Map<String,Double> feelings;
}
