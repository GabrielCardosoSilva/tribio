package com.example.Trabo.service;

import com.example.Trabo.dto.request.AvaliacaoRequest;
import com.example.Trabo.dto.response.AvaliacaoResponse;
import com.example.Trabo.exception.BusinessException;
import com.example.Trabo.exception.NotFoundException;
import com.example.Trabo.model.entity.Avaliacao;
import com.example.Trabo.model.entity.Prestador;
import com.example.Trabo.model.entity.Usuario;
import com.example.Trabo.repository.AvaliacaoRepository;
import com.example.Trabo.repository.PrestadorRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AvaliacaoService {

    private final AvaliacaoRepository avaliacaoRepository;
    private final PrestadorRepository prestadorRepository;
    private final UsuarioAutenticadoService usuarioAutenticadoService;

    public AvaliacaoService(AvaliacaoRepository avaliacaoRepository,
                            PrestadorRepository prestadorRepository,
                            UsuarioAutenticadoService usuarioAutenticadoService) {
        this.avaliacaoRepository = avaliacaoRepository;
        this.prestadorRepository = prestadorRepository;
        this.usuarioAutenticadoService = usuarioAutenticadoService;
    }

    @Transactional
    public AvaliacaoResponse avaliar(Long prestadorId, AvaliacaoRequest request) {
        Usuario usuario = usuarioAutenticadoService.getUsuario();

        Prestador prestador = prestadorRepository.findById(prestadorId)
                .orElseThrow(() -> new NotFoundException("Prestador não encontrado: " + prestadorId));

        if (avaliacaoRepository.existsByPrestadorIdAndUsuarioId(prestadorId, usuario.getId())) {
            throw new BusinessException("Você já avaliou este prestador");
        }

        Avaliacao avaliacao = Avaliacao.builder()
                .prestador(prestador)
                .usuario(usuario)
                .nota(request.nota())
                .comentario(request.comentario())
                .build();

        avaliacao = avaliacaoRepository.save(avaliacao);
        return toResponse(avaliacao);
    }

    public List<AvaliacaoResponse> listarPorPrestador(Long prestadorId) {
        if (!prestadorRepository.existsById(prestadorId)) {
            throw new NotFoundException("Prestador não encontrado: " + prestadorId);
        }
        return avaliacaoRepository.findByPrestadorId(prestadorId)
                .stream().map(this::toResponse).toList();
    }

    private AvaliacaoResponse toResponse(Avaliacao a) {
        return new AvaliacaoResponse(
                a.getId(),
                a.getUsuario().getNome(),
                a.getNota(),
                a.getComentario(),
                a.getCreatedAt()
        );
    }
}
