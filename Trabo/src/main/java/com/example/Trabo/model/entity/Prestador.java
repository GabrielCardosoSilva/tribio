package com.example.Trabo.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "prestadores")
public class Prestador {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false, unique = true)
    private Usuario usuario;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "prestador_categorias",
        joinColumns = @JoinColumn(name = "prestador_id"),
        inverseJoinColumns = @JoinColumn(name = "categoria_id")
    )
    private List<Categoria> categorias = new ArrayList<>();

    @NotBlank
    @Column(nullable = false)
    private String cidade;

    private String bairro;

    @Column(columnDefinition = "TEXT")
    private String descricao;

    private String telefone;

    private String whatsapp;

    @Column(nullable = false)
    private boolean aprovado = false;

    @Column(updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @OneToMany(mappedBy = "prestador", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FotoTrabalho> fotos = new ArrayList<>();

    @OneToMany(mappedBy = "prestador", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Avaliacao> avaliacoes = new ArrayList<>();

    @OneToMany(mappedBy = "prestador", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Servico> servicos = new ArrayList<>();

    // ── Constructors ──────────────────────────────
    public Prestador() {}

    // ── Builder ───────────────────────────────────
    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Usuario usuario;
        private String cidade;
        private String bairro;
        private String descricao;
        private String telefone;
        private String whatsapp;
        private boolean aprovado = false;

        public Builder usuario(Usuario u)        { this.usuario = u; return this; }
        public Builder cidade(String c)          { this.cidade = c; return this; }
        public Builder bairro(String b)          { this.bairro = b; return this; }
        public Builder descricao(String d)       { this.descricao = d; return this; }
        public Builder telefone(String t)        { this.telefone = t; return this; }
        public Builder whatsapp(String w)        { this.whatsapp = w; return this; }
        public Builder aprovado(boolean a)       { this.aprovado = a; return this; }

        public Prestador build() {
            Prestador p = new Prestador();
            p.usuario = this.usuario;
            p.cidade = this.cidade;
            p.bairro = this.bairro;
            p.descricao = this.descricao;
            p.telefone = this.telefone;
            p.whatsapp = this.whatsapp;
            p.aprovado = this.aprovado;
            return p;
        }
    }

    // ── Helper ────────────────────────────────────
    public Double getMediaAvaliacoes() {
        if (avaliacoes == null || avaliacoes.isEmpty()) return null;
        return avaliacoes.stream().mapToInt(Avaliacao::getNota).average().orElse(0.0);
    }

    // ── Getters & Setters ─────────────────────────
    public Long getId()                              { return id; }
    public void setId(Long id)                       { this.id = id; }

    public Usuario getUsuario()                      { return usuario; }
    public void    setUsuario(Usuario u)             { this.usuario = u; }

    public List<Categoria> getCategorias()               { return categorias; }
    public void            setCategorias(List<Categoria> c) { this.categorias = c; }

    public String getCidade()                        { return cidade; }
    public void   setCidade(String cidade)           { this.cidade = cidade; }

    public String getBairro()                        { return bairro; }
    public void   setBairro(String bairro)           { this.bairro = bairro; }

    public String getDescricao()                     { return descricao; }
    public void   setDescricao(String descricao)     { this.descricao = descricao; }

    public String getTelefone()                      { return telefone; }
    public void   setTelefone(String telefone)       { this.telefone = telefone; }

    public String getWhatsapp()                      { return whatsapp; }
    public void   setWhatsapp(String whatsapp)       { this.whatsapp = whatsapp; }

    public boolean isAprovado()                      { return aprovado; }
    public void    setAprovado(boolean aprovado)     { this.aprovado = aprovado; }

    public LocalDateTime getCreatedAt()                  { return createdAt; }
    public void          setCreatedAt(LocalDateTime t)   { this.createdAt = t; }

    public List<FotoTrabalho> getFotos()                         { return fotos; }
    public void               setFotos(List<FotoTrabalho> fotos) { this.fotos = fotos; }

    public List<Avaliacao> getAvaliacoes()                     { return avaliacoes; }
    public void            setAvaliacoes(List<Avaliacao> avs)  { this.avaliacoes = avs; }

    public List<Servico> getServicos() { return servicos; }
    public void setServicos(List<Servico> servicos) { this.servicos = servicos; }
}
