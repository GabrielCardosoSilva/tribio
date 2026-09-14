package com.example.Trabo.service;

import com.example.Trabo.dto.request.CategoriaRequest;
import com.example.Trabo.dto.response.CategoriaResponse;
import com.example.Trabo.exception.BusinessException;
import com.example.Trabo.exception.NotFoundException;
import com.example.Trabo.model.entity.Categoria;
import com.example.Trabo.repository.CategoriaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;

    public CategoriaService(CategoriaRepository categoriaRepository) {
        this.categoriaRepository = categoriaRepository;
    }

    public List<CategoriaResponse> listarTodas() {
        return categoriaRepository.findAll().stream()
                .map(c -> new CategoriaResponse(c.getId(), c.getNome(), c.getIcone(), c.getDescricao()))
                .toList();
    }

    @Transactional
    public CategoriaResponse criar(CategoriaRequest request) {
        if (categoriaRepository.existsByNome(request.nome())) {
            throw new BusinessException("Categoria já existe: " + request.nome());
        }
        Categoria cat = Categoria.builder()
                .nome(request.nome())
                .icone(request.icone())
                .descricao(request.descricao())
                .build();
        cat = categoriaRepository.save(cat);
        return new CategoriaResponse(cat.getId(), cat.getNome(), cat.getIcone(), cat.getDescricao());
    }

    @Transactional
    public void deletar(Long id) {
        if (!categoriaRepository.existsById(id)) {
            throw new NotFoundException("Categoria não encontrada: " + id);
        }
        categoriaRepository.deleteById(id);
    }
}
