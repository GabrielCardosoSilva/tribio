package com.example.Trabo.dto.response;

import com.example.Trabo.model.enums.Role;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Dados de um usuário para visualização administrativa")
public record UsuarioResponse(
        @Schema(example = "1") Long id,
        @Schema(example = "Maria Silva") String nome,
        @Schema(example = "maria@email.com") String email,
        @Schema(example = "USUARIO") Role role,
        @Schema(example = "true") boolean ativo
) {}
