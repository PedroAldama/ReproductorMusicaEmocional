package com.reproductor.music.services.feelings.websocket;

import com.reproductor.music.dto.response.DTOSong;
import com.reproductor.music.dto.response.DTOVectorSong;
import com.reproductor.music.services.cache.FeelingSongCacheServiceImp;
import com.reproductor.music.services.feelings.FeelingsService;
import com.reproductor.music.services.redis.RedisServiceImp;
import com.reproductor.music.services.song.SongService;
import com.reproductor.music.utils.VectorUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WebSocketFeelingsServiceImp implements WebSocketFeelingsService {

    private final FeelingsService feelingsService;
    private final FeelingSongCacheServiceImp redisService;
    private final SongService songService;
    private final VectorUtils vectorUtils;

    @Transactional
    @Override
    public DTOSong recommendationWebSocket(String userName) {
        List<DTOVectorSong> songs = feelingsService.findMostSimilarSongs(userName);
        if(songs.stream().findFirst().isPresent()){
            DTOVectorSong song = songs.getFirst();
            updateUserFeelings(userName,song);
            return DTOSong.builder().name(song.getTitle()).src(songService.getSrc(song.getTitle())).build();
        }
        return DTOSong.builder().name("Not More Songs").src("").build();
    }

    public void updateUserFeelings(String userName,DTOVectorSong song){
        //When a song is recommended, update the feeling vector to send new songs
        List<Double> newFeelings = vectorUtils.average(
                List.of(feelingsService.getCurrentFeelingsByUser(userName),song.getFeelings()));
        redisService.addSongToHistory(userName, song.getTitle());
        redisService.setCurrentFeeling(userName,newFeelings);
    }
}
