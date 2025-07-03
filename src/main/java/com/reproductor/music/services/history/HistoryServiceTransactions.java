package com.reproductor.music.services.history;

import com.reproductor.music.entities.History;
import com.reproductor.music.repositories.HistoryRepository;
import com.reproductor.music.services.redis.RedisServiceImp;
import com.reproductor.music.utils.UserUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
public class HistoryServiceTransactions {

    private final HistoryRepository historyRepository;
    private final UserUtils userUtils;
    private final RedisServiceImp redisService;

    @Transactional
    public void addToHistory(History history) {
        List<String> songs = Stream.concat(
                redisService.getFullList(userUtils.getCurrentUserName()).stream(),
                history.getSongs().stream()
        ).toList();
        history.setSongs(songs);
        historyRepository.save(history);
    }

    @Transactional(readOnly = true)
    public History getHistory(Date date, String user) {
        return historyRepository
                .findById(new SimpleDateFormat("dd-MM-yyyy").format(date) + user)
                .orElse(null);
    }

}
