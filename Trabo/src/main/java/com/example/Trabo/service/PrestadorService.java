package com.example.Trabo.service;

import com.example.Trabo.dto.request.AtualizarPrestadorRequest;
import com.example.Trabo.dto.response.CategoriaResponse;
import com.example.Trabo.dto.response.PrestadorResponse;
import com.example.Trabo.dto.response.ServicoResponse;
import com.example.Trabo.exception.BusinessException;
import com.example.Trabo.exception.NotFoundException;
import com.example.Trabo.model.entity.Categoria;
import com.example.Trabo.model.entity.Prestador;
import com.example.Trabo.model.entity.Usuario;
import com.example.Trabo.repository.CategoriaRepository;
import com.example.Trabo.repository.PrestadorRepository;
import com.example.Trabo.repository.UsuarioRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PrestadorService {

    private final PrestadorRepository prestadorRepository;
    private final CategoriaRepository categoriaRepository;
    private final UsuarioRepository usuarioRepository;

    public PrestadorService(PrestadorRepository prestadorRepository,
                            CategoriaRepository categoriaRepository,
                            UsuarioRepository usuarioRepository) {
        this.prestadorRepository = prestadorRepository;
        this.categoriaRepository = categoriaRepository;
        this.usuarioRepository = usuarioRepository;
    }

    public List<PrestadorResponse> buscarPublico(Long categoriaId, String cidade, String estado, String texto) {
        return prestadorRepository.buscarPublico(categoriaId, cidade, estado, texto)
                .stream().map(this::toResponse).toList();
    }

    public PrestadorResponse buscarPorId(Long id) {
        Prestador p = prestadorRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Prestador não encontrado: " + id));
        return toResponse(p);
    }

    @Transactional
    public PrestadorResponse atualizarPerfil(AtualizarPrestadorRequest request) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("Usuário não encontrado"));
        Prestador prestador = prestadorRepository.findByUsuarioId(usuario.getId())
                .orElseThrow(() -> new NotFoundException("Perfil de prestador não encontrado"));

        prestador.setCidade(request.cidade());
        prestador.setEstado(request.estado());
        prestador.setBairro(request.bairro());
        prestador.setDescricao(request.descricao());
        prestador.setTelefone(request.telefone());
        prestador.setWhatsapp(request.whatsapp());

        if (request.categoriaIds() != null && !request.categoriaIds().isEmpty()) {
            List<Categoria> cats = categoriaRepository.findAllById(request.categoriaIds());
            if (cats.size() != request.categoriaIds().size()) {
                throw new BusinessException("Uma ou mais categorias informadas não existem");
            }
            prestador.getCategorias().clear();
            prestador.getCategorias().addAll(cats);
        }

        return toResponse(prestadorRepository.save(prestador));
    }

    public PrestadorResponse toResponse(Prestador p) {
        return new PrestadorResponse(
                p.getId(),
                p.getUsuario().getNome(),
                p.getUsuario().getEmail(),
                p.getCidade(),
                p.getEstado(),
                p.getBairro(),
                p.getDescricao(),
                p.getTelefone(),
                p.getWhatsapp(),
                p.getUsuario().getFotoPerfil(),
                p.isAprovado(),
                p.getMediaAvaliacoes(),
                p.getAvaliacoes() != null ? p.getAvaliacoes().size() : 0,
                p.getCategorias().stream().map(c -> new CategoriaResponse(c.getId(), c.getNome(), c.getIcone(), c.getDescricao())).toList(),
                p.getFotos().stream().map(f -> f.getUrl()).toList(),
                p.getServicos() != null ? p.getServicos().stream().map(s -> new ServicoResponse(s.getId(), s.getTitulo(), s.getDescricao(), s.getOndeAtende(), s.getPreco(), s.getFotos())).toList() : List.of()
        );
    }
}
