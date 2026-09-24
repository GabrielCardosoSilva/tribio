package com.example.Trabo.controller;


import com.example.Trabo.dto.request.AvaliacaoRequest;
import com.example.Trabo.dto.response.AvaliacaoResponse;
import com.example.Trabo.dto.response.PrestadorResponse;
import com.example.Trabo.service.AvaliacaoService;
import com.example.Trabo.service.PrestadorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/prestadores")
@Tag(name = "Prestadores")
public class PrestadorController {

    private final PrestadorService prestadorService;
    private final AvaliacaoService avaliacaoService;

    public PrestadorController(PrestadorService prestadorService, AvaliacaoService avaliacaoService) {
        this.prestadorService = prestadorService;
        this.avaliacaoService = avaliacaoService;
    }

    @GetMapping
    @Operation(summary = "Busca pública de prestadores")
    public ResponseEntity<List<PrestadorResponse>> buscar(
            @RequestParam(required = false) Long categoriaId,
            @RequestParam(required = false) String cidade,
            @RequestParam(required = false) String estado,
            @RequestParam(required = false) String texto) {
        return ResponseEntity.ok(prestadorService.buscarPublico(categoriaId, cidade, estado, texto));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Perfil público do prestador")
    public ResponseEntity<PrestadorResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(prestadorService.buscarPorId(id));
    }

    @GetMapping("/{id}/avaliacoes")
    @Operation(summary = "Listar avaliações de um prestador")
    public ResponseEntity<List<AvaliacaoResponse>> listarAvaliacoes(@PathVariable Long id) {
        return ResponseEntity.ok(avaliacaoService.listarPorPrestador(id));
    }

    @PostMapping("/{id}/avaliacoes")
    @PreAuthorize("hasRole('USUARIO')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Avaliar um prestador (requer login como USUARIO)")
    public ResponseEntity<AvaliacaoResponse> avaliar(
            @PathVariable Long id,
            @Valid @RequestBody AvaliacaoRequest request) {
        return ResponseEntity.ok(avaliacaoService.avaliar(id, request));
    }

    @PostMapping("/{id}/favoritar")
    @PreAuthorize("hasAnyRole('USUARIO', 'PRESTADOR', 'ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Favoritar ou desfavoritar um prestador")
    public ResponseEntity<Void> favoritar(@PathVariable Long id) {
        prestadorService.favoritar(id);
        return ResponseEntity.ok().build();
    }



    @PostMapping("/me/portfolio")
    @PreAuthorize("hasRole('PRESTADOR')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Salvar todo o portfólio (requer login como PRESTADOR)")
    public ResponseEntity<PrestadorResponse> atualizarPortfolio(
            @Valid @RequestBody com.example.Trabo.dto.request.PortfolioRequest request) {
        return ResponseEntity.ok(prestadorService.atualizarPortfolio(request));
    }
}
