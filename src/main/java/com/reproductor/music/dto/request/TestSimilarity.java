package com.reproductor.music.dto.request;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
public class TestSimilarity {
    String name;
    List<Integer> vector;
}
