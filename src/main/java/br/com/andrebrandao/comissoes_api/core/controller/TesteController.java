package br.com.andrebrandao.comissoes_api.core.controller; // Ajuste para o seu pacote de controllers

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller de Diagnóstico Temporário.
 * Este endpoint simples e desprotegido verifica se a aplicação está de pé e respondendo.
 * Após o diagnóstico, você pode remover ou comentar esta classe.
 */
@RestController
@RequestMapping("/teste")
public class TesteController {

    @GetMapping("/saude")
    public String saude() {
        // Esta mensagem confirma que o Spring Boot recebeu e processou a requisição com sucesso.
        return "OK! App is running.";
    }
}