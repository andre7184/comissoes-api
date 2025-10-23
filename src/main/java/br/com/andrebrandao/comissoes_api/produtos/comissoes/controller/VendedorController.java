package br.com.andrebrandao.comissoes_api.produtos.comissoes.controller;

import java.util.List; // Importar List

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable; // Importar PathVariable
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping; // Importar PutMapping
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import br.com.andrebrandao.comissoes_api.produtos.comissoes.dto.VendedorCriadoResponseDTO; // DTO de Resposta da Criação
import br.com.andrebrandao.comissoes_api.produtos.comissoes.dto.VendedorRequestDTO; // DTO de Requisição da Criação
import br.com.andrebrandao.comissoes_api.produtos.comissoes.dto.VendedorUpdateRequestDTO; // DTO de Requisição da Atualização
import br.com.andrebrandao.comissoes_api.produtos.comissoes.model.Vendedor; // Importar Vendedor
import br.com.andrebrandao.comissoes_api.produtos.comissoes.service.VendedorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * Controller REST para o ADMIN da empresa-cliente gerenciar seus Vendedores.
 * Protegido para ROLE_ADMIN e requer o módulo COMISSOES_CORE ativo.
 */
@RestController
@RequestMapping("/api/vendedores") // URL base para vendedores
@PreAuthorize("hasAuthority('ROLE_ADMIN') and principal.empresa.modulosAtivos.contains('COMISSOES_CORE')") // Segurança!
@RequiredArgsConstructor
public class VendedorController {

    private final VendedorService vendedorService; // Injeta o serviço

    /**
     * Endpoint para CRIAR um novo vendedor (e seu usuário associado).
     * Mapeado para: POST /api/vendedores
     *
     * @param dto O JSON com nome, email e percentual (VendedorRequestDTO).
     * @return O DTO com os dados do vendedor criado E a senha temporária.
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public VendedorCriadoResponseDTO criarNovoVendedor(@Valid @RequestBody VendedorRequestDTO dto) {
        return vendedorService.criar(dto);
    }

    /**
     * Endpoint para LISTAR TODOS os vendedores da empresa do ADMIN logado.
     * Mapeado para: GET /api/vendedores
     * A segurança (@PreAuthorize) já está definida no nível da classe.
     *
     * @return Uma lista de todos os vendedores da empresa.
     */
    @GetMapping
    public List<Vendedor> listarVendedores() {
        return vendedorService.listar();
    }

    /**
     * Endpoint para BUSCAR UM vendedor específico pelo ID,
     * garantindo que ele pertença à empresa do ADMIN logado.
     * Mapeado para: GET /api/vendedores/{id}
     * A segurança (@PreAuthorize) já está definida no nível da classe.
     *
     * @param id O ID do vendedor vindo da URL.
     * @return O vendedor encontrado.
     */
    @GetMapping("/{id}")
    public Vendedor buscarVendedorPorId(@PathVariable Long id) {
        return vendedorService.buscarPorId(id);
    }

    /**
     * Endpoint para ATUALIZAR o percentual de comissão de um vendedor existente.
     * Mapeado para: PUT /api/vendedores/{id}
     * A segurança (@PreAuthorize) já está definida no nível da classe.
     *
     * @param id  O ID do vendedor vindo da URL.
     * @param dto O JSON com o novo percentual (VendedorUpdateRequestDTO).
     * @return O vendedor atualizado.
     */
    @PutMapping("/{id}") // --- MÉTODO ADICIONADO ---
    public Vendedor atualizarVendedor(@PathVariable Long id, @Valid @RequestBody VendedorUpdateRequestDTO dto) {
        return vendedorService.atualizar(id, dto);
    }

} // --- FIM DA CLASSE (A chave extra foi removida) ---