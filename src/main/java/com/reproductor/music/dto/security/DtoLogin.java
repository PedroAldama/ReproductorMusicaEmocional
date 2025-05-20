package com.reproductor.music.dto.security;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DtoLogin {
    private String username;
    private String password;
}
