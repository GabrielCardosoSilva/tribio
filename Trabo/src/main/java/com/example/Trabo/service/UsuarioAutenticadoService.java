package com.example.Trabo.service;

import com.example.Trabo.exception.NotFoundException;
import com.example.Trabo.model.entity.Usuario;
import com.example.Trabo.repository.UsuarioRepository;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Centraliza o acesso ao usuário autenticado na requisição atual.
 * Evita espalhar {@code SecurityContextHolder} pelos services/controllers
 * e facilita o mock em testes unitários.
 */
@Service
public class UsuarioAutenticadoService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioAutenticadoService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    /** E-mail do usuário logado, ou vazio se a requisição for anônima. */
    public Optional<String> getEmailOpcional() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth instanceof AnonymousAuthenticationToken) {
            return Optional.empty();
        }
        return Optional.ofNullable(auth.getName());
    }

    /** E-mail do usuário logado. Lança 404 se não houver usuário autenticado. */
    public String getEmail() {
        return getEmailOpcional().orElseThrow(() -> new NotFoundException("Usuário não encontrado"));
    }

    /** Entidade do usuário logado. */
    public Usuario getUsuario() {
        return usuarioRepository.findByEmail(getEmail())
                .orElseThrow(() -> new NotFoundException("Usuário não encontrado"));
    }

    /** Entidade do usuário logado, ou vazio se anônimo. */
    public Optional<Usuario> getUsuarioOpcional() {
        return getEmailOpcional().flatMap(usuarioRepository::findByEmail);
    }
}
