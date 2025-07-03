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
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class HistoryServiceImp implements HistoryService {

    private final HistoryRepository historyRepository;
    private final UserUtils userUtils;
    private final RedisServiceImp redisService;
    private final HistoryServiceTransactions historyTransactions;


    @Override
    @Transactional
    public void createHistory(String user) {
        History searchHistory = historyTransactions.getHistory(new Date(),user);
        if(searchHistory == null) {
            History history = History.builder()
                    .id(new SimpleDateFormat("dd-MM-yyyy").format(new Date()) + user)
                    .user(user)
                    .creation(new Date())
                    .songs(redisService.getFullList(user))
                    .build();
                    historyRepository.save(history);
        }else {
            historyTransactions.addToHistory(searchHistory);
        }

        redisService.clearList(user);
    }

    @Override
    public History getHistory(Date date, String user) {
        return historyTransactions.getHistory(date,user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<History> getAllHistory() {
        String user = userUtils.getCurrentUserName();
        return historyRepository.findAllByUserOrderByCreationDesc(user);
    }
}
