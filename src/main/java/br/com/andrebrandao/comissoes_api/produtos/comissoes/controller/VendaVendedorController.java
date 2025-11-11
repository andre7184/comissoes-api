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

import br.com.andrebrandao.comissoes_api.produtos.comissoes.dto.VendaVendedorRequestDTO;
import br.com.andrebrandao.comissoes_api.produtos.comissoes.model.Venda;
import br.com.andrebrandao.comissoes_api.produtos.comissoes.service.VendaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * Controller REST para o VENDEDOR (ROLE_VENDEDOR) gerenciar suas próprias Vendas.
 * Protegido para ROLE_VENDEDOR e requer o módulo COMISSOES_CORE ativo.
 */
@RestController
@RequestMapping("/api/portal-vendas") // 1. URL base para o portal do vendedor
@PreAuthorize("hasAuthority('ROLE_VENDEDOR') and principal.empresa.modulosAtivos.contains('COMISSOES_CORE')") // 2. Segurança!
@RequiredArgsConstructor
public class VendaVendedorController {

    private final VendaService vendaService; // 3. Injeta o mesmo VendaService

    /**
     * Endpoint para o VENDEDOR logado lançar uma nova venda.
     * A venda será criada com status 'PENDENTE'.
     * Mapeado para: POST /api/portal-vendas
     *
     * @param dto O JSON com o valorVenda (VendaVendedorRequestDTO).
     * @return A entidade Venda criada (com comissão calculada e status PENDENTE).
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Venda lancarMinhaVenda(@Valid @RequestBody VendaVendedorRequestDTO dto) {
        // 4. @Valid: Dispara a validação @Positive do valorVenda
        // 5. Chama o método específico para o vendedor no VendaService
        return vendaService.lancarPeloVendedor(dto);
    }

    /**
     * Endpoint para o VENDEDOR logado listar todas as suas vendas (PENDENTES,
     * CONFIRMADAS, etc.).
     * Mapeado para: GET /api/portal-vendas
     *
     * @return Uma lista de todas as vendas do vendedor logado.
     */
    @GetMapping
    public List<Venda> listarMinhasVendas() {
        // 6. Chama o método específico para o vendedor no VendaService
        return vendaService.listarMinhasVendas();
    }

}