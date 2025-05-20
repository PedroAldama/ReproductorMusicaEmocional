package com.reproductor.music.services.auth;

import com.reproductor.music.dto.security.DTOLogin;

public interface AuthService {
    String login(DTOLogin dtoLogin);
}
