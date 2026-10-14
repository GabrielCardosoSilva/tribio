package com.example.Trabo.controller;

import com.example.Trabo.dto.request.AvaliacaoRequest;
import com.example.Trabo.dto.request.PortfolioRequest;
import com.example.Trabo.dto.response.AvaliacaoResponse;
import com.example.Trabo.dto.response.ErrorResponse;
import com.example.Trabo.dto.response.PrestadorResponse;
import com.example.Trabo.service.AvaliacaoService;
import com.example.Trabo.service.PrestadorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/prestadores")
@Tag(name = "Prestadores", description = "Endpoints de busca pública, detalhes, portfólio, avaliações e favoritos de prestadores de serviços")
public class PrestadorController {

    private final PrestadorService prestadorService;
    private final AvaliacaoService avaliacaoService;

    public PrestadorController(PrestadorService prestadorService, AvaliacaoService avaliacaoService) {
        this.prestadorService = prestadorService;
        this.avaliacaoService = avaliacaoService;
    }

    @GetMapping
    @Operation(summary = "Busca pública de prestadores",
            description = "Filtra prestadores aprovados e visíveis por categoria, localização (cidade/estado) ou busca textual livre.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de prestadores recuperada com sucesso",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = PrestadorResponse.class))))
    })
    public ResponseEntity<List<PrestadorResponse>> buscar(
            @Parameter(description = "ID da categoria para filtro", example = "1")
            @RequestParam(required = false) Long categoriaId,
            @Parameter(description = "Nome da cidade", example = "São Paulo")
            @RequestParam(required = false) String cidade,
            @Parameter(description = "Sigla do estado (UF)", example = "SP")
            @RequestParam(required = false) String estado,
            @Parameter(description = "Termo de busca (nome, descrição ou título de serviço)", example = "eletricista")
            @RequestParam(required = false) String texto) {
        return ResponseEntity.ok(prestadorService.buscarPublico(categoriaId, cidade, estado, texto));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Perfil público do prestador", description = "Retorna detalhes completos do portfólio e serviços do prestador.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Prestador encontrado",
                    content = @Content(schema = @Schema(implementation = PrestadorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Prestador não encontrado",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<PrestadorResponse> buscarPorId(
            @Parameter(description = "ID do prestador", example = "1") @PathVariable Long id) {
        return ResponseEntity.ok(prestadorService.buscarPorId(id));
    }

    @GetMapping("/{id}/avaliacoes")
    @Operation(summary = "Listar avaliações de um prestador", description = "Recupera todas as notas e comentários deixados por clientes.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de avaliações",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = AvaliacaoResponse.class)))),
            @ApiResponse(responseCode = "404", description = "Prestador não encontrado",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<List<AvaliacaoResponse>> listarAvaliacoes(
            @Parameter(description = "ID do prestador", example = "1") @PathVariable Long id) {
        return ResponseEntity.ok(avaliacaoService.listarPorPrestador(id));
    }

    @PostMapping("/{id}/avaliacoes")
    @PreAuthorize("hasRole('USUARIO')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Avaliar um prestador (requer login como USUARIO)",
            description = "Registra uma nota de 1 a 5 e comentário. Cada usuário só pode avaliar o mesmo prestador uma única vez.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Avaliação registrada com sucesso",
                    content = @Content(schema = @Schema(implementation = AvaliacaoResponse.class))),
            @ApiResponse(responseCode = "400", description = "Nota inválida (fora do intervalo 1-5)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Não autenticado",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Sem permissão (apenas clientes podem avaliar)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Prestador não encontrado",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "422", description = "Usuário já avaliou este prestador",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<AvaliacaoResponse> avaliar(
            @Parameter(description = "ID do prestador a ser avaliado", example = "1") @PathVariable Long id,
            @Valid @RequestBody AvaliacaoRequest request) {
        return ResponseEntity.ok(avaliacaoService.avaliar(id, request));
    }

    @PostMapping("/{id}/favoritar")
    @PreAuthorize("hasAnyRole('USUARIO', 'PRESTADOR', 'ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Favoritar ou desfavoritar um prestador",
            description = "Alterna o status do prestador na lista de favoritos do usuário logado.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Status de favorito alternado com sucesso"),
            @ApiResponse(responseCode = "401", description = "Não autenticado",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Prestador não encontrado",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<Void> favoritar(
            @Parameter(description = "ID do prestador", example = "1") @PathVariable Long id) {
        prestadorService.favoritar(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('PRESTADOR')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Perfil do próprio prestador logado", description = "Retorna os dados cadastrais e portfólio completo do prestador autenticado.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Dados do prestador",
                    content = @Content(schema = @Schema(implementation = PrestadorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Não autenticado",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Apenas prestadores podem acessar",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Perfil não encontrado",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<PrestadorResponse> buscarMeuPerfil() {
        return ResponseEntity.ok(prestadorService.buscarMeuPerfil());
    }

    @PostMapping("/me/portfolio")
    @PreAuthorize("hasRole('PRESTADOR')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Atualizar portfólio completo",
            description = "Salva descrição, localização, serviços, tabela de pacotes, fotos da galeria e cor do perfil.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Portfólio salvo com sucesso",
                    content = @Content(schema = @Schema(implementation = PrestadorResponse.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos de validação",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Não autenticado",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Apenas prestadores podem editar",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<PrestadorResponse> atualizarPortfolio(
            @Valid @RequestBody PortfolioRequest request) {
        return ResponseEntity.ok(prestadorService.atualizarPortfolio(request));
    }
}
