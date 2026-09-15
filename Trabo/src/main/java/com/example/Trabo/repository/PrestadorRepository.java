package com.example.Trabo.repository;

import com.example.Trabo.model.entity.Prestador;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PrestadorRepository extends JpaRepository<Prestador, Long> {

    Optional<Prestador> findByUsuarioId(Long usuarioId);

    List<Prestador> findByAprovado(boolean aprovado);

    @Query("""
        SELECT DISTINCT p FROM Prestador p
        LEFT JOIN p.categorias c
        WHERE p.aprovado = true
          AND SIZE(p.servicos) > 0
          AND (:categoriaId IS NULL OR c.id = :categoriaId)
          AND (:cidade IS NULL OR LOWER(p.cidade) LIKE LOWER(CONCAT('%', :cidade, '%')))
          AND (:estado IS NULL OR LOWER(p.estado) = LOWER(:estado))
          AND (:texto IS NULL OR
               LOWER(p.descricao) LIKE LOWER(CONCAT('%', :texto, '%')) OR
               LOWER(p.usuario.nome) LIKE LOWER(CONCAT('%', :texto, '%')))
        """)
    List<Prestador> buscarPublico(
            @Param("categoriaId") Long categoriaId,
            @Param("cidade") String cidade,
            @Param("estado") String estado,
            @Param("texto") String texto
    );
}
