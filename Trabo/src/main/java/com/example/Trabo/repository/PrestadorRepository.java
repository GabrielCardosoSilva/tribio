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

    @Query(value = """
        SELECT DISTINCT p.* FROM prestadores p
        JOIN usuarios u ON u.id = p.usuario_id
        LEFT JOIN prestador_categorias pc ON pc.prestador_id = p.id
        LEFT JOIN servicos s ON s.prestador_id = p.id
        WHERE p.aprovado = true
          AND p.visivel = true
          AND (:categoriaId IS NULL OR pc.categoria_id = :categoriaId)
          AND (:cidade IS NULL OR LOWER(CAST(p.cidade AS text)) LIKE LOWER(CONCAT('%', CAST(:cidade AS text), '%')))
          AND (:estado IS NULL OR LOWER(CAST(p.estado AS text)) = LOWER(CAST(:estado AS text)))
          AND (:texto IS NULL OR
               LOWER(CAST(p.descricao AS text)) LIKE LOWER(CONCAT('%', CAST(:texto AS text), '%')) OR
               LOWER(CAST(u.nome AS text)) LIKE LOWER(CONCAT('%', CAST(:texto AS text), '%')) OR
               LOWER(CAST(s.titulo AS text)) LIKE LOWER(CONCAT('%', CAST(:texto AS text), '%')) OR
               LOWER(CAST(s.descricao AS text)) LIKE LOWER(CONCAT('%', CAST(:texto AS text), '%')))
        """, nativeQuery = true)
    List<Prestador> buscarPublico(
            @Param("categoriaId") Long categoriaId,
            @Param("cidade") String cidade,
            @Param("estado") String estado,
            @Param("texto") String texto
    );
}
