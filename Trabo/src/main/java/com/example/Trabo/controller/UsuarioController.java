package com.example.Trabo.controller;

import com.example.Trabo.exception.NotFoundException;
import com.example.Trabo.model.entity.Usuario;
import com.example.Trabo.repository.PrestadorRepository;
import com.example.Trabo.repository.UsuarioRepository;
import com.example.Trabo.service.AuditoriaService;
import com.example.Trabo.service.FileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/usuarios")
@Tag(name = "Usuários")
public class UsuarioController {

    private final UsuarioRepository usuarioRepository;
    private final PrestadorRepository prestadorRepository;
    private final FileService fileService;
    private final AuditoriaService auditoriaService;

    public UsuarioController(UsuarioRepository usuarioRepository,
                             PrestadorRepository prestadorRepository,
                             FileService fileService,
                             AuditoriaService auditoriaService) {
        this.usuarioRepository = usuarioRepository;
        this.prestadorRepository = prestadorRepository;
        this.fileService = fileService;
        this.auditoriaService = auditoriaService;
    }

    @GetMapping("/me")
    @Operation(summary = "Retorna dados do usuário autenticado")
    public ResponseEntity<Map<String, Object>> getMe(HttpServletRequest request) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Usuario u = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("Usuário não encontrado"));

        auditoriaService.registrar(email, "ACESSO_DADOS_PESSOAIS", "GET /api/usuarios/me", request.getRemoteAddr());

        return ResponseEntity.ok(Map.of(
                "id", u.getId(),
                "nome", u.getNome(),
                "email", u.getEmail(),
                "role", u.getRole(),
                "fotoPerfil", u.getFotoPerfil() != null ? u.getFotoPerfil() : ""
        ));
    }

    @PostMapping("/perfil/foto")
    @Operation(summary = "Upload de foto de perfil")
    public ResponseEntity<Map<String, String>> uploadFotoPerfil(@RequestParam("file") MultipartFile file,
                                                                 HttpServletRequest request) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Usuario u = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("Usuário não encontrado"));

        String url = fileService.salvarArquivo(file);
        u.setFotoPerfil(url);
        usuarioRepository.save(u);

        auditoriaService.registrar(email, "UPLOAD_FOTO_PERFIL", "Foto atualizada: " + url, request.getRemoteAddr());

        return ResponseEntity.ok(Map.of("url", url));
    }

    /**
     * Exclusão de conta — Direito ao esquecimento (LGPD Art. 18, VI).
     * Remove todos os dados pessoais do usuário e do prestador associado (se houver).
     */
    @DeleteMapping("/me")
    @Transactional
    @Operation(summary = "Excluir conta (direito ao esquecimento — LGPD Art. 18)")
    public ResponseEntity<String> excluirConta(HttpServletRequest request) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Usuario u = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("Usuário não encontrado"));

        // Registra auditoria ANTES de deletar (para manter o histórico)
        auditoriaService.registrar(email, "EXCLUSAO_CONTA",
                "Conta excluída a pedido do usuário (LGPD Art. 18)", request.getRemoteAddr());

        // Remove prestador associado (cascade remove os serviços, fotos, avaliações)
        if (u.getPrestador() != null) {
            prestadorRepository.delete(u.getPrestador());
        }

        // Remove o usuário
        usuarioRepository.delete(u);

        return ResponseEntity.ok("Conta excluída com sucesso. Seus dados foram removidos conforme a LGPD.");
    }
}
