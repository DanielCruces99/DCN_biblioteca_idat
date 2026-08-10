package com.biblioteca_idat_api.biblioteca_idat_api.service;
import org.springframework.stereotype.Service;
import com.biblioteca_idat_api.biblioteca_idat_api.dto.LoginRequestDTO;
import com.biblioteca_idat_api.biblioteca_idat_api.dto.LoginResponseDTO;
import com.biblioteca_idat_api.biblioteca_idat_api.dto.RefreshRequestDTO;
import com.biblioteca_idat_api.biblioteca_idat_api.entity.RefreshToken;
import com.biblioteca_idat_api.biblioteca_idat_api.entity.Usuario;
import com.biblioteca_idat_api.biblioteca_idat_api.exception.TokenInvalidoException;
import com.biblioteca_idat_api.biblioteca_idat_api.repository.RefreshTokenRepository;
import com.biblioteca_idat_api.biblioteca_idat_api.security.JwtService;
import com.biblioteca_idat_api.biblioteca_idat_api.security.UserPrincipal;
import io.jsonwebtoken.JwtException;
import jakarta.transaction.Transactional;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
/*import org.springframework.stereotype.Service;*/


import java.time.LocalDateTime;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final RefreshTokenRepository refreshTokenRepository;

    public AuthService(AuthenticationManager authenticationManager,
                       JwtService jwtService,
                       RefreshTokenRepository refreshTokenRepository) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.refreshTokenRepository = refreshTokenRepository;
    }

    @Transactional
    public LoginResponseDTO login(LoginRequestDTO request) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password()));

        UserPrincipal userPrincipal = (UserPrincipal) auth.getPrincipal();

        String accessToken = jwtService.generateAccessToken(userPrincipal);
        String refreshToken = jwtService.generateRefreshToken(userPrincipal);

        guardarRefreshToken(userPrincipal.getUsuario(), refreshToken);

        return new LoginResponseDTO(accessToken, refreshToken, "Bearer");
    }

    @Transactional
    public LoginResponseDTO refrescarToken(RefreshRequestDTO request) {
        RefreshToken guardado = refreshTokenRepository.findByToken(request.refreshToken())
                .orElseThrow(() -> new TokenInvalidoException("Refresh token no reconocido"));

        if (guardado.isRevocado() || guardado.getFechaExpiracion().isBefore(LocalDateTime.now())) {
            throw new TokenInvalidoException("Refresh token expirado o revocado");
        }

        Usuario usuario = guardado.getUsuario();
        UserPrincipal userPrincipal = new UserPrincipal(usuario);

        try {
            if (!jwtService.isRefreshTokenValid(request.refreshToken(), usuario.getUsername())) {
                throw new TokenInvalidoException("El token proporcionado no es un refresh token válido");
            }
        } catch (JwtException | IllegalArgumentException ex) {
            throw new TokenInvalidoException("Refresh token inválido");
        }

        String nuevoAccessToken = jwtService.generateAccessToken(userPrincipal);
        String nuevoRefreshToken = jwtService.generateRefreshToken(userPrincipal);

        // Rotación: revoca el token usado y guarda uno nuevo
        guardado.setRevocado(true);
        refreshTokenRepository.save(guardado);
        guardarRefreshToken(usuario, nuevoRefreshToken);

        return new LoginResponseDTO(nuevoAccessToken, nuevoRefreshToken, "Bearer");
    }

    @Transactional
    public void logout(String refreshToken) {
        refreshTokenRepository.findByToken(refreshToken)
                .ifPresent(rt -> {
                    rt.setRevocado(true);
                    refreshTokenRepository.save(rt);
                });
    }

    private void guardarRefreshToken(Usuario usuario, String token) {
        RefreshToken rt = new RefreshToken();
        rt.setUsuario(usuario);
        rt.setToken(token);
        rt.setFechaExpiracion(jwtService.extractExpirationDateTime(token));
        rt.setRevocado(false);
        refreshTokenRepository.save(rt);
    }
}
