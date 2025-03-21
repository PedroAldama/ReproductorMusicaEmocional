package com.reproductor.music.services;

import com.reproductor.music.entities.Feelings;
import com.reproductor.music.entities.History;
import com.reproductor.music.entities.Song;
import com.reproductor.music.entities.SongFeelings;
import com.reproductor.music.repositories.HistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class HistoryServiceImp implements HistoryService {

    private final HistoryRepository historyRepository;
    private final SongService songService;

    @Override
    public List<Song> recommendByEmotion(String user) {
        List<History> histories = historyRepository.findByUserOrderByCreationDesc(user);
        if (histories.isEmpty()) return new ArrayList<>();

        List<String> lastSongs = histories.getFirst().getSongs();

        Map<String, Integer> feelSum = new HashMap<>();

        for (String song : lastSongs) {
            List<SongFeelings> emotions = songService.getSongByName(song).getSongFeelings();
            for (SongFeelings e : emotions) {
                String feeling = e.getFeeling().getFeeling();
                feelSum.put(feeling, feelSum.getOrDefault(feeling, 0) + e.getValue());
            }
        }
        String principal = Collections.max(feelSum.entrySet(), Map.Entry.comparingByValue()).getKey();
        return List.of();
    }
}
