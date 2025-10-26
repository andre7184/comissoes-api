// src/main/java/br/com/andrebrandao/comissoes_api/empresa/controller/EmpresaAdminController.java
package br.com.andrebrandao.comissoes_api.empresa.controller;

import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping; // Importar PostMapping
import org.springframework.web.bind.annotation.RequestBody; // Importar RequestBody
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus; // Importar ResponseStatus
import org.springframework.web.bind.annotation.RestController;

import br.com.andrebrandao.comissoes_api.core.model.Modulo;
import br.com.andrebrandao.comissoes_api.security.model.User;
import br.com.andrebrandao.comissoes_api.security.service.TenantService;
import jakarta.validation.Valid; // Importar Valid
import lombok.RequiredArgsConstructor;
import br.com.andrebrandao.comissoes_api.core.dto.AdminUsuarioRequestDTO; // Importar DTO Request
import br.com.andrebrandao.comissoes_api.core.dto.EmpresaDetalhesDTO;
import br.com.andrebrandao.comissoes_api.core.service.EmpresaService;
import br.com.andrebrandao.comissoes_api.security.dto.UsuarioResponseDTO; // Importar DTO Response

/**
 * Controller para endpoints usados pelo ADMIN da empresa-cliente (Tenant).
 */
@RestController
@RequestMapping("/api/empresa")
@RequiredArgsConstructor
public class EmpresaAdminController {

    private final TenantService tenantService;
    private final EmpresaService empresaService;

    @GetMapping("/meus-modulos")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public Set<Modulo> listarMeusModulosAtivos() {
        User adminLogado = tenantService.getUsuarioLogado();
        // Adicionar tratamento se getEmpresa for null ou getModulosAtivos for null
        if (adminLogado != null && adminLogado.getEmpresa() != null) {
             return adminLogado.getEmpresa().getModulosAtivos();
        }
        return Set.of(); // Retorna conjunto vazio se não encontrar
    }

    @GetMapping("/me")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public EmpresaDetalhesDTO buscarMinhaEmpresa() {
        return empresaService.buscarDetalhesEmpresaLogada();
    }

    /**
     * Endpoint para o ADMIN logado criar um novo usuário ROLE_ADMIN na sua própria empresa.
     * Mapeado para: POST /api/empresa/admins
     *
     * @param dto Os dados do novo admin (JSON do body).
     * @return O DTO UsuarioResponseDTO do admin criado e o status 201 Created.
     */
    @PostMapping("/admins") // Este método agora está AQUI
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('ROLE_ADMIN')") // Protegido por ROLE_ADMIN
    public UsuarioResponseDTO criarAdminParaMinhaEmpresa(@Valid @RequestBody AdminUsuarioRequestDTO dto) { // Retorna DTO
        User novoAdmin = empresaService.criarAdminParaMinhaEmpresa(dto); // Chama o serviço correto
        return UsuarioResponseDTO.fromEntity(novoAdmin); // Mapeia para DTO
    }
}