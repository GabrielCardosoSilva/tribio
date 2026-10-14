package com.example.Trabo.controller;

import com.example.Trabo.dto.response.ErrorResponse;
import com.example.Trabo.dto.response.NotificacaoResponse;
import com.example.Trabo.service.NotificacaoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notificacoes")
@Tag(name = "Notificações", description = "Endpoints de consulta e atualização de notificações do usuário autenticado")
@PreAuthorize("isAuthenticated()")
@SecurityRequirement(name = "bearerAuth")
public class NotificacaoController {

    private final NotificacaoService notificacaoService;

    public NotificacaoController(NotificacaoService notificacaoService) {
        this.notificacaoService = notificacaoService;
    }

    @GetMapping
    @Operation(summary = "Lista as notificações do usuário logado", description = "Retorna o histórico de notificações em ordem cronológica decrescente.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de notificações recuperada com sucesso",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = NotificacaoResponse.class)))),
            @ApiResponse(responseCode = "401", description = "Não autenticado",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<List<NotificacaoResponse>> listar() {
        return ResponseEntity.ok(notificacaoService.listar());
    }

    @PutMapping("/lidas")
    @Operation(summary = "Marca todas as notificações do usuário logado como lidas")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Notificações marcadas como lidas"),
            @ApiResponse(responseCode = "401", description = "Não autenticado",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<Void> marcarComoLidas() {
        notificacaoService.marcarComoLidas();
        return ResponseEntity.noContent().build();
    }
}
