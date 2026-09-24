package com.example.Trabo.service;


import com.example.Trabo.dto.request.PortfolioRequest;
import com.example.Trabo.dto.response.CategoriaResponse;
import com.example.Trabo.dto.response.PacotePrecoResponse;
import com.example.Trabo.dto.response.PrestadorResponse;
import com.example.Trabo.dto.response.ServicoResponse;
import com.example.Trabo.exception.BusinessException;
import com.example.Trabo.exception.NotFoundException;
import com.example.Trabo.model.entity.Categoria;
import com.example.Trabo.model.entity.Notificacao;
import com.example.Trabo.model.entity.PacotePreco;
import com.example.Trabo.model.entity.Prestador;
import com.example.Trabo.model.entity.Servico;
import com.example.Trabo.model.entity.Usuario;
import com.example.Trabo.repository.CategoriaRepository;
import com.example.Trabo.repository.NotificacaoRepository;
import com.example.Trabo.repository.PrestadorRepository;
import com.example.Trabo.repository.UsuarioRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class PrestadorService {

    private final PrestadorRepository prestadorRepository;
    private final CategoriaRepository categoriaRepository;
    private final UsuarioRepository usuarioRepository;
    private final NotificacaoRepository notificacaoRepository;

    public PrestadorService(PrestadorRepository prestadorRepository,
                            CategoriaRepository categoriaRepository,
                            UsuarioRepository usuarioRepository,
                            NotificacaoRepository notificacaoRepository) {
        this.prestadorRepository = prestadorRepository;
        this.categoriaRepository = categoriaRepository;
        this.usuarioRepository = usuarioRepository;
        this.notificacaoRepository = notificacaoRepository;
    }

    public List<PrestadorResponse> buscarPublico(Long categoriaId, String cidade, String estado, String texto) {
        return prestadorRepository.buscarPublico(categoriaId, cidade, estado, texto)
                .stream().map(this::toResponse).toList();
    }

    public PrestadorResponse buscarPorId(Long id) {
        Prestador p = prestadorRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Prestador não encontrado: " + id));

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String visitante = "Alguém";
        boolean isOwner = false;

        if (auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser")) {
            try {
                Usuario u = usuarioRepository.findByEmail(auth.getName()).orElse(null);
                if (u != null) {
                    if (u.getId().equals(p.getUsuario().getId())) {
                        isOwner = true;
                    } else {
                        visitante = u.getNome();
                    }
                }
            } catch (Exception e) {}
        }

        if (!isOwner) {
            notificacaoRepository.save(new Notificacao(p.getUsuario(), visitante + " visitou seu perfil", "VIEW"));
        }

        return toResponse(p);
    }

    @Transactional
    public void favoritar(Long prestadorId) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Usuario usuarioLogado = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("Usuário não encontrado"));
        Prestador prestador = prestadorRepository.findById(prestadorId)
                .orElseThrow(() -> new NotFoundException("Prestador não encontrado"));

        if (usuarioLogado.getFavoritos().contains(prestador)) {
            usuarioLogado.getFavoritos().remove(prestador);
        } else {
            usuarioLogado.getFavoritos().add(prestador);
            notificacaoRepository.save(new Notificacao(
                    prestador.getUsuario(), 
                    usuarioLogado.getNome() + " curtiu seu perfil", 
                    "LIKE"
            ));
        }
        usuarioRepository.save(usuarioLogado);
    }



    @Transactional
    public PrestadorResponse atualizarPortfolio(PortfolioRequest request) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("Usuário não encontrado"));
        Prestador prestador = prestadorRepository.findByUsuarioId(usuario.getId())
                .orElseThrow(() -> new NotFoundException("Perfil de prestador não encontrado"));

        prestador.setDescricao(request.descricao());
        prestador.setEstado(request.estado());
        prestador.setCidade(request.cidade());
        prestador.setEnderecoCompleto(request.enderecoCompleto());
        prestador.setWhatsapp(request.whatsapp());
        prestador.setEmailContato(request.emailContato());

        prestador.getServicos().clear();
        if (request.servicos() != null) {
            request.servicos().forEach(s -> {
                prestador.getServicos().add(new Servico(prestador, s.titulo(), s.descricao()));
            });
        }

        prestador.getPacotesPrecos().clear();
        if (request.pacotes() != null) {
            request.pacotes().forEach(p -> {
                prestador.getPacotesPrecos().add(new PacotePreco(prestador, p.pacote(), p.preco(), p.dias(), p.horario()));
            });
        }

        prestador.getGaleria().clear();
        if (request.galeria() != null) {
            prestador.getGaleria().addAll(request.galeria());
        }
        
        prestador.setAprovado(true);
        prestador.setVisivel(request.visivel() != null ? request.visivel() : true);

        return toResponse(prestadorRepository.save(prestador));
    }

    public PrestadorResponse toResponse(Prestador p) {
        return new PrestadorResponse(
                p.getId(),
                p.getUsuario().getNome(),
                p.getUsuario().getEmail(),
                p.getEmailContato(),
                p.getCidade(),
                p.getEstado(),
                p.getBairro(),
                p.getEnderecoCompleto(),
                p.getDescricao(),
                p.getTelefone(),
                p.getWhatsapp(),
                p.getUsuario().getFotoPerfil(),
                p.isAprovado(),
                p.getMediaAvaliacoes(),
                p.getAvaliacoes() != null ? p.getAvaliacoes().size() : 0,
                p.getCategorias().stream().map(c -> new CategoriaResponse(c.getId(), c.getNome(), c.getIcone(), c.getDescricao())).toList(),
                p.getGaleria() != null ? p.getGaleria() : new ArrayList<>(),
                p.getServicos() != null ? p.getServicos().stream().map(s -> new ServicoResponse(s.getId(), s.getTitulo(), s.getDescricao())).toList() : List.of(),
                p.getPacotesPrecos() != null ? p.getPacotesPrecos().stream().map(pp -> new PacotePrecoResponse(pp.getId(), pp.getPacote(), pp.getPreco(), pp.getDias(), pp.getHorario())).toList() : List.of(),
                p.isVisivel()
        );
    }
}
