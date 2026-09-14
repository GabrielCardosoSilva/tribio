package com.example.Trabo.dto.response;

import com.example.Trabo.model.enums.Role;

public record UsuarioResponse(
        Long id,
        String nome,
        String email,
        Role role,
        boolean ativo
) {}
