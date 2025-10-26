// src/main/java/br/com/andrebrandao/comissoes_api/core/dto/AdminUsuarioRequestDTO.java
// Ou pode ser em br.com.andrebrandao.comissoes_api.superadmin.dto
package br.com.andrebrandao.comissoes_api.core.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * DTO para receber dados ao criar um novo usuário ROLE_ADMIN para uma empresa existente.
 * Usado pelo SuperAdminEmpresaController.
 */
@Data
public class AdminUsuarioRequestDTO {

    @NotBlank(message = "O nome do admin não pode ser vazio.")
    @Size(min = 3, max = 100, message = "O nome deve ter entre 3 e 100 caracteres.")
    private String nome;

    @NotBlank(message = "O email do admin não pode ser vazio.")
    @Email(message = "Formato de email inválido.")
    @Size(max = 100, message = "O email não pode exceder 100 caracteres.")
    private String email;

    @NotBlank(message = "A senha do admin não pode ser vazia.")
    @Size(min = 6, max = 50, message = "A senha deve ter entre 6 e 50 caracteres.")
    private String senha;
}