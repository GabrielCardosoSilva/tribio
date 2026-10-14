package com.example.Trabo.dto.response;

import com.example.Trabo.model.enums.Role;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Dados do usuário autenticado")
public record PerfilUsuarioResponse(
        @Schema(example = "1") Long id,
        @Schema(example = "Maria Silva") String nome,
        @Schema(example = "maria@email.com") String email,
        @Schema(example = "USUARIO") Role role,
        @Schema(description = "URL da foto de perfil (vazio se não houver)", example = "/api/arquivos/3f2a.jpg") String fotoPerfil
) {}
