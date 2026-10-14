package com.example.Trabo.controller;

import com.example.Trabo.dto.response.ErrorResponse;
import com.example.Trabo.dto.response.MensagemResponse;
import com.example.Trabo.dto.response.PerfilUsuarioResponse;
import com.example.Trabo.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/usuarios")
@Tag(name = "Usuários", description = "Endpoints de gerenciamento do perfil do usuário autenticado e conformidade LGPD")
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("isAuthenticated()")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping("/me")
    @Operation(summary = "Retorna dados do usuário autenticado", description = "Recupera as informações cadastrais e permissões do usuário logado.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Dados do usuário recuperados com sucesso",
                    content = @Content(schema = @Schema(implementation = PerfilUsuarioResponse.class))),
            @ApiResponse(responseCode = "401", description = "Não autenticado ou token inválido",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<PerfilUsuarioResponse> getMe(HttpServletRequest request) {
        return ResponseEntity.ok(usuarioService.getMe(request.getRemoteAddr()));
    }

    @PostMapping(value = "/perfil/foto", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload de foto de perfil", description = "Envia uma nova foto para o avatar do usuário logado.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Foto atualizada com sucesso",
                    content = @Content(schema = @Schema(example = "{\"url\": \"/api/arquivos/avatar.png\"}"))),
            @ApiResponse(responseCode = "400", description = "Arquivo vazio ou inválido",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Não autenticado",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<Map<String, String>> uploadFotoPerfil(@RequestParam("file") MultipartFile file,
                                                                 HttpServletRequest request) {
        String url = usuarioService.uploadFotoPerfil(file, request.getRemoteAddr());
        return ResponseEntity.ok(Map.of("url", url));
    }

    /**
     * Exclusão de conta — Direito ao esquecimento (LGPD Art. 18, VI).
     * Remove todos os dados pessoais do usuário e do prestador associado (se houver).
     */
    @DeleteMapping("/me")
    @Operation(summary = "Excluir conta (direito ao esquecimento — LGPD Art. 18)",
            description = "Exclui permanentemente todos os dados pessoais do usuário e vínculo de prestador.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Conta excluída com sucesso",
                    content = @Content(schema = @Schema(implementation = MensagemResponse.class))),
            @ApiResponse(responseCode = "401", description = "Não autenticado",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<MensagemResponse> excluirConta(HttpServletRequest request) {
        usuarioService.excluirConta(request.getRemoteAddr());
        return ResponseEntity.ok(new MensagemResponse("Conta excluída com sucesso. Seus dados foram removidos conforme a LGPD."));
    }
}
