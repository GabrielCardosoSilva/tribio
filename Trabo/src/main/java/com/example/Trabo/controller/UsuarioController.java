package com.example.Trabo.controller;

import com.example.Trabo.exception.NotFoundException;
import com.example.Trabo.model.entity.Usuario;
import com.example.Trabo.repository.UsuarioRepository;
import com.example.Trabo.service.FileService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final UsuarioRepository usuarioRepository;
    private final FileService fileService;

    public UsuarioController(UsuarioRepository usuarioRepository, FileService fileService) {
        this.usuarioRepository = usuarioRepository;
        this.fileService = fileService;
    }

    @GetMapping("/me")
    public ResponseEntity<Map<String, Object>> getMe() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Usuario u = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("Usuário não encontrado"));

        return ResponseEntity.ok(Map.of(
                "id", u.getId(),
                "nome", u.getNome(),
                "email", u.getEmail(),
                "role", u.getRole(),
                "fotoPerfil", u.getFotoPerfil() != null ? u.getFotoPerfil() : ""
        ));
    }

    @PostMapping("/perfil/foto")
    public ResponseEntity<Map<String, String>> uploadFotoPerfil(@RequestParam("file") MultipartFile file) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Usuario u = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("Usuário não encontrado"));

        String url = fileService.salvarArquivo(file);
        u.setFotoPerfil(url);
        usuarioRepository.save(u);

        return ResponseEntity.ok(Map.of("url", url));
    }
}
