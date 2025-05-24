package com.reproductor.music.dto.request;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class DTOInteraction {
    private String song;
    private String action;
}
