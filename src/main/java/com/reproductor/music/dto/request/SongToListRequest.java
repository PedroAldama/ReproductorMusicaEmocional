package com.reproductor.music.dto.request;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class SongToListRequest {
    private String name;
    private List<String> song;
}
