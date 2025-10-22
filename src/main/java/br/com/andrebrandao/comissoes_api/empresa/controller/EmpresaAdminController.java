package br.com.andrebrandao.comissoes_api.empresa.controller;

import java.util.Set;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.andrebrandao.comissoes_api.core.model.Modulo;
import br.com.andrebrandao.comissoes_api.security.model.User;
import br.com.andrebrandao.comissoes_api.security.service.TenantService; // 1. IMPORTANTE!
import lombok.RequiredArgsConstructor;

/**
 * Controller para endpoints usados pelo ADMIN da empresa-cliente (Tenant).
 * Todas as APIs que o seu *cliente* logado usa (e que não são do Super Admin)
 * podem começar aqui.
 */
@RestController
@RequestMapping("/api/empresa") // 2. Novo prefixo de API: /api/empresa
@RequiredArgsConstructor
public class EmpresaAdminController {

    // 3. Injeta o helper que sabe quem está logado
    private final TenantService tenantService;

    /**
     * Endpoint para o admin da empresa-cliente listar
     * todos os módulos que estão *atualmente ativos* para sua empresa.
     *
     * Mapeado para: GET /api/empresa/meus-modulos
     *
     * @return Um Set (conjunto) dos objetos Modulo ativos (com id, nome,
     * chave, etc).
     */
    @GetMapping("/meus-modulos")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')") // 4. A MÁGICA! Só permite ROLE_ADMIN
                                          // (o da empresa)
    public Set<Modulo> listarMeusModulosAtivos() {
        
        // 5. Usa o TenantService para pegar o objeto 'User' completo
        //    do usuário que fez a requisição (o dono do token)
        User adminLogado = tenantService.getUsuarioLogado();
        
        // 6. Pega a empresa associada a esse usuário...
        // 7. ...e da empresa, pega a lista de módulos ativos e a retorna.
        return adminLogado.getEmpresa().getModulosAtivos();
    }

}
