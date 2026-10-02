package com.example.Trabo.service;

import com.example.Trabo.model.entity.AuditoriaLog;
import com.example.Trabo.repository.AuditoriaRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * Serviço de auditoria para registrar acessos e operações em dados sensíveis.
 * Requisito LGPD Art. 37 — rastreabilidade de operações com dados pessoais.
 *
 * O método é @Async para não impactar a performance das requisições.
 */
@Service
public class AuditoriaService {

    private final AuditoriaRepository auditoriaRepository;

    public AuditoriaService(AuditoriaRepository auditoriaRepository) {
        this.auditoriaRepository = auditoriaRepository;
    }

    /**
     * Registra uma entrada de auditoria de forma assíncrona.
     *
     * @param usuario  E-mail do usuário que realizou a ação
     * @param acao     Código da ação (ex: "EXCLUSAO_CONTA", "LOGIN", "ACESSO_DADOS")
     * @param detalhes Informações adicionais sobre a ação
     * @param ip       Endereço IP de origem da requisição
     */
    @Async
    public void registrar(String usuario, String acao, String detalhes, String ip) {
        try {
            AuditoriaLog log = new AuditoriaLog(usuario, acao, detalhes, ip);
            auditoriaRepository.save(log);
        } catch (Exception e) {
            // Não propagar erro de auditoria — operação principal não deve falhar por isso
            System.err.println("[AUDITORIA] Falha ao registrar log: " + e.getMessage());
        }
    }
}
