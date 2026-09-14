package com.example.Trabo.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "categorias")
public class Categoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false, unique = true)
    private String nome;

    private String icone;

    private String descricao;

    // ── Constructors ──────────────────────────────
    public Categoria() {}

    public Categoria(Long id, String nome, String icone, String descricao) {
        this.id = id;
        this.nome = nome;
        this.icone = icone;
        this.descricao = descricao;
    }

    // ── Builder ───────────────────────────────────
    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Long id;
        private String nome;
        private String icone;
        private String descricao;

        public Builder id(Long id)               { this.id = id; return this; }
        public Builder nome(String nome)         { this.nome = nome; return this; }
        public Builder icone(String icone)       { this.icone = icone; return this; }
        public Builder descricao(String desc)    { this.descricao = desc; return this; }

        public Categoria build() {
            Categoria c = new Categoria();
            c.id = this.id;
            c.nome = this.nome;
            c.icone = this.icone;
            c.descricao = this.descricao;
            return c;
        }
    }

    // ── Getters & Setters ─────────────────────────
    public Long getId()                          { return id; }
    public void setId(Long id)                   { this.id = id; }

    public String getNome()                      { return nome; }
    public void   setNome(String nome)           { this.nome = nome; }

    public String getIcone()                     { return icone; }
    public void   setIcone(String icone)         { this.icone = icone; }

    public String getDescricao()                 { return descricao; }
    public void   setDescricao(String descricao) { this.descricao = descricao; }
}
