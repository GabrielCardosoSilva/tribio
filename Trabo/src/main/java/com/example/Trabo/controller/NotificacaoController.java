package com.example.Trabo.controller;

import com.example.Trabo.model.entity.Notificacao;
import com.example.Trabo.model.entity.Usuario;
import com.example.Trabo.repository.NotificacaoRepository;
import com.example.Trabo.repository.UsuarioRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notificacoes")
@Tag(name = "Notificações")
@PreAuthorize("isAuthenticated()")
@SecurityRequirement(name = "bearerAuth")
public class NotificacaoController {

    private final NotificacaoRepository notificacaoRepository;
    private final UsuarioRepository usuarioRepository;

    public NotificacaoController(NotificacaoRepository notificacaoRepository, UsuarioRepository usuarioRepository) {
        this.notificacaoRepository = notificacaoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @GetMapping
    @Operation(summary = "Lista as notificações do usuário logado")
    public ResponseEntity<List<Notificacao>> listar() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Usuario usuario = usuarioRepository.findByEmail(email).orElseThrow();
        return ResponseEntity.ok(notificacaoRepository.findByUsuarioIdOrderByCreatedAtDesc(usuario.getId()));
    }

    @PutMapping("/lidas")
    @Operation(summary = "Marca todas as notificações do usuário logado como lidas")
    public ResponseEntity<Void> marcarComoLidas() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Usuario usuario = usuarioRepository.findByEmail(email).orElseThrow();
        List<Notificacao> naoLidas = notificacaoRepository.findByUsuarioIdOrderByCreatedAtDesc(usuario.getId())
                .stream().filter(n -> !n.isLida()).toList();
        
        naoLidas.forEach(n -> n.setLida(true));
        notificacaoRepository.saveAll(naoLidas);
        return ResponseEntity.ok().build();
    }
}
