package com.example.Trabo.dto.request;

import jakarta.validation.constraints.NotBlank;

import java.util.List;

public record AtualizarPrestadorRequest(
        @NotBlank String cidade,
        @NotBlank String estado,
        String bairro,
        String descricao,
        String telefone,
        String whatsapp,
        List<Long> categoriaIds
) {}
