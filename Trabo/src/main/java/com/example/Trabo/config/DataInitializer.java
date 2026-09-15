package com.example.Trabo.config;

import com.example.Trabo.model.entity.Prestador;
import com.example.Trabo.model.entity.Usuario;
import com.example.Trabo.model.enums.Role;
import com.example.Trabo.repository.PrestadorRepository;
import com.example.Trabo.repository.UsuarioRepository;
import com.example.Trabo.model.entity.Servico;
import com.example.Trabo.repository.ServicoRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initData(UsuarioRepository usuarioRepository, 
                               PrestadorRepository prestadorRepository, 
                               ServicoRepository servicoRepository,
                               PasswordEncoder passwordEncoder) {
        return args -> {
            // Apenas inicializar se o banco estiver vazio
            if (usuarioRepository.count() == 0) {
                // Cliente de teste
                Usuario cliente = Usuario.builder()
                        .nome("Cliente de Teste")
                        .email("cliente@teste.com")
                        .senhaHash(passwordEncoder.encode("senha123"))
                        .role(Role.USUARIO)
                        .ativo(true)
                        .build();
                usuarioRepository.save(cliente);

                // Admin
                Usuario admin = Usuario.builder()
                        .nome("Administrador")
                        .email("adm@trabio.com")
                        .senhaHash(passwordEncoder.encode("123456"))
                        .role(Role.ADMIN)
                        .ativo(true)
                        .build();
                usuarioRepository.save(admin);

                // Prestador 1
                Usuario prof1 = Usuario.builder()
                        .nome("João Eletricista")
                        .email("joao@teste.com")
                        .senhaHash(passwordEncoder.encode("senha123"))
                        .role(Role.PRESTADOR)
                        .ativo(true)
                        .build();
                usuarioRepository.save(prof1);

                Prestador prestador1 = Prestador.builder()
                        .usuario(prof1)
                        .cidade("São Paulo")
                        .estado("SP")
                        .bairro("Centro")
                        .descricao("Eletricista residencial e predial com 10 anos de experiência. Instalações, manutenções e reparos em geral.")
                        .telefone("11999999999")
                        .whatsapp("11999999999")
                        .aprovado(true) // Já aprovado para aparecer na busca
                        .build();
                prestadorRepository.save(prestador1);

                // Prestador 2
                Usuario prof2 = Usuario.builder()
                        .nome("Maria Encanadora")
                        .email("maria@teste.com")
                        .senhaHash(passwordEncoder.encode("senha123"))
                        .role(Role.PRESTADOR)
                        .ativo(true)
                        .build();
                usuarioRepository.save(prof2);

                Prestador prestador2 = Prestador.builder()
                        .usuario(prof2)
                        .cidade("São Paulo")
                        .estado("SP")
                        .bairro("Pinheiros")
                        .descricao("Especialista em detecção de vazamentos e instalação de tubulações hidráulicas residenciais e comerciais.")
                        .telefone("11988888888")
                        .whatsapp("11988888888")
                        .aprovado(true)
                        .build();
                prestadorRepository.save(prestador2);
                
                // Prestador 3
                Usuario prof3 = Usuario.builder()
                        .nome("Carlos Pintor")
                        .email("carlos@teste.com")
                        .senhaHash(passwordEncoder.encode("senha123"))
                        .role(Role.PRESTADOR)
                        .ativo(true)
                        .build();
                usuarioRepository.save(prof3);

                Prestador prestador3 = Prestador.builder()
                        .usuario(prof3)
                        .cidade("Rio de Janeiro")
                        .estado("RJ")
                        .bairro("Copacabana")
                        .descricao("Pinturas finas, texturas, grafiato e reformas gerais. Compromisso com o prazo e a limpeza do ambiente.")
                        .telefone("21977777777")
                        .whatsapp("21977777777")
                        .aprovado(true)
                        .build();
                prestadorRepository.save(prestador3);
                // --- Serviços Mock ---
                Servico servico1 = new Servico(prestador1, "Instalação Elétrica Completa", "Instalação de rede elétrica, disjuntores e tomadas para toda a residência.", new java.math.BigDecimal("850.00"), "São Paulo (Zonas Sul e Oeste)");
                servicoRepository.save(servico1);

                Servico servico2 = new Servico(prestador2, "Caça Vazamentos", "Identificação e reparo de vazamentos em tubulações com equipamentos de ultrassom.", new java.math.BigDecimal("250.00"), "Atendimento em Domicílio");
                servicoRepository.save(servico2);
                
                Servico servico3 = new Servico(prestador3, "Pintura Interna", "Pintura de paredes e teto com fino acabamento.", new java.math.BigDecimal("1200.00"), "Somente Rio de Janeiro - Capital");
                servicoRepository.save(servico3);
            }
        };
    }
}
