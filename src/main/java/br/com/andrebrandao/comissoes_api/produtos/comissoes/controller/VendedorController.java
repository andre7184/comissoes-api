// src/main/java/br/com.andrebrandao.comissoes_api/produtos/comissoes/controller/VendedorController.java
package br.com.andrebrandao.comissoes_api.produtos.comissoes.controller;

import java.util.List; 

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable; 
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping; 
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import br.com.andrebrandao.comissoes_api.produtos.comissoes.dto.VendedorCriadoResponseDTO;
import br.com.andrebrandao.comissoes_api.produtos.comissoes.dto.VendedorDetalhadoResponseDTO;
import br.com.andrebrandao.comissoes_api.produtos.comissoes.dto.VendedorRequestDTO; 
import br.com.andrebrandao.comissoes_api.produtos.comissoes.dto.VendedorUpdateRequestDTO; 
import br.com.andrebrandao.comissoes_api.produtos.comissoes.dto.VendedorResponseDTO; // <-- NOVO IMPORT
// import br.com.andrebrandao.comissoes_api.produtos.comissoes.model.Vendedor; // Não é mais o retorno

import br.com.andrebrandao.comissoes_api.produtos.comissoes.service.VendedorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * Controller REST para o ADMIN da empresa-cliente gerenciar seus Vendedores.
 * Protegido para ROLE_ADMIN e requer o módulo COMISSOES_CORE ativo.
 */
@RestController
@RequestMapping("/api/vendedores") // URL base para vendedores
@RequiredArgsConstructor
// --- ANOTAÇÃO @PreAuthorize ATUALIZADA ---
// Verifica o ROLE_ADMIN e chama o serviço 'customSecurityService' para verificar o módulo
// Use a chave exata do seu módulo (COMISSAO_CORE ou COMISSOES_CORE)
@PreAuthorize("hasAuthority('ROLE_ADMIN') and @customSecurityService.hasModulo(authentication, 'COMISSAO_CORE')") 
public class VendedorController {

    private final VendedorService vendedorService; // Injeta o serviço

    /**
     * Endpoint para CRIAR um novo vendedor (e seu usuário associado).
     * @return O DTO com os dados do vendedor criado E a senha temporária.
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public VendedorCriadoResponseDTO criarNovoVendedor(@Valid @RequestBody VendedorRequestDTO dto) {
        return vendedorService.criar(dto);
    }

    /**
     * Endpoint para LISTAR TODOS os vendedores da empresa do ADMIN logado.
     * @return Uma lista de DTOs VendedorResponseDTO.
     */
    @GetMapping
    public List<VendedorResponseDTO> listarVendedores() { // <-- TIPO DE RETORNO ALTERADO
        return vendedorService.listar();
    }

    /**
     * Endpoint para BUSCAR UM vendedor específico pelo ID.
     * @return O DTO VendedorResponseDTO encontrado.
     */
    @GetMapping("/{id}")
    public VendedorResponseDTO buscarVendedorPorId(@PathVariable Long id) { // <-- TIPO DE RETORNO ALTERADO
        return vendedorService.buscarPorId(id);
    }

    /**
     * Endpoint para ATUALIZAR o percentual de comissão de um vendedor existente.
     * @return O DTO VendedorResponseDTO do vendedor atualizado.
     */
    @PutMapping("/{id}") 
    public VendedorResponseDTO atualizarVendedor(@PathVariable Long id, @Valid @RequestBody VendedorUpdateRequestDTO dto) { // <-- TIPO DE RETORNO ALTERADO
        return vendedorService.atualizar(id, dto);
    }

    // ... (Método buscarVendedorPorId)

    /**
     * Endpoint para BUSCAR DETALHES COMPLETOS de um vendedor específico pelo ID.
     * Inclui métricas e histórico de rendimentos mensais.
     * Mapeado para: GET /api/vendedores/{id}/detalhes
     * @return O DTO VendedorDetalhadoResponseDTO.
     */
    @GetMapping("/{id}/detalhes")
    public VendedorDetalhadoResponseDTO buscarDetalhesVendedorPorId(@PathVariable("id") Long idDoVendedor) { 
        return vendedorService.buscarDetalhesPorId(idDoVendedor);
    }
}