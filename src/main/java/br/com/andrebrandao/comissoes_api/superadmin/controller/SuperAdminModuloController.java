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

import br.com.andrebrandao.comissoes_api.core.dto.ModuloRequestDTO;
import br.com.andrebrandao.comissoes_api.core.model.Modulo;
import br.com.andrebrandao.comissoes_api.core.service.ModuloService;
import jakarta.validation.Valid; // 1. Importa a anotação de Validação
import lombok.RequiredArgsConstructor;

/**
 * Controller REST para o Super Administrador gerenciar o Catálogo de Módulos.
 * Todos os endpoints aqui são protegidos e exigem ROLE_SUPER_ADMIN.
 */
@RestController // 2. Define que esta classe é um Controller REST (responde JSON)
@RequestMapping("/api/superadmin/modulos") // 3. Define o prefixo da URL para
                                          // todos os endpoints desta classe
@PreAuthorize("hasAuthority('ROLE_SUPER_ADMIN')") // 4. A "MÁGICA" DA SEGURANÇA!
                                            // Esta anotação tranca TODOS os
                                            // métodos desta classe.
                                            // Só permite acesso se o token JWT
                                            // do usuário tiver o "role" =
                                            // "ROLE_SUPER_ADMIN".
@RequiredArgsConstructor
public class SuperAdminModuloController {

    private final ModuloService moduloService; // 5. Injeta o serviço que tem a
                                               // lógica

    /**
     * Endpoint para CRIAR um novo módulo.
     * Mapeado para: POST /api/superadmin/modulos
     *
     * @param dto O JSON vindo do frontend, validado.
     * @return O módulo criado (com ID) e o status 201 Created.
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED) // 6. Retorna o status 201 (Created) em
                                        // vez do 200 (OK)
    public Modulo criarNovoModulo(@Valid @RequestBody ModuloRequestDTO dto) {
        // 7. @Valid: Dispara as validações (@NotBlank, etc) que definimos no DTO.
        //    @RequestBody: Converte o JSON do "body" para o objeto DTO.
        return moduloService.criar(dto);
    }

    /**
     * Endpoint para LISTAR TODOS os módulos.
     * Mapeado para: GET /api/superadmin/modulos
     *
     * @return Uma lista de todos os módulos.
     */
    @GetMapping
    public List<Modulo> listarTodosModulos() {
        return moduloService.listarTodos();
    }

    /**
     * Endpoint para BUSCAR UM módulo pelo seu ID.
     * Mapeado para: GET /api/superadmin/modulos/{id}
     *
     * @param id O ID vindo da URL (ex: /api/superadmin/modulos/5)
     * @return O módulo encontrado.
     */
    @GetMapping("/{id}")
    public Modulo buscarModuloPorId(@PathVariable Long id) {
        // 8. @PathVariable: Pega o "id" da URL e o injeta na variável 'id'.
        return moduloService.buscarPorId(id);
    }

    /**
     * Endpoint para ATUALIZAR um módulo existente.
     * Mapeado para: PUT /api/superadmin/modulos/{id}
     *
     * @param id  O ID do módulo a ser atualizado.
     * @param dto Os novos dados (JSON) vindos do "body".
     * @return O módulo já atualizado.
     */
    @PutMapping("/{id}")
    public Modulo atualizarModulo(@PathVariable Long id, @Valid @RequestBody ModuloRequestDTO dto) {
        return moduloService.atualizar(id, dto);
    }

    /**
     * Endpoint para LISTAR apenas os módulos disponíveis para venda
     * (status = PRONTO_PARA_PRODUCAO).
     * Mapeado para: GET /api/superadmin/modulos/disponiveis
     *
     * @return Uma lista de módulos prontos para venda.
     */
    @GetMapping("/disponiveis") // 1. Define a sub-rota
    public List<Modulo> listarModulosDisponiveisParaVenda() {
        // 2. Chama o novo método do serviço que criamos
        return moduloService.listarDisponiveisParaVenda();
    }
}