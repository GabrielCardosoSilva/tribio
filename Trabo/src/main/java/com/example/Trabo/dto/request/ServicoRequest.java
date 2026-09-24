package com.example.Trabo.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record ServicoRequest(
        @NotBlank(message = "Título é obrigatório")
        String titulo,
        String descricao
) {}
