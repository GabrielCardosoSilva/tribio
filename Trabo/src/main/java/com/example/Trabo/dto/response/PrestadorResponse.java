package com.example.Trabo.dto.response;

import java.util.List;

public record PrestadorResponse(
        Long id,
        String nome,
        String email,
        String cidade,
        String bairro,
        String descricao,
        String telefone,
        String whatsapp,
        String fotoPerfil,
        boolean aprovado,
        Double mediaAvaliacoes,
        int totalAvaliacoes,
        List<CategoriaResponse> categorias,
        List<String> fotos
) {}
