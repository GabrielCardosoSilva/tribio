package com.example.Trabo.service;

import com.example.Trabo.dto.response.PrestadorResponse;
import com.example.Trabo.dto.response.UsuarioResponse;
import com.example.Trabo.exception.NotFoundException;
import com.example.Trabo.model.entity.Prestador;
import com.example.Trabo.model.entity.Usuario;
import com.example.Trabo.model.enums.Role;
import com.example.Trabo.repository.PrestadorRepository;
import com.example.Trabo.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AdminService {

    private final PrestadorRepository prestadorRepository;
    private final UsuarioRepository usuarioRepository;
    private final PrestadorService prestadorService;

    public AdminService(PrestadorRepository prestadorRepository,
                        UsuarioRepository usuarioRepository,
                        PrestadorService prestadorService) {
        this.prestadorRepository = prestadorRepository;
        this.usuarioRepository = usuarioRepository;
        this.prestadorService = prestadorService;
    }

    // ─── Prestadores ──────────────────────────────────────────────

    public List<PrestadorResponse> listarPrestadores(Boolean aprovado) {
        List<Prestador> lista = (aprovado != null)
                ? prestadorRepository.findByAprovado(aprovado)
                : prestadorRepository.findAll();
        return lista.stream().map(prestadorService::toResponse).toList();
    }

    @Transactional
    public PrestadorResponse aprovarPrestador(Long id) {
        Prestador p = prestadorRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Prestador não encontrado: " + id));
        p.setAprovado(true);
        return prestadorService.toResponse(prestadorRepository.save(p));
    }

    @Transactional
    public PrestadorResponse reprovarPrestador(Long id) {
        Prestador p = prestadorRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Prestador não encontrado: " + id));
        p.setAprovado(false);
        return prestadorService.toResponse(prestadorRepository.save(p));
    }

    // ─── Usuários ────────────────────────────────────────────────

    public List<UsuarioResponse> listarUsuarios() {
        return usuarioRepository.findByRole(Role.USUARIO)
                .stream().map(this::toUsuarioResponse).toList();
    }

    @Transactional
    public UsuarioResponse alterarStatusUsuario(Long id, boolean ativo) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Usuário não encontrado: " + id));
        usuario.setAtivo(ativo);
        return toUsuarioResponse(usuarioRepository.save(usuario));
    }

    private UsuarioResponse toUsuarioResponse(Usuario u) {
        return new UsuarioResponse(u.getId(), u.getNome(), u.getEmail(), u.getRole(), u.isAtivo());
    }
}
