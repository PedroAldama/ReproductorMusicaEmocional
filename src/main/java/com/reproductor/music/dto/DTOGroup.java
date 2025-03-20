package com.reproductor.music.dto;

import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Builder
@Data
public class DTOGroup {
     private String name;
     private List<DTOAlbumsGroup> albums = new ArrayList<>();
}
