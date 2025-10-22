package br.com.andrebrandao.comissoes_api.security.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO (Data Transfer Object) que representa o JSON de Requisição de login.
 * É o que o usuário vai enviar no "body" do POST /api/auth/login.
 */
@Data // Anotação do Lombok (gera Getters, Setters, toString, etc)
@Builder
@NoArgsConstructor // Construtor padrão (necessário para o Jackson/JSON)
@AllArgsConstructor // Construtor com todos os argumentos
public class LoginRequest {

    private String email;
    private String senha;

}