package br.com.andrebrandao.comissoes_api.empresa.controller; // Mantém o pacote

import java.util.Set;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.andrebrandao.comissoes_api.core.model.Modulo;
import br.com.andrebrandao.comissoes_api.security.model.User;
import br.com.andrebrandao.comissoes_api.security.service.TenantService;
import lombok.RequiredArgsConstructor;

// --- NOVOS IMPORTS ---
import br.com.andrebrandao.comissoes_api.core.dto.EmpresaDetalhesDTO;
import br.com.andrebrandao.comissoes_api.core.service.EmpresaService;

/**
 * Controller para endpoints usados pelo ADMIN da empresa-cliente (Tenant).
 */
@RestController
@RequestMapping("/api/empresa")
@RequiredArgsConstructor
public class EmpresaAdminController {

    private final TenantService tenantService;
    private final EmpresaService empresaService; // <-- INJETAR O EmpresaService

    /**
     * Endpoint para o admin listar os módulos ativos para sua empresa.
     * Mapeado para: GET /api/empresa/meus-modulos
     */
    @GetMapping("/meus-modulos")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public Set<Modulo> listarMeusModulosAtivos() {
        User adminLogado = tenantService.getUsuarioLogado();
        return adminLogado.getEmpresa().getModulosAtivos();
    }

    /**
     * Endpoint para buscar os detalhes da empresa do usuário ADMIN logado.
     * Mapeado para: GET /api/empresa/me
     *
     * @return O DTO EmpresaDetalhesDTO preenchido.
     */
    @GetMapping("/me") // <-- NOVO ENDPOINT
    @PreAuthorize("hasAuthority('ROLE_ADMIN')") // <-- Protegido por ROLE_ADMIN
    public EmpresaDetalhesDTO buscarMinhaEmpresa() {
        // Chama o método que criamos no EmpresaService
        return empresaService.buscarDetalhesEmpresaLogada();
    }
}