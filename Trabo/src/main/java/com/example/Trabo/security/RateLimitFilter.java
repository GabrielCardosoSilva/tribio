package com.example.Trabo.security;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RateLimitFilter extends OncePerRequestFilter {

    private final Map<String, Bucket> cache = new ConcurrentHashMap<>();
    private final SecurityErrorHandler errorHandler;
    private final int capacity;

    public RateLimitFilter(SecurityErrorHandler errorHandler,
                           @Value("${app.rate-limit.capacity:10}") int capacity) {
        this.errorHandler = errorHandler;
        this.capacity = capacity;
    }

    private Bucket createNewBucket() {
        // Limita a N requisições por minuto por IP para as rotas sensíveis (padrão: 10)
        Bandwidth limit = Bandwidth.builder()
                .capacity(capacity)
                .refillGreedy(capacity, Duration.ofMinutes(1))
                .build();
        return Bucket.builder().addLimit(limit).build();
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();
        
        // Aplica o filtro apenas nas rotas de login e registro (evitar brute-force)
        if (path.startsWith("/api/auth/login") || path.startsWith("/api/auth/registro")) {
            String ip = request.getRemoteAddr();
            Bucket bucket = cache.computeIfAbsent(ip, k -> createNewBucket());

            if (!bucket.tryConsume(1)) {
                errorHandler.write(response, request, HttpStatus.TOO_MANY_REQUESTS,
                        "Muitas tentativas. Aguarde um minuto e tente novamente.");
                return;
            }
        }
        
        filterChain.doFilter(request, response);
    }
}
