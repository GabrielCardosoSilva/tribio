package com.example.Trabo.controller;

import com.example.Trabo.dto.response.ErrorResponse;
import com.example.Trabo.service.FileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/arquivos")
@Tag(name = "Arquivos", description = "Upload e recuperação de arquivos estáticos (fotos de perfil, galeria de portfólio)")
public class FileController {

    private final FileService fileService;

    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("isAuthenticated()")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Upload de arquivo", description = "Envia uma imagem para a galeria ou portfólio (requer login).")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Arquivo salvo com sucesso, retorna o caminho relativo do arquivo",
                    content = @Content(schema = @Schema(example = "/api/arquivos/uuid.jpg"))),
            @ApiResponse(responseCode = "400", description = "Arquivo vazio ou formato não suportado",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Não autenticado",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<String> uploadArquivo(@RequestParam("file") MultipartFile file) {
        String uri = fileService.salvarArquivo(file);
        return ResponseEntity.ok(uri);
    }

    @GetMapping("/{nomeArquivo:.+}")
    @Operation(summary = "Visualizar/baixar arquivo", description = "Retorna o arquivo enviado publicamente pelo nome gerado.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Arquivo retornado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Arquivo não encontrado",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<Resource> lerArquivo(
            @Parameter(description = "Nome do arquivo com extensão", example = "9b1deb4d-3b7d-4bad-9bdd-2b0d7b3dcb6d.png")
            @PathVariable String nomeArquivo) {
        Resource resource = fileService.carregarArquivo(nomeArquivo);

        MediaType mediaType = MediaTypeFactory.getMediaType(resource)
                .orElse(MediaType.APPLICATION_OCTET_STREAM);

        return ResponseEntity.ok()
                .contentType(mediaType)
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }
}
