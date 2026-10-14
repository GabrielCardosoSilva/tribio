package com.example.Trabo.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Serviço oferecido pelo prestador")
public record ServicoRequest(
        @Schema(description = "Título ou nome do serviço", example = "Troca de Fiação Residencial")
        @NotBlank(message = "Título é obrigatório")
        String titulo,

        @Schema(description = "Descrição detalhada do que está incluso", example = "Substituição completa da fiação antiga por cabos antichama certificados.")
        String descricao
) {}
