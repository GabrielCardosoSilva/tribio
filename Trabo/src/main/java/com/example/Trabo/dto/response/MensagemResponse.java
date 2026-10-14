package com.example.Trabo.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Mensagem simples de confirmação")
public record MensagemResponse(
        @Schema(example = "Operação realizada com sucesso") String mensagem
) {}
