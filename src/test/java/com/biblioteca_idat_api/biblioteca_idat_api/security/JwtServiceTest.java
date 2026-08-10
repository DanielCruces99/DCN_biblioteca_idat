package com.biblioteca_idat_api.biblioteca_idat_api.security;

import com.biblioteca_idat_api.biblioteca_idat_api.entity.Rol;
import com.biblioteca_idat_api.biblioteca_idat_api.entity.Usuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class JwtServiceTest {

    @Autowired
    private JwtService jwtService;

    private UserPrincipal userPrincipal;

    @BeforeEach
    void configurarUsuario() {
        Rol rol = new Rol();
        rol.setNombre("ROLE_ADMIN");

        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setUsername("admin-test");
        usuario.setPassword("hash-de-prueba");
        usuario.setActivo(true);
        usuario.setRoles(Set.of(rol));

        userPrincipal = new UserPrincipal(usuario);
    }

    @Test
    void accessTokenSoloDebeSerValidoComoAccessToken() {
        String token = jwtService.generateAccessToken(userPrincipal);

        assertThat(jwtService.isAccessTokenValid(token, userPrincipal.getUsername())).isTrue();
        assertThat(jwtService.isRefreshTokenValid(token, userPrincipal.getUsername())).isFalse();
    }

    @Test
    void refreshTokenNoDebeSerValidoComoAccessToken() {
        String token = jwtService.generateRefreshToken(userPrincipal);

        assertThat(jwtService.isRefreshTokenValid(token, userPrincipal.getUsername())).isTrue();
        assertThat(jwtService.isAccessTokenValid(token, userPrincipal.getUsername())).isFalse();
    }
}
