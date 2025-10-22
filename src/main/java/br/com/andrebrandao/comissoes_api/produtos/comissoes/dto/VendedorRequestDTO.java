package br.com.andrebrandao.comissoes_api.produtos.comissoes.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class VendedorRequestDTO {

    // --- Dados para a entidade User ---
    
    @NotBlank(message = "O nome do vendedor não pode ser vazio.")
    @Size(min = 3, max = 100, message = "O nome deve ter entre 3 e 100 caracteres.")
    private String nome;

    @NotBlank(message = "O email do vendedor não pode ser vazio.")
    @Email(message = "Formato de email inválido.")
    @Size(max = 100, message = "O email não pode exceder 100 caracteres.")
    private String email;

    // --- CAMPO 'SENHA' REMOVIDO DAQUI ---

    // --- Dados para a entidade Vendedor ---

    @NotNull(message = "O percentual de comissão não pode ser nulo.")
    @DecimalMin(value = "0.0", inclusive = true, message = "O percentual não pode ser negativo.")
    private BigDecimal percentualComissao;

}