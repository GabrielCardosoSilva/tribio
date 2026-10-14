package com.example.Trabo.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

/**
 * Formato padrão de erro retornado por toda a API.
 * Exemplo: { "error": "Prestador não encontrado", "status": 404, "path": "/api/prestadores/9", "timestamp": "..." }
 */
@Schema(description = "Resposta padrão de erro da API")
public record ErrorResponse(
        @Schema(description = "Mensagem legível descrevendo o erro", example = "Prestador não encontrado: 42")
        String error,

        @Schema(description = "Código HTTP do erro", example = "404")
        int status,

        @Schema(description = "Caminho da requisição que originou o erro", example = "/api/prestadores/42")
        String path,

        @Schema(description = "Momento em que o erro ocorreu", example = "2026-10-13T19:30:00")
        LocalDateTime timestamp
) {
    public static ErrorResponse of(int status, String error, String path) {
        return new ErrorResponse(error, status, path, LocalDateTime.now());
    }
}
