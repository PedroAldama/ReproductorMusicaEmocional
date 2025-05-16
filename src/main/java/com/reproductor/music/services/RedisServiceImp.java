package com.reproductor.music.services;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class RedisServiceImp {

    private final StringRedisTemplate redisTemplate;
    private static final String LIST_KEY = "list";
    private static final String FEELING_KEY = "feeling";
    private static final String LIST_SONG = "songs:";

    public void addSongToList(String user,String song) {
        redisTemplate.opsForList().leftPush(LIST_KEY + user, song);
    }
    public List<String> getFullList(String user){
        return redisTemplate.opsForList().range(LIST_KEY + user, 0, -1);
    }
    public List<String> getListOf(String user){
        return redisTemplate.opsForList().range(LIST_KEY + user, 0, 3);
    }
    public void clearList(String user){
        redisTemplate.delete(LIST_KEY + user);
    }
    public String getCurrentFeeling(String user){
        return redisTemplate.opsForValue().get(FEELING_KEY + user);
    }
    public void setCurrentFeeling(String user, List<Double> feeling){
        redisTemplate.opsForValue().set(FEELING_KEY + user, feeling.stream().map(String::valueOf).collect(Collectors.joining(",")));
    }
    public void saveUserSongVectorToRedis(String key,String raw) {
        redisTemplate.opsForValue().set(key, raw);
    }

    public List<Double> getUserSongVector(String username, String songName) {
        String key = String.format("feelings:%s:%s", username, songName);
        String raw = redisTemplate.opsForValue().get(key);

        if (raw != null) {
            return Arrays.stream(raw.split(","))
                    .map(Double::parseDouble)
                    .toList();
        }
        return List.of();
    }





}
