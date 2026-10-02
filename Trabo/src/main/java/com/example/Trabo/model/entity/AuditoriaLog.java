package com.example.Trabo.model.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Registro de auditoria para acesso e operações em dados sensíveis.
 * Cumpre requisito LGPD de rastreabilidade (Art. 37).
 */
@Entity
@Table(name = "auditoria_logs")
public class AuditoriaLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** E-mail (ou "anônimo") do usuário que realizou a ação */
    @Column(nullable = false)
    private String usuario;

    /** Descrição da ação executada, ex: "EXCLUSAO_CONTA", "ACESSO_PERFIL" */
    @Column(nullable = false)
    private String acao;

    /** Detalhes adicionais opcionais */
    @Column(length = 500)
    private String detalhes;

    /** IP de origem da requisição */
    private String ip;

    @Column(nullable = false, updatable = false)
    private LocalDateTime dataHora = LocalDateTime.now();

    public AuditoriaLog() {}

    public AuditoriaLog(String usuario, String acao, String detalhes, String ip) {
        this.usuario = usuario;
        this.acao = acao;
        this.detalhes = detalhes;
        this.ip = ip;
        this.dataHora = LocalDateTime.now();
    }

    // ── Getters ───────────────────────────────────────────────
    public Long getId()              { return id; }
    public String getUsuario()       { return usuario; }
    public String getAcao()          { return acao; }
    public String getDetalhes()      { return detalhes; }
    public String getIp()            { return ip; }
    public LocalDateTime getDataHora() { return dataHora; }

    // ── Setters ───────────────────────────────────────────────
    public void setId(Long id)             { this.id = id; }
    public void setUsuario(String u)       { this.usuario = u; }
    public void setAcao(String a)          { this.acao = a; }
    public void setDetalhes(String d)      { this.detalhes = d; }
    public void setIp(String ip)           { this.ip = ip; }
    public void setDataHora(LocalDateTime t) { this.dataHora = t; }
}
