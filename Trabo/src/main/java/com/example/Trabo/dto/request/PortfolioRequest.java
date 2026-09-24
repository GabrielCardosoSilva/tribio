package com.example.Trabo.dto.request;

import jakarta.validation.constraints.NotBlank;
import java.util.List;

public record PortfolioRequest(
        @NotBlank String descricao,
        @NotBlank String estado,
        @NotBlank String cidade,
        String enderecoCompleto,
        @NotBlank String whatsapp,
        String emailContato,
        List<ServicoRequest> servicos,
        List<PacotePrecoRequest> pacotes,
        List<String> galeria,
        Boolean visivel
) {}
