package com.example.Trabo.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

import java.util.List;

@Schema(description = "Dados completos para atualização do portfólio do prestador")
public record PortfolioRequest(
        @Schema(description = "Texto 'Sobre' ou biografia profissional", example = "Especialista em instalações elétricas com mais de 10 anos de experiência.")
        @NotBlank String descricao,

        @Schema(description = "Sigla do estado (UF)", example = "SP")
        @NotBlank String estado,

        @Schema(description = "Cidade principal de atuação", example = "Campinas")
        @NotBlank String cidade,

        @Schema(description = "Endereço completo (opcional)", example = "Rua das Flores, 123 - Centro")
        String enderecoCompleto,

        @Schema(description = "WhatsApp para contato direto de clientes", example = "(19) 99999-9999")
        @NotBlank String whatsapp,

        @Schema(description = "E-mail profissional para contato", example = "contato@carlos.com.br")
        String emailContato,

        @Schema(description = "Lista de serviços oferecidos")
        List<ServicoRequest> servicos,

        @Schema(description = "Tabela de preços e horários")
        List<PacotePrecoRequest> pacotes,

        @Schema(description = "Lista de URLs das fotos do portfólio")
        List<String> galeria,

        @Schema(description = "Se o perfil está visível publicamente na busca", example = "true")
        Boolean visivel,

        @Schema(description = "Cor temática do perfil em hexadecimal", example = "#2D6A4F")
        String corPerfil
) {}
