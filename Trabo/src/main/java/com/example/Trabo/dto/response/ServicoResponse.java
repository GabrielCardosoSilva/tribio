package com.example.Trabo.dto.response;

import java.math.BigDecimal;
import java.util.List;

public record ServicoResponse(
        Long id,
        String titulo,
        String descricao,
        BigDecimal preco,
        List<String> fotos
) {}
