package com.reproductor.music.services.history;

import com.reproductor.music.entities.History;
import com.reproductor.music.entities.Song;
import com.reproductor.music.entities.SongFeelings;
import com.reproductor.music.repositories.HistoryRepository;
import com.reproductor.music.services.redis.RedisServiceImp;
import com.reproductor.music.services.song.SongService;
import com.reproductor.music.utils.UserUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class HistoryServiceImp implements HistoryService {

    private final HistoryRepository historyRepository;
    private final UserUtils userUtils;
    private final SongService songService;
    private final RedisServiceImp redisService;

    private String userName;

    @Override
    public List<Song> recommendByEmotion() {
        initial();
        List<History> histories = historyRepository.findByUserOrderByCreationDesc(this.userName);
        if (histories.isEmpty()) return new ArrayList<>();
        List<String> lastSongs = histories.getFirst().getSongs();

        Map<String, Integer> feelSum = new HashMap<>();

        for (String song : lastSongs) {
            List<SongFeelings> emotions = songService.getSongByName(song).getSongFeelings();
            for (SongFeelings e : emotions) {
                String feeling = e.getFeeling().getFeeling();
                feelSum.put(feeling, (int) (feelSum.getOrDefault(feeling, 0) + e.getValue()));
            }
        }
        String principal = Collections.max(feelSum.entrySet(), Map.Entry.comparingByValue()).getKey();
        return List.of();
    }

    @Override
    public void createHistory(String user) {
        initial();
        History searchHistory = getHistory(new Date());
        if(searchHistory == null) {
            History history = History.builder()
                    .id(new SimpleDateFormat("dd-MM-yyyy").format(new Date()) + user)
                    .user(user)
                    .creation(new Date())
                    .songs(redisService.getFullList(user))
                    .build();
                    historyRepository.save(history);
        }else {
            addToHistory(searchHistory);
        }

        redisService.clearList(user);
    }

    @Override
    public void addToHistory(History history) {
        initial();
        List<String> songs = Stream.concat(
                redisService.getFullList(this.userName).stream(),
                history.getSongs().stream()
        ).toList();
        history.setSongs(songs);
        historyRepository.save(history);
    }

    @Override
    public History getHistory(Date date) {
        initial();
        return historyRepository
                .findById(new SimpleDateFormat("dd-MM-yyyy").format(date) + this.userName)
                .orElse(null);
    }

    @Override
    public List<History> getAllHistory() {
        initial();
        String user = userUtils.getCurrentUserName();
        return historyRepository.findAllByUserOrderByCreationDesc(user);
    }

    @Override
    public List<String> getSongsHistory( Date date) {
        History history = getHistory(date);
        return  history != null ? history.getSongs() : List.of();
    }
    private void initial(){
        if (this.userName == null){
            this.userName = userUtils.getCurrentUserName();
        }
    }
}
