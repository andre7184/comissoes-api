// src/main/java/br/com/andrebrandao/comissoes_api/produtos/comissoes/dto/VendaUpdateRequestDTO.java
package br.com.andrebrandao.comissoes_api.produtos.comissoes.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * DTO para *receber* dados ao ATUALIZAR uma Venda existente.
 * Permite alterar o valor da venda e a descrição.
 */
@Data
public class VendaUpdateRequestDTO {

    @NotNull(message = "O valor da venda não pode ser nulo.")
    @Positive(message = "O valor da venda deve ser positivo (maior que zero).")
    private BigDecimal valorVenda;

    // Descrição é opcional, mas se enviada, pode ter um limite de tamanho
    @Size(max = 500, message = "A descrição não pode exceder 500 caracteres.")
    private String descricaoVenda; // Pode ser null se não for alterada
}