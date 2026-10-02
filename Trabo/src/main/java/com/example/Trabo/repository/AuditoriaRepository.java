package com.example.Trabo.repository;

import com.example.Trabo.model.entity.AuditoriaLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuditoriaRepository extends JpaRepository<AuditoriaLog, Long> {
    List<AuditoriaLog> findByUsuarioOrderByDataHoraDesc(String usuario);
}
