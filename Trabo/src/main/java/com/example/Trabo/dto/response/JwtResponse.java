package com.example.Trabo.dto.response;

public record JwtResponse(
        String token,
        String tipo,
        String nome,
        String role
) {
    public JwtResponse(String token, String nome, String role) {
        this(token, "Bearer", nome, role);
    }
}
