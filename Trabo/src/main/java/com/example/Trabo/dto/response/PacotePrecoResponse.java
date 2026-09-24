package com.example.Trabo.dto.response;

public record PacotePrecoResponse(
        Long id,
        String pacote,
        String preco,
        String dias,
        String horario
) {}
