package com.reproductor.music.utils;

import com.reproductor.music.services.history.HistoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class RedisScheduled {
    private final StringRedisTemplate redisTemplate;
    private final HistoryService historyService;

    @Scheduled(cron = "0 0 0 * * *")
    public void clearRedisList(){
        Set<String> keys = redisTemplate.keys("list*");
        if(keys != null && !keys.isEmpty()){
            Set<String> users = keys.stream().map(k -> k.split("list")[1]).collect(Collectors.toSet());
            users.forEach(historyService::createHistory);
            log.info("Clear redisList");
        }
    }
}
