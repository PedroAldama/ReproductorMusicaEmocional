package com.reproductor.music.services;

import com.reproductor.music.entities.History;
import com.reproductor.music.entities.Song;
import com.reproductor.music.entities.SongFeelings;
import com.reproductor.music.repositories.HistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class HistoryServiceImp implements HistoryService {

    private final HistoryRepository historyRepository;
    private final SongService songService;
    private final RedisServiceImp redisService;

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

    @Override
    public void createHistory(String user) {
        History searchHistory = getHistory(user, new Date());
        if(searchHistory == null) {
            History history = History.builder()
                    .id(new SimpleDateFormat("dd-MM-yyyy").format(new Date()) + user)
                    .user(user)
                    .creation(new Date())
                    .songs(redisService.getFullList(user))
                    .build();
                    historyRepository.save(history);
        }else{
            List<String> songs = Stream.concat(
                    redisService.getFullList(user).stream(),
                    searchHistory.getSongs().stream()
            ).toList();
            searchHistory.setSongs(songs);
            historyRepository.save(searchHistory);
        }

        redisService.clearList(user);
    }

    @Override
    public void addToHistory(String user, Song song) {

    }

    @Override
    public History getHistory(String user, Date date) {
        return historyRepository
                .findById(new SimpleDateFormat("dd-MM-yyyy").format(date) + user)
                .orElse(null);
    }

    @Override
    public List<History> getAllHistory() {
        return historyRepository.findAll();
    }
}
