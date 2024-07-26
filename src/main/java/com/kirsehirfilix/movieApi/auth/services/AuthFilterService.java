package com.kirsehirfilix.movieApi.auth.services;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Service;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Service // Bu sınıfın bir Spring hizmet bileşeni olduğunu belirtir.
public class AuthFilterService extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    // Yapılandırıcı, JwtService ve UserDetailsService bağımlılıklarını enjekte eder
    public AuthFilterService(JwtService jwtService, UserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        // İstek başlığından "Authorization" başlığını alır
        final String authHeader = request.getHeader("Authorization");
        String jwt;
        String username;

        // Eğer Authorization başlığı yoksa veya Bearer ile başlamıyorsa filtreleme zincirine devam eder
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // JWT'yi Authorization başlığından çıkarır (Bearer token kısmını kaldırır)
        jwt = authHeader.substring(7);

        // JWT'den kullanıcı adını çıkarır
        username = jwtService.extractUsername(jwt);

        // Kullanıcı adı varsa ve henüz kimlik doğrulama yapılmamışsa devam eder
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            // Kullanıcı ayrıntılarını yükler
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            // JWT'nin geçerli olup olmadığını kontrol eder
            if (jwtService.isTokenValid(jwt, userDetails)) {
                // Kullanıcıyı kimlik doğrulama belirteci oluşturur
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );

                // Kimlik doğrulama ayrıntılarını isteğe göre ayarlar
                authenticationToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );

                // SecurityContextHolder'a kimlik doğrulama belirtecini ayarlar
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
        }

        // Filtreleme zincirine devam eder
        filterChain.doFilter(request, response);
    }
}

