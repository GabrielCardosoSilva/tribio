package com.example.Trabo.controller;

import com.example.Trabo.model.entity.Prestador;
import com.example.Trabo.model.entity.Usuario;
import com.example.Trabo.model.enums.Role;
import com.example.Trabo.repository.PrestadorRepository;
import com.example.Trabo.repository.UsuarioRepository;
import com.example.Trabo.security.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class PrestadorControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PrestadorRepository prestadorRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Autowired
    private org.springframework.jdbc.core.JdbcTemplate jdbcTemplate;

    private Prestador prestadorSalvo;
    private String tokenUsuario;

    @BeforeEach
    void setUp() {
        jdbcTemplate.execute("DELETE FROM notificacoes");
        jdbcTemplate.execute("DELETE FROM avaliacoes");
        jdbcTemplate.execute("DELETE FROM usuario_favoritos");
        prestadorRepository.deleteAll();
        usuarioRepository.deleteAll();

        Usuario uPrestador = Usuario.builder()
                .nome("José Pinturas")
                .email("jose@pinturas.com")
                .senhaHash(passwordEncoder.encode("senha123"))
                .role(Role.PRESTADOR)
                .ativo(true)
                .build();
        usuarioRepository.save(uPrestador);

        prestadorSalvo = Prestador.builder()
                .usuario(uPrestador)
                .cidade("Campinas")
                .estado("SP")
                .descricao("Pinturas residenciais e comerciais de alta qualidade")
                .telefone("19988887777")
                .whatsapp("19988887777")
                .aprovado(true)
                .visivel(true)
                .build();
        prestadorRepository.save(prestadorSalvo);

        Usuario uCliente = Usuario.builder()
                .nome("Ana Cliente")
                .email("ana@cliente.com")
                .senhaHash(passwordEncoder.encode("senha123"))
                .role(Role.USUARIO)
                .ativo(true)
                .build();
        usuarioRepository.save(uCliente);

        tokenUsuario = tokenProvider.generateTokenFromEmail("ana@cliente.com");
    }

    @Test
    @DisplayName("IT: Deve buscar prestadores publicamente sem necessidade de autenticação")
    void buscarPrestadores_publico() throws Exception {
        mockMvc.perform(get("/api/prestadores")
                        .param("cidade", "Campinas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].nome", is("José Pinturas")))
                .andExpect(jsonPath("$[0].cidade", is("Campinas")));
    }

    @Test
    @DisplayName("IT: Deve retornar 404 padronizado ao buscar prestador por ID inexistente")
    void buscarPorId_inexistente_retorna404() throws Exception {
        mockMvc.perform(get("/api/prestadores/999999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.error", containsString("Prestador não encontrado")))
                .andExpect(jsonPath("$.path", is("/api/prestadores/999999")));
    }

    @Test
    @DisplayName("IT: Deve bloquear endpoint protegido /favoritar sem autenticação retornando 401")
    void favoritar_semAuth_retorna401() throws Exception {
        mockMvc.perform(post("/api/prestadores/" + prestadorSalvo.getId() + "/favoritar"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status", is(401)))
                .andExpect(jsonPath("$.error", containsString("Autenticação necessária")));
    }

    @Test
    @DisplayName("IT: Deve favoritar prestador com sucesso quando autenticado")
    void favoritar_comAuth_sucesso() throws Exception {
        mockMvc.perform(post("/api/prestadores/" + prestadorSalvo.getId() + "/favoritar")
                        .header("Authorization", "Bearer " + tokenUsuario))
                .andExpect(status().isOk());
    }
}
