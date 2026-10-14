package com.example.Trabo.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Dados para autenticação")
public record LoginRequest(
        @Schema(description = "E-mail cadastrado", example = "cliente@teste.com")
        @Email @NotBlank String email,

        @Schema(description = "Senha do usuário", example = "senha123")
        @NotBlank String senha
) {}
