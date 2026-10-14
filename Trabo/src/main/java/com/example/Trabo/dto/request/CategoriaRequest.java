package com.example.Trabo.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Dados para criação de uma categoria de serviços")
public record CategoriaRequest(
        @Schema(description = "Nome da categoria", example = "Eletricista")
        @NotBlank String nome,

        @Schema(description = "Ícone ou emoji representativo", example = "⚡")
        String icone,

        @Schema(description = "Descrição dos tipos de serviço incluídos", example = "Instalações elétricas, quadros de força, tomadas e chuveiros.")
        String descricao
) {}
