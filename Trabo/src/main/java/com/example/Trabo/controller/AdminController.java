package com.example.Trabo.controller;

import com.example.Trabo.dto.response.PrestadorResponse;
import com.example.Trabo.dto.response.UsuarioResponse;
import com.example.Trabo.service.AdminService;
import io.swagger.v3.oas.annotations.Operation;
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
@Tag(name = "Admin")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    // ─── Prestadores ─────────────────────────────────────────────

    @GetMapping("/prestadores")
    @Operation(summary = "Listar prestadores (filtro opcional por status de aprovação)")
    public ResponseEntity<List<PrestadorResponse>> listarPrestadores(
            @RequestParam(required = false) Boolean aprovado) {
        return ResponseEntity.ok(adminService.listarPrestadores(aprovado));
    }

    @PatchMapping("/prestadores/{id}/aprovar")
    @Operation(summary = "Aprovar cadastro de prestador")
    public ResponseEntity<PrestadorResponse> aprovar(@PathVariable Long id) {
        return ResponseEntity.ok(adminService.aprovarPrestador(id));
    }

    @PatchMapping("/prestadores/{id}/reprovar")
    @Operation(summary = "Reprovar cadastro de prestador")
    public ResponseEntity<PrestadorResponse> reprovar(@PathVariable Long id) {
        return ResponseEntity.ok(adminService.reprovarPrestador(id));
    }

    // ─── Usuários ─────────────────────────────────────────────────

    @GetMapping("/usuarios")
    @Operation(summary = "Listar usuários comuns")
    public ResponseEntity<List<UsuarioResponse>> listarUsuarios() {
        return ResponseEntity.ok(adminService.listarUsuarios());
    }

    @PatchMapping("/usuarios/{id}/status")
    @Operation(summary = "Ativar ou desativar usuário")
    public ResponseEntity<UsuarioResponse> alterarStatus(
            @PathVariable Long id,
            @RequestParam boolean ativo) {
        return ResponseEntity.ok(adminService.alterarStatusUsuario(id, ativo));
    }
}
