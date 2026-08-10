package com.biblioteca_idat_api.biblioteca_idat_api.repository;

import com.biblioteca_idat_api.biblioteca_idat_api.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);
}