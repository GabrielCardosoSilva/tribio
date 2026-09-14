package com.example.Trabo.model.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "fotos_trabalho")
public class FotoTrabalho {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prestador_id", nullable = false)
    private Prestador prestador;

    @Column(nullable = false)
    private String url;

    private String descricao;

    // ── Constructors ──────────────────────────────
    public FotoTrabalho() {}

    public FotoTrabalho(Long id, Prestador prestador, String url, String descricao) {
        this.id = id;
        this.prestador = prestador;
        this.url = url;
        this.descricao = descricao;
    }

    // ── Getters & Setters ─────────────────────────
    public Long getId()                          { return id; }
    public void setId(Long id)                   { this.id = id; }

    public Prestador getPrestador()              { return prestador; }
    public void      setPrestador(Prestador p)  { this.prestador = p; }

    public String getUrl()                       { return url; }
    public void   setUrl(String url)             { this.url = url; }

    public String getDescricao()                 { return descricao; }
    public void   setDescricao(String d)         { this.descricao = d; }
}
