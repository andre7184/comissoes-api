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
import br.com.andrebrandao.comissoes_api.core.model.Empresa;
import br.com.andrebrandao.comissoes_api.core.service.EmpresaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * Controller REST para o Super Administrador gerenciar as Empresas (Clientes/Tenants).
 * Todos os endpoints aqui são protegidos e exigem ROLE_SUPER_ADMIN.
 */
@RestController
@RequestMapping("/api/superadmin/empresas") // 1. Prefixo da URL para Empresas
@PreAuthorize("hasAuthority('ROLE_SUPER_ADMIN')") // 2. Trava de segurança para a
                                            // classe inteira
@RequiredArgsConstructor
public class SuperAdminEmpresaController {

    private final EmpresaService empresaService; // 3. Injeta o serviço de lógica

    /**
     * Endpoint para CRIAR uma nova empresa-cliente.
     * Mapeado para: POST /api/superadmin/empresas
     *
     * @param dto O JSON vindo do frontend, validado.
     * @return A empresa criada (com ID) e o status 201 Created.
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Empresa criarNovaEmpresa(@Valid @RequestBody EmpresaRequestDTO dto) {
        // 4. @Valid: Dispara a validação do @Pattern do CNPJ que definimos no DTO
        return empresaService.criar(dto);
    }

    /**
     * Endpoint para LISTAR TODAS as empresas-clientes.
     * Mapeado para: GET /api/superadmin/empresas
     *
     * @return Uma lista de todas as empresas.
     */
    @GetMapping
    public List<Empresa> listarTodasEmpresas() {
        return empresaService.listarTodas();
    }

    /**
     * Endpoint para BUSCAR UMA empresa pelo seu ID.
     * Mapeado para: GET /api/superadmin/empresas/{id}
     *
     * @param id O ID vindo da URL (ex: /api/superadmin/empresas/3)
     * @return A empresa encontrada.
     */
    @GetMapping("/{id}")
    public Empresa buscarEmpresaPorId(@PathVariable Long id) {
        return empresaService.buscarPorId(id);
    }

    /**
     * Endpoint para ATUALIZAR uma empresa existente.
     * Mapeado para: PUT /api/superadmin/empresas/{id}
     *
     * @param id  O ID da empresa a ser atualizada.
     * @param dto Os novos dados (JSON) vindos do "body".
     * @return A empresa já atualizada.
     */
    @PutMapping("/{id}")
    public Empresa atualizarEmpresa(@PathVariable Long id, @Valid @RequestBody EmpresaRequestDTO dto) {
        return empresaService.atualizar(id, dto);
    }

    /**
     * Endpoint para ATUALIZAR A LISTA DE MÓDULOS ATIVOS de uma empresa.
     * É aqui que a "venda" de módulos acontece.
     * Mapeado para: PUT /api/superadmin/empresas/{id}/modulos
     *
     * @param id  O ID da empresa a ser atualizada.
     * @param dto O JSON vindo do "body" (ex: { "moduloIds": [1, 3] })
     * @return A entidade Empresa atualizada (com a nova lista de módulos).
     */
    @PutMapping("/{id}/modulos") // 1. Define a sub-rota
    public Empresa atualizarModulosDaEmpresa(
            @PathVariable Long id, // 2. Pega o ID da empresa da URL
            @Valid @RequestBody AtualizarModulosEmpresaRequestDTO dto // 3. Pega o JSON do body
    ) {
        // 4. Chama o serviço de lógica que criamos, passando o ID da empresa
        //    e o Set<Long> de IDs de módulos
        return empresaService.atualizarModulosAtivos(id, dto.getModuloIds());
    }
}