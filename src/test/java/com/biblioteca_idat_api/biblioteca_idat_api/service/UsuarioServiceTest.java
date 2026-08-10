package com.biblioteca_idat_api.biblioteca_idat_api.service;

import com.biblioteca_idat_api.biblioteca_idat_api.dto.UsuarioRequestDTO;
import com.biblioteca_idat_api.biblioteca_idat_api.entity.Rol;
import com.biblioteca_idat_api.biblioteca_idat_api.entity.Usuario;
import com.biblioteca_idat_api.biblioteca_idat_api.exception.RecursoDuplicadoException;
import com.biblioteca_idat_api.biblioteca_idat_api.repository.RolRepository;
import com.biblioteca_idat_api.biblioteca_idat_api.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
class UsuarioServiceTest {

    @Autowired private UsuarioService usuarioService;
    @Autowired private UsuarioRepository usuarioRepository;
    @Autowired private RolRepository rolRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    @BeforeEach
    void prepararRolBase() {
        if (!rolRepository.existsByNombre("ROLE_USER")) {
            Rol rol = new Rol();
            rol.setNombre("ROLE_USER");
            rolRepository.save(rol);
        }
    }

    @Test
    void debeCrearUsuarioConPasswordCifradaYRolPorDefecto() {
        var request = new UsuarioRequestDTO(
                "lector.test", "Lector de Prueba", "lector@test.com", "claveSegura123", null);

        var response = usuarioService.crear(request);
        Usuario guardado = usuarioRepository.findById(response.id()).orElseThrow();

        assertThat(response.roles()).containsExactly("ROLE_USER");
        assertThat(guardado.getPassword()).isNotEqualTo(request.password());
        assertThat(passwordEncoder.matches(request.password(), guardado.getPassword())).isTrue();
    }

    @Test
    void debeRechazarUsernameDuplicado() {
        var request = new UsuarioRequestDTO(
                "duplicado", "Primer Usuario", "primero@test.com", "claveSegura123", Set.of("USER"));
        usuarioService.crear(request);

        var duplicado = new UsuarioRequestDTO(
                "duplicado", "Segundo Usuario", "segundo@test.com", "otraClave123", Set.of("USER"));

        assertThatThrownBy(() -> usuarioService.crear(duplicado))
                .isInstanceOf(RecursoDuplicadoException.class)
                .hasMessageContaining("username");
    }
}
