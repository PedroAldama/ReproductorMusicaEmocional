package com.reproductor.music.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Album {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String title;
    private int releaseDate;

    @OneToMany(mappedBy = "album")
    @JsonManagedReference
    private List<Song> songs = new ArrayList<>();


    @ManyToOne
    @JoinColumn(name = "group_id")
    @JsonBackReference
    private Group group;

}
