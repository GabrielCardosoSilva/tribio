package com.example.Trabo.dto.request;

public record PacotePrecoRequest(
        String pacote,
        String preco,
        String dias,
        String horario
) {}
