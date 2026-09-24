package com.example.Trabo.dto.response;

import java.util.List;

public record PrestadorResponse(
        Long id,
        String nome,
        String email,
        String emailContato,
        String cidade,
        String estado,
        String bairro,
        String enderecoCompleto,
        String descricao,
        String telefone,
        String whatsapp,
        String fotoPerfil,
        boolean aprovado,
        Double mediaAvaliacoes,
        int totalAvaliacoes,
        List<CategoriaResponse> categorias,
        List<String> galeria,
        List<ServicoResponse> servicos,
        List<PacotePrecoResponse> pacotesPrecos,
        boolean visivel
) {}
