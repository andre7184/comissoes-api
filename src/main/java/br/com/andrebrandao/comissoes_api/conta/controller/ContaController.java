package br.com.andrebrandao.comissoes_api.conta.controller;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import br.com.andrebrandao.comissoes_api.conta.dto.AlterarSenhaRequestDTO;
import br.com.andrebrandao.comissoes_api.conta.service.ContaService;
import jakarta.validation.Valid; // Importar @Valid
import lombok.RequiredArgsConstructor;

/**
 * Controller REST para operações genéricas da conta do usuário logado.
 * Protegido para qualquer usuário autenticado (logado).
 */
@RestController
@RequestMapping("/api/conta") // 1. URL base para gerenciamento de conta
@PreAuthorize("isAuthenticated()") // 2. A MÁGICA: Só precisa estar logado!
@RequiredArgsConstructor
public class ContaController {

    private final ContaService contaService; // 3. Injeta o serviço de lógica

    /**
     * Endpoint para QUALQUER usuário logado alterar sua própria senha.
     * Mapeado para: PUT /api/conta/alterar-minha-senha
     *
     * @param dto O JSON com a senha atual e a nova senha.
     */
    @PutMapping("/alterar-minha-senha")
    @ResponseStatus(HttpStatus.NO_CONTENT) // 4. Resposta 204 (Sucesso, sem conteúdo)
    public void alterarMinhaSenha(@Valid @RequestBody AlterarSenhaRequestDTO dto) {
        // 5. @Valid: Dispara as validações (@NotBlank, @Size) do DTO
        // 6. Delega o trabalho para o serviço
        contaService.alterarMinhaSenha(dto);
    }
}