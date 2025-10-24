// src/main/java/br/com/andrebrandao/comissoes_api/core/dto/EmpresaUpdateRequestDTO.java
package br.com.andrebrandao.comissoes_api.core.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * DTO (Data Transfer Object) para a requisição de ATUALIZAÇÃO
 * de dados básicos de uma Empresa (Tenant).
 * Usado pelo endpoint PUT /api/superadmin/empresas/{id}.
 * Contém apenas os campos que podem ser modificados após a criação.
 */
@Data // Gera automaticamente getters, setters, toString, equals, hashCode
@NoArgsConstructor // Gera um construtor sem argumentos
@AllArgsConstructor // Gera um construtor com todos os argumentos
public class EmpresaUpdateRequestDTO {
    
    @NotBlank(message = "Nome Fantasia é obrigatório.")
    @Size(min = 2, max = 100, message = "Nome Fantasia deve ter entre 2 e 100 caracteres.")
    private String nomeFantasia;

    @NotBlank(message = "CNPJ é obrigatório.")
    // Valida o formato XX.XXX.XXX/XXXX-XX
    @Pattern(regexp = "^\\d{2}\\.\\d{3}\\.\\d{3}/\\d{4}-\\d{2}$", message = "Formato de CNPJ inválido (deve ser XX.XXX.XXX/XXXX-XX).")
    private String cnpj;
    
    // Note que os campos de admin (adminNome, adminEmail, adminSenha)
    // NÃO estão presentes neste DTO, pois não são atualizáveis por este endpoint.
}