// src/main/java/br/com/andrebrandao/comissoes_api/produtos/comissoes/controller/DashboardController.java
package br.com.andrebrandao.comissoes_api.produtos.comissoes.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.andrebrandao.comissoes_api.produtos.comissoes.dto.DashboardResponseDTO;
import br.com.andrebrandao.comissoes_api.produtos.comissoes.service.VendaService; // Injetamos o serviço de Venda
import lombok.RequiredArgsConstructor;

/**
 * Controller REST dedicado à visualização do Dashboard Gerencial.
 * Protegido para ROLE_ADMIN e requer o módulo COMISSOES_CORE ativo.
 */
@RestController
@RequestMapping("/api/dashboard") // URL base: /api/dashboard
@RequiredArgsConstructor
// A mesma proteção de segurança do VendaController
@PreAuthorize("hasAuthority('ROLE_ADMIN') and @customSecurityService.hasModulo(authentication, 'COMISSAO_CORE')")
public class DashboardController {

    private final VendaService vendaService; 

    /**
     * Endpoint para gerar e retornar o Dashboard de Métricas e Ranking da Empresa.
     * Mapeado para: GET /api/dashboard/empresa
     *
     * @return O DTO DashboardResponseDTO preenchido.
     */
    @GetMapping("/empresa")
    public DashboardResponseDTO gerarDashboardEmpresa() {
        // Chama o método de serviço que criamos anteriormente
        return vendaService.gerarDashboard();
    }
}