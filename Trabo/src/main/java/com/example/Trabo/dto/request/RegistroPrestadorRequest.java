package com.example.Trabo.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegistroPrestadorRequest(
        // Dados de usuário
        @NotBlank String nome,
        @Email @NotBlank String email,
        @NotBlank @Size(min = 6) String senha,

        // Dados profissionais
        @NotBlank String cidade,
        @NotBlank @Size(min = 2, max = 2) String estado,
        String bairro,
        @NotBlank String descricao,
        @NotBlank String telefone,
        String whatsapp
) {}
