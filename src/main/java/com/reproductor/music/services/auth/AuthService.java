package com.reproductor.music.services.auth;

import com.reproductor.music.dto.security.DtoLogin;

public interface AuthService {
    String login(DtoLogin dtoLogin);
}
