package com.reproductor.music.entities;

import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@Builder
@Document(collection = "playList")
public class Lista {
    @Id
    private String id;
    private String name;
    private String user;
    private String principalEmotion;
    private List<String> songs = new ArrayList<>();
    private Date creation;
    private Date lastModification;
}
