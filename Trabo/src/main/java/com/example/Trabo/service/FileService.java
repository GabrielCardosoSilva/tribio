package com.example.Trabo.service;

import com.example.Trabo.exception.BadRequestException;
import com.example.Trabo.exception.NotFoundException;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Set;
import java.util.UUID;

@Service
public class FileService {

    private final Path uploadPath;
    private static final Set<String> EXTENSOES_PERMITIDAS = Set.of(
            ".jpg", ".jpeg", ".png", ".webp", ".gif", ".pdf"
    );

    public FileService() {
        this.uploadPath = Paths.get("uploads").toAbsolutePath().normalize();
        try {
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
        } catch (IOException e) {
            throw new RuntimeException("Não foi possível criar o diretório de uploads", e);
        }
    }

    public String salvarArquivo(MultipartFile arquivo) {
        if (arquivo == null || arquivo.isEmpty()) {
            throw new BadRequestException("Nenhum arquivo enviado");
        }

        String originalFilename = arquivo.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf(".")).toLowerCase();
        }

        if (!EXTENSOES_PERMITIDAS.contains(extension)) {
            throw new BadRequestException("Formato de arquivo não suportado. Formatos aceitos: JPG, PNG, WEBP, GIF, PDF");
        }

        String fileName = UUID.randomUUID() + extension;
        Path targetPath = uploadPath.resolve(fileName).normalize();

        // Previne path traversal
        if (!targetPath.startsWith(uploadPath)) {
            throw new BadRequestException("Nome de arquivo inválido");
        }

        try {
            Files.copy(arquivo.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
            return "/api/arquivos/" + fileName;
        } catch (IOException e) {
            throw new RuntimeException("Falha ao salvar arquivo", e);
        }
    }

    public Resource carregarArquivo(String nomeArquivo) {
        if (nomeArquivo == null || nomeArquivo.isBlank()) {
            throw new BadRequestException("Nome do arquivo não pode ser vazio");
        }

        Path targetPath = uploadPath.resolve(nomeArquivo).normalize();

        // Proteção contra Directory Traversal (ex: ../../etc/passwd)
        if (!targetPath.startsWith(uploadPath)) {
            throw new BadRequestException("Caminho de arquivo inválido");
        }

        try {
            Resource resource = new UrlResource(targetPath.toUri());
            if (!resource.exists() || !resource.isReadable()) {
                throw new NotFoundException("Arquivo não encontrado: " + nomeArquivo);
            }
            return resource;
        } catch (MalformedURLException e) {
            throw new BadRequestException("URL de arquivo malformada");
        }
    }
}
