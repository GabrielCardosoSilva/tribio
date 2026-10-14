package com.example.Trabo.controller;

import com.example.Trabo.dto.request.LoginRequest;
import com.example.Trabo.dto.request.RegistroUsuarioRequest;
import com.example.Trabo.repository.UsuarioRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @BeforeEach
    void clean() {
        usuarioRepository.deleteAll();
    }

    @Test
    @DisplayName("IT: Deve registrar usuário e fazer login com sucesso retornando JWT")
    void registrarELogarUsuario_comSucesso() throws Exception {
        RegistroUsuarioRequest registro = new RegistroUsuarioRequest("Lucas Mendes", "lucas@email.com", "senhaSegura123");

        mockMvc.perform(post("/api/auth/registro/usuario")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registro)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.mensagem", containsString("sucesso")));

        LoginRequest login = new LoginRequest("lucas@email.com", "senhaSegura123");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token", notNullValue()))
                .andExpect(jsonPath("$.tipo", is("Bearer")))
                .andExpect(jsonPath("$.nome", is("Lucas Mendes")))
                .andExpect(jsonPath("$.role", is("USUARIO")));
    }

    @Test
    @DisplayName("IT: Deve retornar 422 padronizado ao tentar cadastrar email já existente")
    void registrarUsuario_emailDuplicado_retorna422() throws Exception {
        RegistroUsuarioRequest registro = new RegistroUsuarioRequest("Lucas Mendes", "lucas@email.com", "senhaSegura123");

        mockMvc.perform(post("/api/auth/registro/usuario")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registro)))
                .andExpect(status().isCreated());

        // Tentativa com mesmo e-mail
        mockMvc.perform(post("/api/auth/registro/usuario")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registro)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.status", is(422)))
                .andExpect(jsonPath("$.error", containsString("Email já cadastrado")))
                .andExpect(jsonPath("$.path", is("/api/auth/registro/usuario")))
                .andExpect(jsonPath("$.timestamp", notNullValue()));
    }

    @Test
    @DisplayName("IT: Deve retornar 401 padronizado em credenciais inválidas")
    void login_senhaInvalida_retorna401() throws Exception {
        LoginRequest login = new LoginRequest("inexistente@email.com", "senhaErrada");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status", is(401)))
                .andExpect(jsonPath("$.error", containsString("Email ou senha inválidos")));
    }
}
