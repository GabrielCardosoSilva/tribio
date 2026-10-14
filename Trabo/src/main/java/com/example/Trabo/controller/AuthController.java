package com.example.Trabo.controller;

import com.example.Trabo.dto.request.LoginRequest;
import com.example.Trabo.dto.request.RegistroPrestadorRequest;
import com.example.Trabo.dto.request.RegistroUsuarioRequest;
import com.example.Trabo.dto.response.ErrorResponse;
import com.example.Trabo.dto.response.JwtResponse;
import com.example.Trabo.dto.response.MensagemResponse;
import com.example.Trabo.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Autenticação", description = "Endpoints públicos de cadastro, login e encerramento de sessão (JWT)")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/registro/usuario")
    @Operation(summary = "Cadastrar novo usuário comum", description = "Cria uma conta com perfil de cliente para buscar profissionais e avaliar serviços.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Usuário cadastrado com sucesso",
                    content = @Content(schema = @Schema(implementation = MensagemResponse.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos de validação",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "422", description = "Email já cadastrado",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<MensagemResponse> registrarUsuario(@Valid @RequestBody RegistroUsuarioRequest request) {
        authService.registrarUsuario(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new MensagemResponse("Usuário cadastrado com sucesso"));
    }

    @PostMapping("/registro/prestador")
    @Operation(summary = "Cadastrar novo prestador de serviços", description = "Cria uma conta profissional com informações de contato e localização.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Prestador cadastrado com sucesso",
                    content = @Content(schema = @Schema(implementation = MensagemResponse.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos de validação",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "422", description = "Email já cadastrado",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<MensagemResponse> registrarPrestador(@Valid @RequestBody RegistroPrestadorRequest request) {
        authService.registrarPrestador(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new MensagemResponse("Prestador cadastrado com sucesso."));
    }

    @PostMapping("/login")
    @Operation(summary = "Login de usuário ou prestador", description = "Autentica via email e senha e retorna o token JWT para as requisições autenticadas.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Login bem-sucedido com token JWT gerado",
                    content = @Content(schema = @Schema(implementation = JwtResponse.class))),
            @ApiResponse(responseCode = "401", description = "Credenciais inválidas",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "429", description = "Limite de tentativas excedido (Rate Limit)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<JwtResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/logout")
    @Operation(summary = "Logout — Invalida o token atual", description = "Adiciona o token à lista negra (blacklist) impedindo novos acessos.")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Logout realizado com sucesso",
                    content = @Content(schema = @Schema(implementation = MensagemResponse.class))),
            @ApiResponse(responseCode = "400", description = "Token não encontrado no cabeçalho Authorization",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<MensagemResponse> logout(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        authService.logout(bearerToken);
        return ResponseEntity.ok(new MensagemResponse("Logout realizado com sucesso"));
    }
}
