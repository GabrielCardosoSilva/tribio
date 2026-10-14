package com.example.Trabo.service;

import com.example.Trabo.dto.request.CategoriaRequest;
import com.example.Trabo.dto.response.CategoriaResponse;
import com.example.Trabo.exception.BusinessException;
import com.example.Trabo.exception.NotFoundException;
import com.example.Trabo.model.entity.Categoria;
import com.example.Trabo.repository.CategoriaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoriaServiceTest {

    @Mock
    private CategoriaRepository categoriaRepository;

    @InjectMocks
    private CategoriaService categoriaService;

    @Test
    @DisplayName("Deve listar todas as categorias")
    void listarTodas_comSucesso() {
        Categoria c1 = Categoria.builder().id(1L).nome("Eletricista").icone("⚡").descricao("Elétrica").build();
        Categoria c2 = Categoria.builder().id(2L).nome("Pintor").icone("🎨").descricao("Pintura").build();

        when(categoriaRepository.findAll()).thenReturn(List.of(c1, c2));

        List<CategoriaResponse> resultado = categoriaService.listarTodas();

        assertThat(resultado).hasSize(2);
        assertThat(resultado.get(0).nome()).isEqualTo("Eletricista");
        assertThat(resultado.get(1).nome()).isEqualTo("Pintor");
    }

    @Test
    @DisplayName("Deve criar nova categoria com sucesso")
    void criar_comSucesso() {
        CategoriaRequest request = new CategoriaRequest("Encanador", "🔧", "Serviços hidráulicos");
        when(categoriaRepository.existsByNome("Encanador")).thenReturn(false);

        Categoria salva = Categoria.builder()
                .id(10L)
                .nome("Encanador")
                .icone("🔧")
                .descricao("Serviços hidráulicos")
                .build();
        when(categoriaRepository.save(any(Categoria.class))).thenReturn(salva);

        CategoriaResponse response = categoriaService.criar(request);

        assertThat(response).isNotNull();
        assertThat(response.id()).isEqualTo(10L);
        assertThat(response.nome()).isEqualTo("Encanador");
    }

    @Test
    @DisplayName("Deve lançar BusinessException ao tentar criar categoria com nome duplicado")
    void criar_nomeDuplicado() {
        CategoriaRequest request = new CategoriaRequest("Eletricista", "⚡", "Desc");
        when(categoriaRepository.existsByNome("Eletricista")).thenReturn(true);

        assertThatThrownBy(() -> categoriaService.criar(request))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Categoria já existe");

        verify(categoriaRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve deletar categoria existente")
    void deletar_comSucesso() {
        when(categoriaRepository.existsById(1L)).thenReturn(true);

        categoriaService.deletar(1L);

        verify(categoriaRepository).deleteById(1L);
    }

    @Test
    @DisplayName("Deve lançar NotFoundException ao tentar deletar categoria inexistente")
    void deletar_inexistente() {
        when(categoriaRepository.existsById(999L)).thenReturn(false);

        assertThatThrownBy(() -> categoriaService.deletar(999L))
                .isInstanceOf(NotFoundException.class);

        verify(categoriaRepository, never()).deleteById(any());
    }
}
