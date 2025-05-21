package com.reproductor.music.services.auth;

import com.reproductor.music.dto.response.DTOUserResponse;
import com.reproductor.music.dto.security.DTOLogin;
import com.reproductor.music.dto.security.DTOUserRegister;

public interface AuthService {
    String login(DTOLogin dtoLogin);
    DTOUserResponse registerUser(DTOUserRegister dtoUserRegister);
}
