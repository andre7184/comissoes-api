package br.com.andrebrandao.comissoes_api.core.dto;

import jakarta.validation.constraints.Email; // Import adicionado
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size; // Import adicionado
import lombok.Data;

@Data
public class EmpresaRequestDTO {

    // --- Dados da Empresa ---
    @NotBlank(message = "O nome fantasia não pode ser vazio.")
    @Size(min = 2, max = 100, message = "O nome fantasia deve ter entre 2 e 100 caracteres.")
    private String nomeFantasia;

    @NotBlank(message = "O CNPJ não pode ser vazio.")
    @Pattern(regexp = "^\\d{2}\\.\\d{3}\\.\\d{3}/\\d{4}-\\d{2}$", message = "O formato do CNPJ deve ser XX.XXX.XXX/XXXX-XX")
    private String cnpj;
    
    @NotBlank(message = "O nome razão social não pode ser vazio.")
    private String razaoSocial;
    // --- CAMPOS ADICIONADOS (Dados do Admin da Empresa Cliente) ---
    
    @NotBlank(message = "O nome do admin não pode ser vazio.")
    @Size(min = 3, max = 100, message = "O nome do admin deve ter entre 3 e 100 caracteres.")
    private String adminNome;

    @NotBlank(message = "O email do admin não pode ser vazio.")
    @Email(message = "Formato de email inválido.")
    @Size(max = 100, message = "O email não pode exceder 100 caracteres.")
    private String adminEmail;

    @NotBlank(message = "A senha do admin não pode ser vazia.")
    @Size(min = 6, max = 50, message = "A senha deve ter entre 6 e 50 caracteres.")
    private String adminSenha;
}