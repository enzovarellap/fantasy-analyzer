package com.fantasyfootball.fantasy_analyzer.service;

import com.fantasyfootball.fantasy_analyzer.config.JwtUtil;
import com.fantasyfootball.fantasy_analyzer.dto.AuthResponse;
import com.fantasyfootball.fantasy_analyzer.dto.LoginRequest;
import com.fantasyfootball.fantasy_analyzer.dto.RegisterRequest;
import com.fantasyfootball.fantasy_analyzer.model.entity.Usuario;
import com.fantasyfootball.fantasy_analyzer.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    public AuthResponse register(RegisterRequest request) {
        if (usuarioRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email já cadastrado");
        }

        Usuario usuario = Usuario.builder()
                .nome(request.getNome())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .build();

        usuario = usuarioRepository.save(usuario);

        UserDetails userDetails = User.builder()
                .username(usuario.getEmail())
                .password(usuario.getPassword())
                .authorities(Collections.emptyList())
                .build();

        String token = jwtUtil.generateToken(userDetails);

        return AuthResponse.builder()
                .token(token)
                .tipo("Bearer")
                .id(usuario.getId())
                .nome(usuario.getNome())
                .email(usuario.getEmail())
                .build();
    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        Usuario usuario = usuarioRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        UserDetails userDetails = User.builder()
                .username(usuario.getEmail())
                .password(usuario.getPassword())
                .authorities(Collections.emptyList())
                .build();

        String token = jwtUtil.generateToken(userDetails);

        return AuthResponse.builder()
                .token(token)
                .tipo("Bearer")
                .id(usuario.getId())
                .nome(usuario.getNome())
                .email(usuario.getEmail())
                .build();
    }
}
