package br.com.andrebrandao.comissoes_api.security.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.andrebrandao.comissoes_api.security.dto.LoginRequest;
import br.com.andrebrandao.comissoes_api.security.dto.LoginResponse;
import br.com.andrebrandao.comissoes_api.security.service.AutenticacaoService;
import lombok.RequiredArgsConstructor;

/**
 * Controller público para lidar com a autenticação (login).
 */
@RestController // 1. Marca como um Controller REST (responde JSON)
@RequestMapping("/api/auth") // 2. Define o prefixo da URL para todos os métodos
                             // aqui
@RequiredArgsConstructor
public class AutenticacaoController {

    private final AutenticacaoService autenticacaoService; // 3. Injeta nosso
                                                           // serviço de lógica

    /**
     * Endpoint de Login.
     * Mapeado para POST /api/auth/login
     *
     * @param request O JSON com email e senha (LoginRequest)
     * @return um JSON com o token e permissões (LoginResponse)
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        // 4. Apenas delega o trabalho para o serviço e retorna a resposta
        return ResponseEntity.ok(autenticacaoService.login(request));
    }

    // TODO: Criar um endpoint POST /api/auth/register
    // (para um usuário se registrar no futuro)
}