package com.example.Trabo.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

@Entity
@Table(name = "avaliacoes")
public class Avaliacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prestador_id", nullable = false)
    private Prestador prestador;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @NotNull
    @Min(1)
    @Max(5)
    @Column(nullable = false)
    private Integer nota;

    @Column(columnDefinition = "TEXT")
    private String comentario;

    @Column(updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    // ── Constructors ──────────────────────────────
    public Avaliacao() {}

    // ── Builder ───────────────────────────────────
    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Prestador prestador;
        private Usuario usuario;
        private Integer nota;
        private String comentario;

        public Builder prestador(Prestador p)    { this.prestador = p; return this; }
        public Builder usuario(Usuario u)        { this.usuario = u; return this; }
        public Builder nota(Integer n)           { this.nota = n; return this; }
        public Builder comentario(String c)      { this.comentario = c; return this; }

        public Avaliacao build() {
            Avaliacao a = new Avaliacao();
            a.prestador = this.prestador;
            a.usuario = this.usuario;
            a.nota = this.nota;
            a.comentario = this.comentario;
            return a;
        }
    }

    // ── Getters & Setters ─────────────────────────
    public Long getId()                              { return id; }
    public void setId(Long id)                       { this.id = id; }

    public Prestador getPrestador()                  { return prestador; }
    public void      setPrestador(Prestador p)       { this.prestador = p; }

    public Usuario getUsuario()                      { return usuario; }
    public void    setUsuario(Usuario u)             { this.usuario = u; }

    public Integer getNota()                         { return nota; }
    public void    setNota(Integer nota)             { this.nota = nota; }

    public String getComentario()                    { return comentario; }
    public void   setComentario(String comentario)   { this.comentario = comentario; }

    public LocalDateTime getCreatedAt()                  { return createdAt; }
    public void          setCreatedAt(LocalDateTime t)   { this.createdAt = t; }
}
