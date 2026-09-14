package com.example.Trabo.dto.request;

import jakarta.validation.constraints.NotBlank;

public record CategoriaRequest(
        @NotBlank String nome,
        String icone,
        String descricao
) {}
