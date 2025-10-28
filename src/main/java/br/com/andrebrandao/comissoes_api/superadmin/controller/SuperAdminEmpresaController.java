// src/main/java/br/com/andrebrandao/comissoes_api/superadmin/controller/SuperAdminEmpresaController.java
package br.com.andrebrandao.comissoes_api.superadmin.controller;

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

import br.com.andrebrandao.comissoes_api.core.dto.AtualizarModulosEmpresaRequestDTO;
import br.com.andrebrandao.comissoes_api.core.dto.EmpresaRequestDTO;
import br.com.andrebrandao.comissoes_api.core.dto.EmpresaUpdateRequestDTO;
import br.com.andrebrandao.comissoes_api.core.model.Empresa;
import br.com.andrebrandao.comissoes_api.core.service.EmpresaService;
import br.com.andrebrandao.comissoes_api.core.dto.AdminUsuarioRequestDTO;
import br.com.andrebrandao.comissoes_api.security.dto.UsuarioResponseDTO; // Importar DTO
import br.com.andrebrandao.comissoes_api.security.model.User;
import br.com.andrebrandao.comissoes_api.core.dto.EmpresaComAdminsDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * Controller REST para o Super Administrador gerenciar as Empresas (Clientes/Tenants).
 */
@RestController
@RequestMapping("/api/superadmin/empresas")
@PreAuthorize("hasAuthority('ROLE_SUPER_ADMIN')")
@RequiredArgsConstructor
public class SuperAdminEmpresaController {

    private final EmpresaService empresaService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Empresa criarNovaEmpresa(@Valid @RequestBody EmpresaRequestDTO dto) {
        return empresaService.criar(dto);
    }

    /**
     * Lista todas as empresas cadastradas, incluindo módulos ativos e usuários admin.
     * Mapeado para: GET /api/superadmin/empresas
     *
     * @return Lista de DTOs EmpresaComAdminsDTO.
     */
    @GetMapping // <-- Este método
    public List<EmpresaComAdminsDTO> listarTodasEmpresas() { // <-- TIPO DE RETORNO ALTERADO AQUI
        return empresaService.listarTodas(); // Agora os tipos coincidem
    }

    /**
     * Busca UMA empresa pelo ID. Este ainda retorna a entidade completa
     * (pode ser alterado para um DTO no futuro, se necessário).
     */
    @GetMapping("/{id}")
    public Empresa buscarEmpresaPorId(@PathVariable Long id) {
        return empresaService.buscarPorId(id);
    }

    @PutMapping("/{id}")
    public Empresa atualizarEmpresa(@PathVariable Long id, @Valid @RequestBody EmpresaUpdateRequestDTO dto) {
        return empresaService.atualizar(id, dto);
    }

    @PutMapping("/{id}/modulos")
    public Empresa atualizarModulosDaEmpresa(@PathVariable Long id, @Valid @RequestBody AtualizarModulosEmpresaRequestDTO dto) {
        return empresaService.atualizarModulosAtivos(id, dto.getModuloIds());
    }

    /**
     * Endpoint para o SUPER ADMIN criar um novo usuário ROLE_ADMIN para uma empresa específica.
     * Mapeado para: POST /api/superadmin/empresas/{empresaId}/admins
     *
     * @param empresaId O ID da empresa (vindo da URL).
     * @param dto Os dados do novo admin (JSON do body).
     * @return O DTO UsuarioResponseDTO do admin criado e o status 201 Created.
     */
    @PostMapping("/{empresaId}/admins") // Este endpoint continua aqui
    @ResponseStatus(HttpStatus.CREATED)
    public UsuarioResponseDTO criarAdminParaEmpresa(@PathVariable Long empresaId, @Valid @RequestBody AdminUsuarioRequestDTO dto) { // Retorna DTO
        User novoAdmin = empresaService.criarAdminParaEmpresa(empresaId, dto); // Chama o serviço correto
        return UsuarioResponseDTO.fromEntity(novoAdmin); // Mapeia para DTO
    }

    // O método criarAdminParaMinhaEmpresa FOI REMOVIDO daqui
}