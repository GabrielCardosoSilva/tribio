package com.example.Trabo.model.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "pacotes_precos")
public class PacotePreco {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prestador_id", nullable = false)
    private Prestador prestador;

    private String pacote;
    private String preco;
    private String dias;
    private String horario;

    public PacotePreco() {}

    public PacotePreco(Prestador prestador, String pacote, String preco, String dias, String horario) {
        this.prestador = prestador;
        this.pacote = pacote;
        this.preco = preco;
        this.dias = dias;
        this.horario = horario;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Prestador getPrestador() { return prestador; }
    public void setPrestador(Prestador prestador) { this.prestador = prestador; }
    
    public String getPacote() { return pacote; }
    public void setPacote(String pacote) { this.pacote = pacote; }
    
    public String getPreco() { return preco; }
    public void setPreco(String preco) { this.preco = preco; }
    
    public String getDias() { return dias; }
    public void setDias(String dias) { this.dias = dias; }
    
    public String getHorario() { return horario; }
    public void setHorario(String horario) { this.horario = horario; }
}
