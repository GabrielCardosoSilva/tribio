package com.example.Trabo.controller;

import com.example.Trabo.dto.request.ServicoRequest;
import com.example.Trabo.dto.response.ServicoResponse;
import com.example.Trabo.service.ServicoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/servicos")
public class ServicoController {

    private final ServicoService servicoService;

    public ServicoController(ServicoService servicoService) {
        this.servicoService = servicoService;
    }

    @GetMapping("/meus")
    @PreAuthorize("hasRole('PRESTADOR')")
    public ResponseEntity<List<ServicoResponse>> listarMeusServicos() {
        return ResponseEntity.ok(servicoService.listarMeusServicos());
    }

    @PostMapping
    @PreAuthorize("hasRole('PRESTADOR')")
    public ResponseEntity<ServicoResponse> criar(@Valid @RequestBody ServicoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(servicoService.criar(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('PRESTADOR')")
    public ResponseEntity<ServicoResponse> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody ServicoRequest request) {
        return ResponseEntity.ok(servicoService.atualizar(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('PRESTADOR')")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        servicoService.excluir(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/fotos")
    @PreAuthorize("hasRole('PRESTADOR')")
    public ResponseEntity<ServicoResponse> uploadFotoServico(
            @PathVariable Long id,
            @RequestParam("file") org.springframework.web.multipart.MultipartFile file) {
        return ResponseEntity.ok(servicoService.adicionarFoto(id, file));
    }
}
