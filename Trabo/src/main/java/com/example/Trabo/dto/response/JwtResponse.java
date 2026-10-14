package com.example.Trabo.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Resposta de autenticação contendo o token JWT")
public record JwtResponse(
        @Schema(description = "Token JWT assinado", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...") String token,
        @Schema(description = "Tipo do token", example = "Bearer") String tipo,
        @Schema(description = "Nome do usuário autenticado", example = "Maria Silva") String nome,
        @Schema(description = "Perfil de acesso (USUARIO, PRESTADOR, ADMIN)", example = "USUARIO") String role
) {
    public JwtResponse(String token, String nome, String role) {
        this(token, "Bearer", nome, role);
    }
}
