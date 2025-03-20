package com.reproductor.music.dto;

import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
public class DTOLIST {
    private String name;
    private String data;
    private List<String> songs = new ArrayList<>();
}
