package com.reproductor.music.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DTOAlbumsGroup {
    private String name;
    private int release;
}
