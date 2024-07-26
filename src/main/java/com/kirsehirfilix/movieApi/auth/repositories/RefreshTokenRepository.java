package com.kirsehirfilix.movieApi.auth.repositories;

import com.kirsehirfilix.movieApi.auth.entities.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken,Integer>{
    Optional<RefreshToken> findByRefreshToken(String refreshToken);

}
