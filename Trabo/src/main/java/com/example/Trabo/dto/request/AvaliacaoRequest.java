package com.example.Trabo.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Dados para envio de avaliação de um prestador")
public record AvaliacaoRequest(
        @Schema(description = "Nota dada ao prestador de 1 a 5 estrelas", example = "5", minimum = "1", maximum = "5")
        @NotNull @Min(1) @Max(5) Integer nota,

        @Schema(description = "Comentário sobre o serviço realizado", example = "Excelente profissional, pontual e muito caprichoso!")
        String comentario
) {}
