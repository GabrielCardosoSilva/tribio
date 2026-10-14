package com.example.Trabo.service;

import com.example.Trabo.dto.response.PerfilUsuarioResponse;
import com.example.Trabo.model.entity.Prestador;
import com.example.Trabo.model.entity.Usuario;
import com.example.Trabo.model.enums.Role;
import com.example.Trabo.repository.PrestadorRepository;
import com.example.Trabo.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PrestadorRepository prestadorRepository;

    @Mock
    private FileService fileService;

    @Mock
    private AuditoriaService auditoriaService;

    @Mock
    private UsuarioAutenticadoService usuarioAutenticadoService;

    @InjectMocks
    private UsuarioService usuarioService;

    private Usuario usuarioMock;

    @BeforeEach
    void setUp() {
        usuarioMock = Usuario.builder()
                .id(1L)
                .nome("Maria Silva")
                .email("maria@teste.com")
                .role(Role.USUARIO)
                .fotoPerfil("/api/arquivos/avatar.png")
                .build();
    }

    @Test
    @DisplayName("Deve retornar perfil do usuário autenticado")
    void getMe_comSucesso() {
        when(usuarioAutenticadoService.getUsuario()).thenReturn(usuarioMock);

        PerfilUsuarioResponse perfil = usuarioService.getMe("127.0.0.1");

        assertThat(perfil).isNotNull();
        assertThat(perfil.nome()).isEqualTo("Maria Silva");
        assertThat(perfil.email()).isEqualTo("maria@teste.com");
        assertThat(perfil.role()).isEqualTo(Role.USUARIO);
        verify(auditoriaService).registrar("maria@teste.com", "ACESSO_DADOS_PESSOAIS", "GET /api/usuarios/me", "127.0.0.1");
    }

    @Test
    @DisplayName("Deve atualizar foto de perfil do usuário")
    void uploadFotoPerfil_comSucesso() {
        MockMultipartFile file = new MockMultipartFile("file", "avatar.png", "image/png", "bytes".getBytes());
        when(usuarioAutenticadoService.getUsuario()).thenReturn(usuarioMock);
        when(fileService.salvarArquivo(file)).thenReturn("/api/arquivos/nova-foto.png");

        String url = usuarioService.uploadFotoPerfil(file, "127.0.0.1");

        assertThat(url).isEqualTo("/api/arquivos/nova-foto.png");
        assertThat(usuarioMock.getFotoPerfil()).isEqualTo("/api/arquivos/nova-foto.png");
        verify(usuarioRepository).save(usuarioMock);
        verify(auditoriaService).registrar("maria@teste.com", "UPLOAD_FOTO_PERFIL", "Foto atualizada: /api/arquivos/nova-foto.png", "127.0.0.1");
    }

    @Test
    @DisplayName("Deve excluir conta e prestador associado conforme LGPD")
    void excluirConta_comPrestador() {
        Prestador prestador = Prestador.builder().id(5L).build();
        usuarioMock.setPrestador(prestador);

        when(usuarioAutenticadoService.getUsuario()).thenReturn(usuarioMock);

        usuarioService.excluirConta("127.0.0.1");

        verify(auditoriaService).registrar(eq("maria@teste.com"), eq("EXCLUSAO_CONTA"), anyString(), eq("127.0.0.1"));
        verify(prestadorRepository).delete(prestador);
        verify(usuarioRepository).delete(usuarioMock);
    }
}
