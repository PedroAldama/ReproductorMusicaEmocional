package com.reproductor.music.dto.request;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ListRequest {
    private String name;
    private String username;
}
