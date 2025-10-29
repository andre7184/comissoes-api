package br.com.andrebrandao.comissoes_api.produtos.comissoes.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive; // 1. Validação: Valor deve ser maior que zero
import lombok.Data;

/**
 * DTO (Data Transfer Object) para *receber* dados ao lançar uma nova Venda.
 */
@Data
public class VendaRequestDTO {

    /**
     * O ID do Vendedor que realizou a venda.
     */
    @NotNull(message = "O ID do vendedor não pode ser nulo.")
    private Long vendedorId;

    /**
     * A descrição da venda.
     */
    private String descricaoVenda;

    /**
     * O valor total da venda realizada.
     */
    @NotNull(message = "O valor da venda não pode ser nulo.")
    @Positive(message = "O valor da venda deve ser positivo (maior que zero).") // 2. Garante > 0
    private BigDecimal valorVenda;

    // Nota: Não precisamos da data. A entidade Venda usará @CreationTimestamp
    // para registrar a data automaticamente quando for salva.
}