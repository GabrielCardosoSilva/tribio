package com.example.Trabo.service;

import com.example.Trabo.dto.response.NotificacaoResponse;
import com.example.Trabo.model.entity.Notificacao;
import com.example.Trabo.model.entity.Usuario;
import com.example.Trabo.repository.NotificacaoRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificacaoServiceTest {

    @Mock
    private NotificacaoRepository notificacaoRepository;

    @Mock
    private UsuarioAutenticadoService usuarioAutenticadoService;

    @InjectMocks
    private NotificacaoService notificacaoService;

    @Test
    @DisplayName("Deve listar notificações do usuário logado")
    void listar_comSucesso() {
        Usuario usuario = Usuario.builder().id(1L).nome("João").build();
        when(usuarioAutenticadoService.getUsuario()).thenReturn(usuario);

        Notificacao n1 = new Notificacao(usuario, "Mensagem 1", "LIKE");
        Notificacao n2 = new Notificacao(usuario, "Mensagem 2", "VIEW");
        when(notificacaoRepository.findByUsuarioIdOrderByCreatedAtDesc(1L)).thenReturn(List.of(n1, n2));

        List<NotificacaoResponse> resultado = notificacaoService.listar();

        assertThat(resultado).hasSize(2);
        assertThat(resultado.get(0).mensagem()).isEqualTo("Mensagem 1");
        assertThat(resultado.get(1).mensagem()).isEqualTo("Mensagem 2");
    }

    @Test
    @DisplayName("Deve marcar notificações não lidas como lidas")
    void marcarComoLidas_comSucesso() {
        Usuario usuario = Usuario.builder().id(1L).nome("João").build();
        when(usuarioAutenticadoService.getUsuario()).thenReturn(usuario);

        Notificacao n1 = new Notificacao(usuario, "Nova msg", "LIKE");
        assertThat(n1.isLida()).isFalse();

        when(notificacaoRepository.findByUsuarioIdOrderByCreatedAtDesc(1L)).thenReturn(List.of(n1));

        notificacaoService.marcarComoLidas();

        assertThat(n1.isLida()).isTrue();
        verify(notificacaoRepository).saveAll(anyList());
    }
}
