package br.com.andrebrandao.comissoes_api.conta.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * DTO (Data Transfer Object) para *receber* os dados
 * quando um usuário deseja alterar sua própria senha.
 */
@Data // Gera Getters, Setters, toString, etc.
public class AlterarSenhaRequestDTO {

    @NotBlank(message = "A senha atual não pode ser vazia.")
    private String senhaAtual;

    @NotBlank(message = "A nova senha não pode ser vazia.")
    @Size(min = 6, max = 50, message = "A nova senha deve ter entre 6 e 50 caracteres.")
    private String novaSenha;
}