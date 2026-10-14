package com.example.Trabo.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Dados para cadastro de novo prestador de serviços")
public record RegistroPrestadorRequest(
        // Dados de usuário
        @Schema(description = "Nome completo ou razão social", example = "Carlos Eletricista")
        @NotBlank String nome,

        @Schema(description = "E-mail único de acesso", example = "carlos.eletro@email.com")
        @Email @NotBlank String email,

        @Schema(description = "Senha com no mínimo 6 caracteres", example = "senha123", minLength = 6)
        @NotBlank @Size(min = 6) String senha,

        // Dados profissionais
        @Schema(description = "Cidade principal de atuação", example = "Campinas")
        @NotBlank String cidade,

        @Schema(description = "Sigla do estado (UF)", example = "SP")
        @NotBlank @Size(min = 2, max = 2) String estado,

        @Schema(description = "Bairro", example = "Cambuí")
        String bairro,

        @Schema(description = "Apresentação e resumo dos serviços prestados", example = "Instalações elétricas residenciais e comerciais há mais de 10 anos.")
        @NotBlank String descricao,

        @Schema(description = "Telefone para contato", example = "19999999999")
        @NotBlank String telefone,

        @Schema(description = "WhatsApp com DDD", example = "19999999999")
        String whatsapp
) {}
