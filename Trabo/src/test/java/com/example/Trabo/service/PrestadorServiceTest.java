package com.example.Trabo.service;

import com.example.Trabo.dto.request.PortfolioRequest;
import com.example.Trabo.dto.response.PrestadorResponse;
import com.example.Trabo.exception.NotFoundException;
import com.example.Trabo.model.entity.Notificacao;
import com.example.Trabo.model.entity.Prestador;
import com.example.Trabo.model.entity.Usuario;
import com.example.Trabo.model.enums.Role;
import com.example.Trabo.repository.CategoriaRepository;
import com.example.Trabo.repository.NotificacaoRepository;
import com.example.Trabo.repository.PrestadorRepository;
import com.example.Trabo.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PrestadorServiceTest {

    @Mock
    private PrestadorRepository prestadorRepository;

    @Mock
    private CategoriaRepository categoriaRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private NotificacaoRepository notificacaoRepository;

    @Mock
    private UsuarioAutenticadoService usuarioAutenticadoService;

    @InjectMocks
    private PrestadorService prestadorService;

    private Usuario prestadorUsuario;
    private Prestador prestador;
    private Usuario clienteLogado;

    @BeforeEach
    void setUp() {
        prestadorUsuario = Usuario.builder()
                .id(1L)
                .nome("Carlos Eletricista")
                .email("carlos@teste.com")
                .role(Role.PRESTADOR)
                .build();

        prestador = Prestador.builder()
                .id(10L)
                .usuario(prestadorUsuario)
                .cidade("Campinas")
                .estado("SP")
                .descricao("Especialista em elétrica")
                .aprovado(true)
                .visivel(true)
                .categorias(new ArrayList<>())
                .servicos(new ArrayList<>())
                .pacotesPrecos(new ArrayList<>())
                .galeria(new ArrayList<>())
                .build();

        clienteLogado = Usuario.builder()
                .id(2L)
                .nome("Maria Cliente")
                .email("maria@teste.com")
                .role(Role.USUARIO)
                .favoritos(new HashSet<>())
                .build();
    }

    @Test
    @DisplayName("Deve buscar prestador por ID e gerar notificação de visita quando visitante for diferente do dono")
    void buscarPorId_comNotificacaoDeVisita() {
        when(prestadorRepository.findById(10L)).thenReturn(Optional.of(prestador));
        when(usuarioAutenticadoService.getUsuarioOpcional()).thenReturn(Optional.of(clienteLogado));

        PrestadorResponse response = prestadorService.buscarPorId(10L);

        assertThat(response).isNotNull();
        assertThat(response.id()).isEqualTo(10L);
        assertThat(response.nome()).isEqualTo("Carlos Eletricista");
        verify(notificacaoRepository).save(any(Notificacao.class));
    }

    @Test
    @DisplayName("Deve lançar NotFoundException ao buscar prestador por ID inexistente")
    void buscarPorId_inexistente() {
        when(prestadorRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> prestadorService.buscarPorId(999L))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    @DisplayName("Deve favoritar e em seguida desfavoritar alternando status")
    void favoritar_alternaStatus() {
        when(usuarioAutenticadoService.getUsuario()).thenReturn(clienteLogado);
        when(prestadorRepository.findById(10L)).thenReturn(Optional.of(prestador));

        // 1. Favoritar (adicionar)
        prestadorService.favoritar(10L);
        assertThat(clienteLogado.getFavoritos()).contains(prestador);
        verify(notificacaoRepository).save(any(Notificacao.class));
        verify(usuarioRepository, times(1)).save(clienteLogado);

        // 2. Desfavoritar (remover)
        prestadorService.favoritar(10L);
        assertThat(clienteLogado.getFavoritos()).doesNotContain(prestador);
        verify(usuarioRepository, times(2)).save(clienteLogado);
    }

    @Test
    @DisplayName("Deve atualizar portfólio completo do prestador")
    void atualizarPortfolio_comSucesso() {
        when(usuarioAutenticadoService.getUsuario()).thenReturn(prestadorUsuario);
        when(prestadorRepository.findByUsuarioId(1L)).thenReturn(Optional.of(prestador));
        when(prestadorRepository.save(any(Prestador.class))).thenAnswer(i -> i.getArgument(0));

        PortfolioRequest request = new PortfolioRequest(
                "Nova descrição", "RJ", "Rio de Janeiro", "Av Atlântica",
                "21999999999", "contato@carlos.com",
                null, null, null, true, "#1D4ED8"
        );

        PrestadorResponse response = prestadorService.atualizarPortfolio(request);

        assertThat(response).isNotNull();
        assertThat(prestador.getDescricao()).isEqualTo("Nova descrição");
        assertThat(prestador.getCidade()).isEqualTo("Rio de Janeiro");
        assertThat(prestador.getEstado()).isEqualTo("RJ");
        assertThat(prestador.getCorPerfil()).isEqualTo("#1D4ED8");
        verify(prestadorRepository).save(prestador);
    }
}
