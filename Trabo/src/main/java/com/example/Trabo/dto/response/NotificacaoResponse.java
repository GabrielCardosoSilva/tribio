package com.example.Trabo.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "Notificação recebida pelo usuário")
public record NotificacaoResponse(
        @Schema(example = "10") Long id,
        @Schema(example = "Maria Silva curtiu seu perfil") String mensagem,
        @Schema(description = "Tipo: LIKE, VIEW ou SYSTEM", example = "LIKE") String tipo,
        @Schema(example = "false") boolean lida,
        LocalDateTime createdAt
) {}
