package com.example.Trabo.dto.response;

import java.time.LocalDateTime;

public record AvaliacaoResponse(
        Long id,
        String nomeUsuario,
        Integer nota,
        String comentario,
        LocalDateTime createdAt
) {}
