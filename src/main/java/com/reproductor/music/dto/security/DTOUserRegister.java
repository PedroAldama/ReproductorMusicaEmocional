package com.reproductor.music.dto.security;

import lombok.Builder;
import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;



@Builder
@Data
public class DTOUserRegister {
    @NotBlank(message = "username is required")
    @Size(min = 3, max = 20, message = "username length is in range 3-20")
    private String username;

    @NotBlank(message = "password is required")
    @Size(min = 6, max = 100, message = "password size must be min 6 characters")
    private String password;

    @NotBlank(message = "email is required")
    @Email(message = "email must be valid")
    @Size(max = 50, message = "email so long, max 50 characters")
    private String email;
}
