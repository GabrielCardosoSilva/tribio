package com.example.Trabo.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Pacote de preço e disponibilidade de atendimento")
public record PacotePrecoRequest(
        @Schema(description = "Nome do pacote ou serviço tabelado", example = "Visita Técnica e Orçamento")
        String pacote,

        @Schema(description = "Valor formatado ou estimado", example = "R$ 80,00")
        String preco,

        @Schema(description = "Dias de atendimento", example = "Seg a Sáb")
        String dias,

        @Schema(description = "Horário de atendimento", example = "08:00 - 18:00")
        String horario
) {}
