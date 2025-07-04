package com.reproductor.music.services.auth;

import com.reproductor.music.dto.response.DTOUserResponse;
import com.reproductor.music.dto.security.DTOLogin;
import com.reproductor.music.dto.security.DTOUserRegister;
import com.reproductor.music.entities.Users;
import com.reproductor.music.entities.user.ERole;
import com.reproductor.music.entities.user.Role;
import com.reproductor.music.repositories.RoleRepository;
import com.reproductor.music.repositories.UserRepository;
import com.reproductor.music.security.JwtTokenProvider;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
@AllArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;
    private final PasswordEncoder encoder;
    private final RoleRepository roleRepository;
    @Override
    @Transactional
    public String login(DTOLogin dtoLogin) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(dtoLogin.getUsername(), dtoLogin.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        return jwtTokenProvider.generateToken(authentication);
    }

    @Override
    @Transactional
    public DTOUserResponse registerUser(DTOUserRegister dtoUserRegister) {
        if (userRepository.existsByUsername(dtoUserRegister.getUsername()) || userRepository.existsByEmail(dtoUserRegister.getEmail())) {
            throw new IllegalArgumentException("username or email address already in use");
        }
        Role userRole = roleRepository.findByName(ERole.ROLE_USER).orElseThrow(
                ()->new IllegalArgumentException("Role Not Found"));

        Users user = Users.builder().email(dtoUserRegister.getEmail())
                .username(dtoUserRegister.getUsername())
                .password(encoder.encode(dtoUserRegister.getPassword()))
                .roles(Set.of(userRole))
                .build();

        userRepository.save(user);

        return DTOUserResponse.builder().username(user.getUsername()).email(user.getEmail()).build();
    }
}
