package com.example.Trabo.model.entity;

import com.example.Trabo.model.enums.Role;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;

@Entity
@Table(name = "usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false)
    private String nome;

    @Email
    @NotBlank
    @Column(nullable = false, unique = true)
    private String email;

    @NotBlank
    @Column(nullable = false)
    private String senhaHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Column(nullable = false)
    private boolean ativo = true;

    @Column(updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    private String fotoPerfil;

    @OneToOne(mappedBy = "usuario", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Prestador prestador;

    // ── Constructors ──────────────────────────────────────────
    public Usuario() {}

    public Usuario(Long id, String nome, String email, String senhaHash, Role role, boolean ativo, LocalDateTime createdAt) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.senhaHash = senhaHash;
        this.role = role;
        this.ativo = ativo;
        this.createdAt = createdAt;
    }

    // ── Builder ───────────────────────────────────────────────
    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Long id;
        private String nome;
        private String email;
        private String senhaHash;
        private Role role;
        private boolean ativo = true;
        private LocalDateTime createdAt = LocalDateTime.now();
        private String fotoPerfil;

        public Builder id(Long id)                   { this.id = id; return this; }
        public Builder nome(String nome)             { this.nome = nome; return this; }
        public Builder email(String email)           { this.email = email; return this; }
        public Builder senhaHash(String senhaHash)   { this.senhaHash = senhaHash; return this; }
        public Builder role(Role role)               { this.role = role; return this; }
        public Builder ativo(boolean ativo)          { this.ativo = ativo; return this; }
        public Builder createdAt(LocalDateTime t)    { this.createdAt = t; return this; }
        public Builder fotoPerfil(String f)          { this.fotoPerfil = f; return this; }

        public Usuario build() {
            Usuario u = new Usuario();
            u.id = this.id;
            u.nome = this.nome;
            u.email = this.email;
            u.senhaHash = this.senhaHash;
            u.role = this.role;
            u.ativo = this.ativo;
            u.createdAt = this.createdAt;
            u.fotoPerfil = this.fotoPerfil;
            return u;
        }
    }

    // ── Getters & Setters ─────────────────────────────────────
    public Long getId()              { return id; }
    public void setId(Long id)       { this.id = id; }

    public String getNome()                  { return nome; }
    public void   setNome(String nome)       { this.nome = nome; }

    public String getEmail()                 { return email; }
    public void   setEmail(String email)     { this.email = email; }

    public String getSenhaHash()                     { return senhaHash; }
    public void   setSenhaHash(String senhaHash)     { this.senhaHash = senhaHash; }

    public Role getRole()              { return role; }
    public void setRole(Role role)     { this.role = role; }

    public boolean isAtivo()             { return ativo; }
    public void    setAtivo(boolean a)   { this.ativo = a; }

    public LocalDateTime getCreatedAt()                  { return createdAt; }
    public void          setCreatedAt(LocalDateTime t)   { this.createdAt = t; }

    public String getFotoPerfil()                { return fotoPerfil; }
    public void   setFotoPerfil(String f)        { this.fotoPerfil = f; }

    public Prestador getPrestador()                { return prestador; }
    public void      setPrestador(Prestador p)    { this.prestador = p; }
}
