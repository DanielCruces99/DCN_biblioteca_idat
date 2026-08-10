package com.biblioteca_idat_api.biblioteca_idat_api.service;

import com.biblioteca_idat_api.biblioteca_idat_api.dto.UsuarioRequestDTO;
import com.biblioteca_idat_api.biblioteca_idat_api.dto.UsuarioResponseDTO;
import com.biblioteca_idat_api.biblioteca_idat_api.entity.Rol;
import com.biblioteca_idat_api.biblioteca_idat_api.entity.Usuario;
import com.biblioteca_idat_api.biblioteca_idat_api.exception.RecursoDuplicadoException;
import com.biblioteca_idat_api.biblioteca_idat_api.exception.RecursoNoEncontradoException;
import com.biblioteca_idat_api.biblioteca_idat_api.mapper.UsuarioMapper;
import com.biblioteca_idat_api.biblioteca_idat_api.repository.RolRepository;
import com.biblioteca_idat_api.biblioteca_idat_api.repository.UsuarioRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

@Service
public class UsuarioService {

    private static final Set<String> ROLES_PERMITIDOS = Set.of("ROLE_ADMIN", "ROLE_USER");

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository,
                          RolRepository rolRepository,
                          PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.rolRepository = rolRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public UsuarioResponseDTO crear(UsuarioRequestDTO request) {
        String username = request.username().trim();
        String email = request.email().trim().toLowerCase(Locale.ROOT);

        if (usuarioRepository.existsByUsername(username)) {
            throw new RecursoDuplicadoException("El username ya está registrado");
        }
        if (usuarioRepository.existsByEmail(email)) {
            throw new RecursoDuplicadoException("El email ya está registrado");
        }

        Usuario usuario = UsuarioMapper.toEntity(request);
        usuario.setUsername(username);
        usuario.setNombre(request.nombre().trim());
        usuario.setEmail(email);
        usuario.setPassword(passwordEncoder.encode(request.password()));
        usuario.setActivo(true);
        usuario.setRoles(resolverRoles(request.roles()));

        return UsuarioMapper.toDTO(usuarioRepository.save(usuario));
    }

    @Transactional
    public List<UsuarioResponseDTO> listar() {
        return usuarioRepository.findAll().stream()
                .map(UsuarioMapper::toDTO)
                .toList();
    }

    @Transactional
    public UsuarioResponseDTO buscarPorId(Long id) {
        return UsuarioMapper.toDTO(obtenerUsuario(id));
    }

    @Transactional
    public UsuarioResponseDTO cambiarEstado(Long id, boolean activo) {
        Usuario usuario = obtenerUsuario(id);
        usuario.setActivo(activo);
        return UsuarioMapper.toDTO(usuarioRepository.save(usuario));
    }

    private Usuario obtenerUsuario(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado: " + id));
    }

    private Set<Rol> resolverRoles(Set<String> rolesSolicitados) {
        Set<String> nombres = rolesSolicitados == null || rolesSolicitados.isEmpty()
                ? Set.of("ROLE_USER")
                : normalizarRoles(rolesSolicitados);

        Set<Rol> roles = new LinkedHashSet<>();
        for (String nombre : nombres) {
            roles.add(rolRepository.findByNombre(nombre)
                    .orElseThrow(() -> new RecursoNoEncontradoException(
                            "El rol no está configurado en la base de datos: " + nombre)));
        }
        return roles;
    }

    private Set<String> normalizarRoles(Set<String> rolesSolicitados) {
        Set<String> normalizados = new LinkedHashSet<>();
        for (String rol : rolesSolicitados) {
            if (rol == null || rol.isBlank()) {
                throw new IllegalArgumentException("Los roles no pueden estar vacíos");
            }
            String nombre = rol.trim().toUpperCase(Locale.ROOT);
            if (!nombre.startsWith("ROLE_")) {
                nombre = "ROLE_" + nombre;
            }
            if (!ROLES_PERMITIDOS.contains(nombre)) {
                throw new IllegalArgumentException("Rol no permitido: " + rol);
            }
            normalizados.add(nombre);
        }
        return normalizados;
    }
}
