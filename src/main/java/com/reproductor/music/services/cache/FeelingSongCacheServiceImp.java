package com.reproductor.music.services.cache;

import com.reproductor.music.services.redis.RedisServiceImp;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FeelingSongCacheServiceImp implements FeelingSongCacheService {

    private final RedisServiceImp redisService;

    @Override
    public List<Double> getCurrentFeeling(String username) {
        String feelings = redisService.getCurrentFeeling(username);
        if(feelings != null && !feelings.isEmpty()){
            return Arrays.stream(feelings.split(",")).map(Double::valueOf).toList();
        }
        return Collections.emptyList();
    }

    @Override
    public void setCurrentFeeling(String username, List<Double> feelings) {
      redisService.setCurrentFeeling(username, feelings);
    }

    @Override
    public void addSongToHistory(String username, String song) {
       redisService.addSongToList(username, song);
    }

    @Override
    public List<String> getSongHistory(String username) {
        return redisService.getListOf(username);
    }

    @Override
    public List<Double> getUserSongVector(String username, String songName) {
         return redisService.getUserSongVector(username, songName);
    }

    @Override
    public void saveUserSongVector(String username, String songName, List<Double> vector) {
        String raw = vector.stream().map(String::valueOf).collect(Collectors.joining(","));
        String key = String.format("feelings:%s:%s", username, songName);
        redisService.saveUserSongVectorToRedis(key, raw);
        redisService.setUserSongsVectorCurrentlyStorage(username, songName);
    }

    @Override
    public List<String> getUserCachedSongs(String username) {
       return redisService.getUserSongsVectorCurrentlyStorage(username).stream().toList();
    }

    @Override
    public void cacheUserSong(String username, String songName) {
        redisService.setUserSongsVectorCurrentlyStorage(username, songName);
    }

    @Override
    public List<String> getFullUserSongList(String username) {
       return redisService.getFullList(username);
    }
}
