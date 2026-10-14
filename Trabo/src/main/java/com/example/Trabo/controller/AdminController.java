package com.example.Trabo.controller;

import com.example.Trabo.dto.response.ErrorResponse;
import com.example.Trabo.dto.response.PrestadorResponse;
import com.example.Trabo.dto.response.UsuarioResponse;
import com.example.Trabo.service.AdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Admin", description = "Endpoints administrativos para moderação de prestadores e gerenciamento de usuários")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    // ─── Prestadores ─────────────────────────────────────────────

    @GetMapping("/prestadores")
    @Operation(summary = "Listar prestadores", description = "Retorna prestadores cadastrados com filtro opcional por status de aprovação.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de prestadores",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = PrestadorResponse.class)))),
            @ApiResponse(responseCode = "401", description = "Não autenticado",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Acesso proibido (requer ADMIN)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<List<PrestadorResponse>> listarPrestadores(
            @Parameter(description = "Filtro por status de aprovação (true = aprovado, false = pendente/reprovado)")
            @RequestParam(required = false) Boolean aprovado) {
        return ResponseEntity.ok(adminService.listarPrestadores(aprovado));
    }

    @PatchMapping("/prestadores/{id}/aprovar")
    @Operation(summary = "Aprovar cadastro de prestador", description = "Aprova o cadastro do prestador tornando seu perfil visível na plataforma.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Prestador aprovado com sucesso",
                    content = @Content(schema = @Schema(implementation = PrestadorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Não autenticado",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Acesso proibido (requer ADMIN)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Prestador não encontrado",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<PrestadorResponse> aprovar(
            @Parameter(description = "ID do prestador", example = "1") @PathVariable Long id) {
        return ResponseEntity.ok(adminService.aprovarPrestador(id));
    }

    @PatchMapping("/prestadores/{id}/reprovar")
    @Operation(summary = "Reprovar cadastro de prestador", description = "Desaprova o prestador ocultando seu perfil da busca.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Prestador reprovado com sucesso",
                    content = @Content(schema = @Schema(implementation = PrestadorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Não autenticado",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Acesso proibido (requer ADMIN)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Prestador não encontrado",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<PrestadorResponse> reprovar(
            @Parameter(description = "ID do prestador", example = "1") @PathVariable Long id) {
        return ResponseEntity.ok(adminService.reprovarPrestador(id));
    }

    // ─── Usuários ─────────────────────────────────────────────────

    @GetMapping("/usuarios")
    @Operation(summary = "Listar usuários comuns", description = "Retorna todos os usuários cadastrados como clientes.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de usuários",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = UsuarioResponse.class)))),
            @ApiResponse(responseCode = "401", description = "Não autenticado",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Acesso proibido (requer ADMIN)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<List<UsuarioResponse>> listarUsuarios() {
        return ResponseEntity.ok(adminService.listarUsuarios());
    }

    @PatchMapping("/usuarios/{id}/status")
    @Operation(summary = "Ativar ou desativar usuário", description = "Permite suspender ou reativar o acesso de um usuário ao sistema.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Status atualizado com sucesso",
                    content = @Content(schema = @Schema(implementation = UsuarioResponse.class))),
            @ApiResponse(responseCode = "401", description = "Não autenticado",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Acesso proibido (requer ADMIN)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<UsuarioResponse> alterarStatus(
            @Parameter(description = "ID do usuário", example = "1") @PathVariable Long id,
            @Parameter(description = "Novo status (true = ativo, false = inativo)") @RequestParam boolean ativo) {
        return ResponseEntity.ok(adminService.alterarStatusUsuario(id, ativo));
    }
}
