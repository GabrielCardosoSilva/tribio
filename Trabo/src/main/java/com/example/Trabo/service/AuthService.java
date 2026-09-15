package com.example.Trabo.service;

import com.example.Trabo.dto.request.LoginRequest;
import com.example.Trabo.dto.request.RegistroPrestadorRequest;
import com.example.Trabo.dto.request.RegistroUsuarioRequest;
import com.example.Trabo.dto.response.JwtResponse;
import com.example.Trabo.exception.BusinessException;
import com.example.Trabo.model.entity.Prestador;
import com.example.Trabo.model.entity.Usuario;
import com.example.Trabo.model.enums.Role;
import com.example.Trabo.repository.PrestadorRepository;
import com.example.Trabo.repository.UsuarioRepository;
import com.example.Trabo.security.JwtTokenProvider;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PrestadorRepository prestadorRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;

    public AuthService(UsuarioRepository usuarioRepository,
                       PrestadorRepository prestadorRepository,
                       PasswordEncoder passwordEncoder,
                       AuthenticationManager authenticationManager,
                       JwtTokenProvider tokenProvider) {
        this.usuarioRepository = usuarioRepository;
        this.prestadorRepository = prestadorRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.tokenProvider = tokenProvider;
    }

    @Transactional
    public void registrarUsuario(RegistroUsuarioRequest request) {
        if (usuarioRepository.existsByEmail(request.email())) {
            throw new BusinessException("Email já cadastrado: " + request.email());
        }
        Usuario usuario = Usuario.builder()
                .nome(request.nome())
                .email(request.email())
                .senhaHash(passwordEncoder.encode(request.senha()))
                .role(Role.USUARIO)
                .ativo(true)
                .build();
        usuarioRepository.save(usuario);
    }

    @Transactional
    public void registrarPrestador(RegistroPrestadorRequest request) {
        if (usuarioRepository.existsByEmail(request.email())) {
            throw new BusinessException("Email já cadastrado: " + request.email());
        }
        Usuario usuario = Usuario.builder()
                .nome(request.nome())
                .email(request.email())
                .senhaHash(passwordEncoder.encode(request.senha()))
                .role(Role.PRESTADOR)
                .ativo(true)
                .build();
        usuarioRepository.save(usuario);

        Prestador prestador = Prestador.builder()
                .usuario(usuario)
                .cidade(request.cidade())
                .estado(request.estado())
                .bairro(request.bairro())
                .descricao(request.descricao())
                .telefone(request.telefone())
                .whatsapp(request.whatsapp())
                .aprovado(false) // Aguarda aprovação do admin
                .build();
        prestadorRepository.save(prestador);
    }

    public JwtResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.senha())
        );
        String token = tokenProvider.generateToken(authentication);
        Usuario usuario = usuarioRepository.findByEmail(request.email()).orElseThrow();
        return new JwtResponse(token, usuario.getNome(), usuario.getRole().name());
    }
}
