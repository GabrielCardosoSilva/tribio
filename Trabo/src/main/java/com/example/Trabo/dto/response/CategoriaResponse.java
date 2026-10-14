package com.example.Trabo.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Dados de uma categoria de serviços")
public record CategoriaResponse(
        @Schema(example = "1") Long id,
        @Schema(example = "Eletricista") String nome,
        @Schema(example = "⚡") String icone,
        @Schema(example = "Instalações elétricas, quadros de força, tomadas.") String descricao
) {}
