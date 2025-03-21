package com.reproductor.music.entities;


import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Data;

import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;

@Document(collection = "history")
@Builder
@Data
public class History {
    @Id
    private String id;
    private String user;
    private List<String> songs;
    private Date creation;
}
