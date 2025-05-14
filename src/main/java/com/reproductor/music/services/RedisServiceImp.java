package com.reproductor.music.services;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RedisServiceImp {

    private final StringRedisTemplate redisTemplate;
    private static final String LIST_KEY = "list";

    public void addSongToList(String user,String song) {
        redisTemplate.opsForList().leftPush(LIST_KEY + user, song);
    }
    public List<String> getFullList(String user){
        return redisTemplate.opsForList().range(LIST_KEY + user, 0, -1);
    }

    public void clearList(String user){
        redisTemplate.delete(LIST_KEY + user);
    }
}
