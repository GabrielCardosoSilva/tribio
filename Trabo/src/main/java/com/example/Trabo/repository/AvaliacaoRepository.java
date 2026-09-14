package com.example.Trabo.repository;

import com.example.Trabo.model.entity.Avaliacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AvaliacaoRepository extends JpaRepository<Avaliacao, Long> {
    List<Avaliacao> findByPrestadorId(Long prestadorId);
    boolean existsByPrestadorIdAndUsuarioId(Long prestadorId, Long usuarioId);
}
