package com.reproductor.music.dto.security;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DTOLogin {
    private String username;
    private String password;
}
