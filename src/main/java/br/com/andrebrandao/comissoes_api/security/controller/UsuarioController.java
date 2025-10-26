// src/main/java/br/com/andrebrandao/comissoes_api/security/controller/UsuarioController.java
package br.com.andrebrandao.comissoes_api.security.controller;

import org.springframework.http.ResponseEntity; // Para retornar OK sem corpo
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.andrebrandao.comissoes_api.security.dto.AlterarSenhaRequestDTO;
import br.com.andrebrandao.comissoes_api.security.service.UsuarioService; // Importa o novo serviço
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * Controller REST para operações relacionadas ao usuário logado (ex: alterar senha).
 */
@RestController
@RequestMapping("/api/usuarios") // URL base: /api/usuarios
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;

    /**
     * Endpoint para o usuário logado alterar sua própria senha.
     * Mapeado para: PUT /api/usuarios/me/senha
     * Requer que o usuário esteja autenticado.
     *
     * @param dto O DTO com a senha atual e a nova senha.
     * @return Status 200 OK sem corpo em caso de sucesso.
     */
    @PutMapping("/me/senha")
    @PreAuthorize("isAuthenticated()") // Garante que SÓ usuários autenticados podem acessar
    public ResponseEntity<Void> alterarMinhaSenha(@Valid @RequestBody AlterarSenhaRequestDTO dto) {
        usuarioService.alterarSenhaUsuarioLogado(dto);
        return ResponseEntity.ok().build(); // Retorna 200 OK sem corpo
        // Alternativamente, poderia ser ResponseEntity.noContent().build(); (204 No Content)
    }
}