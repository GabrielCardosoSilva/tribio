package com.example.Trabo.service;

import com.example.Trabo.dto.request.LoginRequest;
import com.example.Trabo.dto.request.RegistroPrestadorRequest;
import com.example.Trabo.dto.request.RegistroUsuarioRequest;
import com.example.Trabo.dto.response.JwtResponse;
import com.example.Trabo.exception.BadRequestException;
import com.example.Trabo.exception.BusinessException;
import com.example.Trabo.model.entity.Prestador;
import com.example.Trabo.model.entity.Usuario;
import com.example.Trabo.model.enums.Role;
import com.example.Trabo.repository.PrestadorRepository;
import com.example.Trabo.repository.UsuarioRepository;
import com.example.Trabo.security.JwtTokenProvider;
import com.example.Trabo.security.TokenBlacklist;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PrestadorRepository prestadorRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtTokenProvider tokenProvider;

    @Mock
    private TokenBlacklist tokenBlacklist;

    @InjectMocks
    private AuthService authService;

    private Usuario usuarioMock;

    @BeforeEach
    void setUp() {
        usuarioMock = Usuario.builder()
                .id(1L)
                .nome("Maria Silva")
                .email("maria@teste.com")
                .senhaHash("hash123")
                .role(Role.USUARIO)
                .ativo(true)
                .build();
    }

    @Test
    @DisplayName("Deve cadastrar novo usuário comum com sucesso")
    void registrarUsuario_comSucesso() {
        RegistroUsuarioRequest request = new RegistroUsuarioRequest("Maria Silva", "maria@teste.com", "senha123");
        when(usuarioRepository.existsByEmail("maria@teste.com")).thenReturn(false);
        when(passwordEncoder.encode("senha123")).thenReturn("hash123");

        authService.registrarUsuario(request);

        verify(usuarioRepository).save(any(Usuario.class));
    }

    @Test
    @DisplayName("Deve lançar BusinessException quando email já existir no cadastro de usuário")
    void registrarUsuario_emailDuplicado() {
        RegistroUsuarioRequest request = new RegistroUsuarioRequest("Maria Silva", "maria@teste.com", "senha123");
        when(usuarioRepository.existsByEmail("maria@teste.com")).thenReturn(true);

        assertThatThrownBy(() -> authService.registrarUsuario(request))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Email já cadastrado");

        verify(usuarioRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve cadastrar novo prestador com sucesso")
    void registrarPrestador_comSucesso() {
        RegistroPrestadorRequest request = new RegistroPrestadorRequest(
                "Carlos Eletricista", "carlos@teste.com", "senha123",
                "Campinas", "SP", "Cambuí", "Serviços elétricos", "19999999999", "19999999999"
        );
        when(usuarioRepository.existsByEmail("carlos@teste.com")).thenReturn(false);
        when(passwordEncoder.encode("senha123")).thenReturn("hash123");

        authService.registrarPrestador(request);

        verify(usuarioRepository).save(any(Usuario.class));
        verify(prestadorRepository).save(any(Prestador.class));
    }

    @Test
    @DisplayName("Deve autenticar e retornar JwtResponse no login")
    void login_comSucesso() {
        LoginRequest request = new LoginRequest("maria@teste.com", "senha123");
        Authentication auth = mock(Authentication.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(auth);
        when(tokenProvider.generateToken(auth)).thenReturn("jwt.token.mock");
        when(usuarioRepository.findByEmail("maria@teste.com")).thenReturn(Optional.of(usuarioMock));

        JwtResponse response = authService.login(request);

        assertThat(response).isNotNull();
        assertThat(response.token()).isEqualTo("jwt.token.mock");
        assertThat(response.nome()).isEqualTo("Maria Silva");
        assertThat(response.role()).isEqualTo("USUARIO");
    }

    @Test
    @DisplayName("Deve invalidar token no logout")
    void logout_comSucesso() {
        authService.logout("Bearer token-valido-123");
        verify(tokenBlacklist).blacklist("token-valido-123");
    }

    @Test
    @DisplayName("Deve lançar BadRequestException no logout sem token")
    void logout_semToken_lancaException() {
        assertThatThrownBy(() -> authService.logout(null))
                .isInstanceOf(BadRequestException.class);

        assertThatThrownBy(() -> authService.logout("InvalidHeader"))
                .isInstanceOf(BadRequestException.class);
    }
}
