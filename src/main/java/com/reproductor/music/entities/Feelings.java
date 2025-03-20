package com.reproductor.music.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Table
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Feelings {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String feeling;

    @OneToMany(mappedBy = "feeling", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SongFeelings> songFeelings = new ArrayList<>();

}
