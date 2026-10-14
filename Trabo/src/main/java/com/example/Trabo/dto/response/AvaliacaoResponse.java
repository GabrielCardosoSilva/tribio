package com.example.Trabo.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "Dados de uma avaliação de prestador")
public record AvaliacaoResponse(
        @Schema(example = "1") Long id,
        @Schema(example = "Maria Silva") String nomeUsuario,
        @Schema(example = "5") Integer nota,
        @Schema(example = "Serviço excelente e dentro do prazo!") String comentario,
        LocalDateTime createdAt
) {}
