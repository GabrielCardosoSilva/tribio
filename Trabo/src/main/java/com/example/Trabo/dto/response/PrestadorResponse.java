package com.example.Trabo.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Perfil completo do prestador com portfólio, serviços e avaliações")
public record PrestadorResponse(
        @Schema(example = "1") Long id,
        @Schema(example = "Carlos Eletricista") String nome,
        @Schema(example = "carlos@teste.com") String email,
        @Schema(example = "contato@carlos.com.br") String emailContato,
        @Schema(example = "Campinas") String cidade,
        @Schema(example = "SP") String estado,
        @Schema(example = "Cambuí") String bairro,
        @Schema(example = "Rua das Flores, 123") String enderecoCompleto,
        @Schema(example = "Especialista em instalações elétricas e reformas residenciais.") String descricao,
        @Schema(example = "19999999999") String telefone,
        @Schema(example = "19999999999") String whatsapp,
        @Schema(example = "/api/arquivos/avatar.png") String fotoPerfil,
        @Schema(example = "true") boolean aprovado,
        @Schema(example = "4.8") Double mediaAvaliacoes,
        @Schema(example = "12") int totalAvaliacoes,
        List<CategoriaResponse> categorias,
        List<String> galeria,
        List<ServicoResponse> servicos,
        List<PacotePrecoResponse> pacotesPrecos,
        @Schema(example = "true") boolean visivel,
        @Schema(example = "#2D6A4F") String corPerfil
) {}
