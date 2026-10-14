package com.example.Trabo.service;

import com.example.Trabo.dto.request.AvaliacaoRequest;
import com.example.Trabo.dto.response.AvaliacaoResponse;
import com.example.Trabo.exception.BusinessException;
import com.example.Trabo.exception.NotFoundException;
import com.example.Trabo.model.entity.Avaliacao;
import com.example.Trabo.model.entity.Prestador;
import com.example.Trabo.model.entity.Usuario;
import com.example.Trabo.model.enums.Role;
import com.example.Trabo.repository.AvaliacaoRepository;
import com.example.Trabo.repository.PrestadorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AvaliacaoServiceTest {

    @Mock
    private AvaliacaoRepository avaliacaoRepository;

    @Mock
    private PrestadorRepository prestadorRepository;

    @Mock
    private UsuarioAutenticadoService usuarioAutenticadoService;

    @InjectMocks
    private AvaliacaoService avaliacaoService;

    private Usuario usuarioMock;
    private Prestador prestadorMock;

    @BeforeEach
    void setUp() {
        usuarioMock = Usuario.builder()
                .id(1L)
                .nome("Cliente Teste")
                .email("cliente@teste.com")
                .role(Role.USUARIO)
                .build();

        prestadorMock = Prestador.builder()
                .id(10L)
                .usuario(Usuario.builder().id(2L).nome("Prestador Teste").build())
                .cidade("Campinas")
                .estado("SP")
                .build();
    }

    @Test
    @DisplayName("Deve avaliar prestador com sucesso")
    void avaliar_comSucesso() {
        AvaliacaoRequest request = new AvaliacaoRequest(5, "Ótimo serviço!");
        when(usuarioAutenticadoService.getUsuario()).thenReturn(usuarioMock);
        when(prestadorRepository.findById(10L)).thenReturn(Optional.of(prestadorMock));
        when(avaliacaoRepository.existsByPrestadorIdAndUsuarioId(10L, 1L)).thenReturn(false);

        Avaliacao salva = Avaliacao.builder()
                .id(100L)
                .usuario(usuarioMock)
                .prestador(prestadorMock)
                .nota(5)
                .comentario("Ótimo serviço!")
                .build();
        when(avaliacaoRepository.save(any(Avaliacao.class))).thenReturn(salva);

        AvaliacaoResponse response = avaliacaoService.avaliar(10L, request);

        assertThat(response).isNotNull();
        assertThat(response.nota()).isEqualTo(5);
        assertThat(response.comentario()).isEqualTo("Ótimo serviço!");
        assertThat(response.nomeUsuario()).isEqualTo("Cliente Teste");
    }

    @Test
    @DisplayName("Deve lançar BusinessException se usuário tentar avaliar o mesmo prestador duas vezes")
    void avaliar_duplicada() {
        AvaliacaoRequest request = new AvaliacaoRequest(4, "Bom");
        when(usuarioAutenticadoService.getUsuario()).thenReturn(usuarioMock);
        when(prestadorRepository.findById(10L)).thenReturn(Optional.of(prestadorMock));
        when(avaliacaoRepository.existsByPrestadorIdAndUsuarioId(10L, 1L)).thenReturn(true);

        assertThatThrownBy(() -> avaliacaoService.avaliar(10L, request))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("já avaliou");

        verify(avaliacaoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar NotFoundException ao avaliar prestador inexistente")
    void avaliar_prestadorInexistente() {
        AvaliacaoRequest request = new AvaliacaoRequest(5, "Top");
        when(usuarioAutenticadoService.getUsuario()).thenReturn(usuarioMock);
        when(prestadorRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> avaliacaoService.avaliar(999L, request))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    @DisplayName("Deve listar avaliações de um prestador existente")
    void listarPorPrestador_comSucesso() {
        when(prestadorRepository.existsById(10L)).thenReturn(true);

        Avaliacao a1 = Avaliacao.builder()
                .id(1L)
                .usuario(usuarioMock)
                .prestador(prestadorMock)
                .nota(5)
                .comentario("Muito bom")
                .build();
        when(avaliacaoRepository.findByPrestadorId(10L)).thenReturn(List.of(a1));

        List<AvaliacaoResponse> lista = avaliacaoService.listarPorPrestador(10L);

        assertThat(lista).hasSize(1);
        assertThat(lista.get(0).nota()).isEqualTo(5);
    }
}
