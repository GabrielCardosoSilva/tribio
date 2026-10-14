package com.example.Trabo.service;

import com.example.Trabo.dto.response.PerfilUsuarioResponse;
import com.example.Trabo.exception.NotFoundException;
import com.example.Trabo.model.entity.Usuario;
import com.example.Trabo.repository.PrestadorRepository;
import com.example.Trabo.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PrestadorRepository prestadorRepository;
    private final FileService fileService;
    private final AuditoriaService auditoriaService;
    private final UsuarioAutenticadoService usuarioAutenticadoService;

    public UsuarioService(UsuarioRepository usuarioRepository,
                          PrestadorRepository prestadorRepository,
                          FileService fileService,
                          AuditoriaService auditoriaService,
                          UsuarioAutenticadoService usuarioAutenticadoService) {
        this.usuarioRepository = usuarioRepository;
        this.prestadorRepository = prestadorRepository;
        this.fileService = fileService;
        this.auditoriaService = auditoriaService;
        this.usuarioAutenticadoService = usuarioAutenticadoService;
    }

    public PerfilUsuarioResponse getMe(String remoteAddr) {
        Usuario u = usuarioAutenticadoService.getUsuario();
        auditoriaService.registrar(u.getEmail(), "ACESSO_DADOS_PESSOAIS", "GET /api/usuarios/me", remoteAddr);

        return new PerfilUsuarioResponse(
                u.getId(),
                u.getNome(),
                u.getEmail(),
                u.getRole(),
                u.getFotoPerfil() != null ? u.getFotoPerfil() : ""
        );
    }

    @Transactional
    public String uploadFotoPerfil(MultipartFile file, String remoteAddr) {
        Usuario u = usuarioAutenticadoService.getUsuario();
        String url = fileService.salvarArquivo(file);
        u.setFotoPerfil(url);
        usuarioRepository.save(u);

        auditoriaService.registrar(u.getEmail(), "UPLOAD_FOTO_PERFIL", "Foto atualizada: " + url, remoteAddr);
        return url;
    }

    /**
     * Exclusão de conta — Direito ao esquecimento (LGPD Art. 18, VI).
     * Remove todos os dados pessoais do usuário e do prestador associado (se houver).
     */
    @Transactional
    public void excluirConta(String remoteAddr) {
        Usuario u = usuarioAutenticadoService.getUsuario();

        // Registra auditoria ANTES de deletar (para manter o histórico de conformidade)
        auditoriaService.registrar(u.getEmail(), "EXCLUSAO_CONTA",
                "Conta excluída a pedido do usuário (LGPD Art. 18)", remoteAddr);

        // Remove prestador associado (cascade remove os serviços, fotos, avaliações)
        if (u.getPrestador() != null) {
            prestadorRepository.delete(u.getPrestador());
        }

        usuarioRepository.delete(u);
    }
}
