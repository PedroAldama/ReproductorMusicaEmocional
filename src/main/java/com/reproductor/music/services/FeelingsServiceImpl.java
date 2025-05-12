package com.reproductor.music.services;

import com.reproductor.music.dto.DTOSongFeelings;
import com.reproductor.music.dto.request.DTOVectorSong;
import com.reproductor.music.entities.Song;
import com.reproductor.music.entities.SongFeelings;
import com.reproductor.music.exceptions.SongException;
import com.reproductor.music.repositories.FeelingsRepository;
import com.reproductor.music.dto.request.FeelingsRequest;
import com.reproductor.music.repositories.SongFeelingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

import static com.reproductor.music.dto.Convert.getAllSongsWithFeelings;

@RequiredArgsConstructor
@Service
public class FeelingsServiceImpl implements FeelingsService {

    private final SongService songService;
    private final FeelingsRepository repository;
    private final SongFeelingRepository songFeelingRepository;

    @Override
    public void addFeelings(FeelingsRequest feelings) {
        Song song = songService.getSongByName(feelings.getSongName());
        if (song == null) {
            throw new SongException.SongNotFoundException(feelings.getSongName());
        }
        int i = 1;
        for(int n: feelings.getFeelings()) {
             SongFeelings newSongFeeling = new SongFeelings();
             newSongFeeling.setSong(song);
             newSongFeeling.setFeeling(repository.findById((long) i++).orElseThrow());
             newSongFeeling.setValue(n);
             songFeelingRepository.save(newSongFeeling);
        }
    }

    @Override
    public void updateFeelings(FeelingsRequest feelings) {

    }

    @Override
    public DTOVectorSong searchSongBySimilarFeelings(List<Integer> feelingValues, String name) {
       List<DTOVectorSong> songs = getAllSongsWithFeelings(songService.findAllWithFeeling());
        return findMostSimilarSong(feelingValues,songs, name);
    }

    @Override
    public DTOSongFeelings searchByName(String name) {
        Song searchSong = songService.getSongByName(name);
        if(searchSong == null) {
            throw new SongException.SongNotFoundException(name);
        }
        List<SongFeelings> songFeelings = songFeelingRepository.findBySong(searchSong);

        return DTOSongFeelings.builder().name(searchSong.getName())
            .feelings(getFeelingsMap(songFeelings)).build();
    }

    private Map<String,Integer> getFeelingsMap(List<SongFeelings> songFeelings){
        Map<String,Integer> data = new HashMap<>();
        String[] feeling = {"Alegria", "Tristeza", "Calma", "Euforia", "Melancolia", "Amor", "Ira",
                "Ansiedad", "Misterio", "Empoderamiento"};
        for (int i = 0; i < songFeelings.size(); i++) {
            data.put(feeling[i], songFeelings.get(i).getValue());
        }
        return data;
    }

    private double cosineSimilarity(List<Integer> v1, List<Integer> v2) {
        double dot = 0.0;
        double normA = 0.0;
        double normB = 0.0;

        for(int i= 0; i < v1.size(); i++){
            dot+=v1.get(i)*v2.get(i);
            normA+= Math.pow(v2.get(i),2);
            normB+= Math.pow(v1.get(i),2);
        }
        return dot / (Math.sqrt(normA) * Math.sqrt(normB));
    }

    private DTOVectorSong findMostSimilarSong(List<Integer> userVector, List<DTOVectorSong> allSongs, String name) {
        return allSongs.stream()
                .filter(song -> !song.getFeelings().isEmpty() && !song.getTitle().equals(name))
                .max(Comparator.comparingDouble(song ->
                        cosineSimilarity(userVector, song.getFeelings())))
                .orElse(null);
    }
}
