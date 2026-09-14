package com.example.Trabo.controller;

import com.example.Trabo.dto.request.LoginRequest;
import com.example.Trabo.dto.request.RegistroPrestadorRequest;
import com.example.Trabo.dto.request.RegistroUsuarioRequest;
import com.example.Trabo.dto.response.JwtResponse;
import com.example.Trabo.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Autenticação")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/registro/usuario")
    @Operation(summary = "Cadastrar novo usuário comum")
    public ResponseEntity<String> registrarUsuario(@Valid @RequestBody RegistroUsuarioRequest request) {
        authService.registrarUsuario(request);
        return ResponseEntity.status(HttpStatus.CREATED).body("Usuário cadastrado com sucesso");
    }

    @PostMapping("/registro/prestador")
    @Operation(summary = "Cadastrar novo prestador (aguarda aprovação do admin)")
    public ResponseEntity<String> registrarPrestador(@Valid @RequestBody RegistroPrestadorRequest request) {
        authService.registrarPrestador(request);
        return ResponseEntity.status(HttpStatus.CREATED).body("Prestador cadastrado. Aguardando aprovação do administrador.");
    }

    @PostMapping("/login")
    @Operation(summary = "Login — retorna JWT")
    public ResponseEntity<JwtResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }
}
