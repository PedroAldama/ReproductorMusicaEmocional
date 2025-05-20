package com.reproductor.music.utils;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class UserUtils {
    public String getCurrentUserName(){
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }
}
