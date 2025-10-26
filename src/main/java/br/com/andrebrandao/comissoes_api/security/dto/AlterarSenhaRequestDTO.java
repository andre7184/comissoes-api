// src/main/java/br/com/andrebrandao/comissoes_api/security/dto/AlterarSenhaRequestDTO.java
package br.com.andrebrandao.comissoes_api.security.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * DTO para receber dados ao alterar a senha do próprio usuário logado.
 */
@Data
public class AlterarSenhaRequestDTO {

    @NotBlank(message = "A senha atual não pode ser vazia.")
    private String senhaAtual;

    @NotBlank(message = "A nova senha não pode ser vazia.")
    @Size(min = 6, max = 50, message = "A nova senha deve ter entre 6 e 50 caracteres.")
    private String novaSenha;
}
