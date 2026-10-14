package com.example.Trabo.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Dados para cadastro de novo usuário cliente")
public record RegistroUsuarioRequest(
        @Schema(description = "Nome completo do usuário", example = "Maria Silva")
        @NotBlank String nome,

        @Schema(description = "E-mail único de acesso", example = "maria.silva@email.com")
        @Email @NotBlank String email,

        @Schema(description = "Senha com no mínimo 6 caracteres", example = "senha123", minLength = 6)
        @NotBlank @Size(min = 6) String senha
) {}
