package com.example.Trabo.service;

import com.example.Trabo.dto.response.NotificacaoResponse;
import com.example.Trabo.model.entity.Notificacao;
import com.example.Trabo.model.entity.Usuario;
import com.example.Trabo.repository.NotificacaoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class NotificacaoService {

    private final NotificacaoRepository notificacaoRepository;
    private final UsuarioAutenticadoService usuarioAutenticadoService;

    public NotificacaoService(NotificacaoRepository notificacaoRepository,
                              UsuarioAutenticadoService usuarioAutenticadoService) {
        this.notificacaoRepository = notificacaoRepository;
        this.usuarioAutenticadoService = usuarioAutenticadoService;
    }

    public List<NotificacaoResponse> listar() {
        Usuario usuario = usuarioAutenticadoService.getUsuario();
        return notificacaoRepository.findByUsuarioIdOrderByCreatedAtDesc(usuario.getId())
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public void marcarComoLidas() {
        Usuario usuario = usuarioAutenticadoService.getUsuario();
        List<Notificacao> naoLidas = notificacaoRepository.findByUsuarioIdOrderByCreatedAtDesc(usuario.getId())
                .stream()
                .filter(n -> !n.isLida())
                .toList();

        naoLidas.forEach(n -> n.setLida(true));
        notificacaoRepository.saveAll(naoLidas);
    }

    public NotificacaoResponse toResponse(Notificacao n) {
        return new NotificacaoResponse(
                n.getId(),
                n.getMensagem(),
                n.getTipo(),
                n.isLida(),
                n.getCreatedAt()
        );
    }
}
