package com.example.Trabo.service;

import com.example.Trabo.dto.request.ServicoRequest;
import com.example.Trabo.dto.response.ServicoResponse;
import com.example.Trabo.exception.NotFoundException;
import com.example.Trabo.model.entity.Prestador;
import com.example.Trabo.model.entity.Servico;
import com.example.Trabo.model.entity.Usuario;
import com.example.Trabo.repository.PrestadorRepository;
import com.example.Trabo.repository.ServicoRepository;
import com.example.Trabo.repository.UsuarioRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ServicoService {

    private final ServicoRepository servicoRepository;
    private final PrestadorRepository prestadorRepository;
    private final UsuarioRepository usuarioRepository;
    private final FileService fileService;

    public ServicoService(ServicoRepository servicoRepository, 
                          PrestadorRepository prestadorRepository,
                          UsuarioRepository usuarioRepository,
                          FileService fileService) {
        this.servicoRepository = servicoRepository;
        this.prestadorRepository = prestadorRepository;
        this.usuarioRepository = usuarioRepository;
        this.fileService = fileService;
    }

    private Prestador getPrestadorLogado() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("Usuário não encontrado"));
        return prestadorRepository.findByUsuarioId(usuario.getId())
                .orElseThrow(() -> new NotFoundException("Perfil de prestador não encontrado"));
    }

    public List<ServicoResponse> listarMeusServicos() {
        Prestador p = getPrestadorLogado();
        return servicoRepository.findByPrestadorId(p.getId())
                .stream().map(this::toResponse).toList();
    }

    @Transactional
    public ServicoResponse criar(ServicoRequest request) {
        Prestador p = getPrestadorLogado();
        Servico s = new Servico(p, request.titulo(), request.descricao(), request.preco());
        return toResponse(servicoRepository.save(s));
    }

    @Transactional
    public ServicoResponse adicionarFoto(Long id, org.springframework.web.multipart.MultipartFile file) {
        Prestador p = getPrestadorLogado();
        Servico s = servicoRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Serviço não encontrado"));
        
        if (!s.getPrestador().getId().equals(p.getId())) {
            throw new RuntimeException("Acesso negado");
        }

        String url = fileService.salvarArquivo(file);
        s.getFotos().add(url);
        return toResponse(servicoRepository.save(s));
    }

    @Transactional
    public void excluir(Long id) {
        Prestador p = getPrestadorLogado();
        Servico s = servicoRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Serviço não encontrado"));
        
        if (!s.getPrestador().getId().equals(p.getId())) {
            throw new RuntimeException("Acesso negado");
        }
        
        servicoRepository.delete(s);
    }

    private ServicoResponse toResponse(Servico s) {
        return new ServicoResponse(s.getId(), s.getTitulo(), s.getDescricao(), s.getPreco(), s.getFotos());
    }
}
