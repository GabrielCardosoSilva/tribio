package com.example.Trabo.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileService {

    private final String uploadDir = "uploads/";

    public FileService() {
        try {
            Files.createDirectories(Paths.get(uploadDir));
        } catch (IOException e) {
            throw new RuntimeException("Não foi possível criar o diretório de uploads", e);
        }
    }

    public String salvarArquivo(MultipartFile arquivo) {
        try {
            String fileName = UUID.randomUUID().toString() + "_" + arquivo.getOriginalFilename();
            Path filePath = Paths.get(uploadDir + fileName);
            Files.copy(arquivo.getInputStream(), filePath);
            return "/api/arquivos/" + fileName;
        } catch (IOException e) {
            throw new RuntimeException("Falha ao salvar arquivo", e);
        }
    }
}
