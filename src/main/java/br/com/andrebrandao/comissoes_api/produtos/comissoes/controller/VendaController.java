package br.com.andrebrandao.comissoes_api.produtos.comissoes.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import br.com.andrebrandao.comissoes_api.produtos.comissoes.dto.VendaRequestDTO;
import br.com.andrebrandao.comissoes_api.produtos.comissoes.model.Venda;
import br.com.andrebrandao.comissoes_api.produtos.comissoes.service.VendaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * Controller REST para o ADMIN da empresa-cliente gerenciar as Vendas.
 * Protegido para ROLE_ADMIN e requer o módulo COMISSOES_CORE ativo.
 */
@RestController
@RequestMapping("/api/vendas") // 1. URL base para vendas
@PreAuthorize("hasAuthority('ROLE_ADMIN') and principal.empresa.modulosAtivos.contains('COMISSOES_CORE')") // 2. Segurança!
@RequiredArgsConstructor
public class VendaController {

    private final VendaService vendaService; // 3. Injeta o serviço de lógica

    /**
     * Endpoint para LANÇAR uma nova venda para um vendedor.
     * Mapeado para: POST /api/vendas
     *
     * @param dto O JSON com vendedorId e valorVenda (VendaRequestDTO).
     * @return A entidade Venda criada (com comissão calculada e data).
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Venda lancarNovaVenda(@Valid @RequestBody VendaRequestDTO dto) {
        // 4. @Valid: Dispara a validação @Positive do valorVenda
        return vendaService.lancar(dto);
    }

    /**
     * Endpoint para LISTAR TODAS as vendas da empresa do ADMIN logado.
     * Mapeado para: GET /api/vendas
     *
     * @return Uma lista de todas as vendas da empresa.
     */
    @GetMapping
    public List<Venda> listarVendas() {
        return vendaService.listar();
    }

}