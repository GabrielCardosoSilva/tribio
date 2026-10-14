package com.example.Trabo.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Item da tabela de preços e horários")
public record PacotePrecoResponse(
        @Schema(example = "1") Long id,
        @Schema(example = "Visita e Diagnóstico") String pacote,
        @Schema(example = "R$ 100,00") String preco,
        @Schema(example = "Seg a Sex") String dias,
        @Schema(example = "08:00 - 18:00") String horario
) {}
