package com.example.Trabo.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Dados de um serviço do portfólio")
public record ServicoResponse(
        @Schema(example = "1") Long id,
        @Schema(example = "Instalação de Chuveiro Elétrico") String titulo,
        @Schema(example = "Instalação com verificação de disjuntores e teste de segurança.") String descricao
) {}
