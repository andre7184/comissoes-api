package br.com.andrebrandao.comissoes_api.produtos.comissoes.dto;

import java.math.BigDecimal;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

/**
 * DTO (Data Transfer Object) para *receber* dados quando um VENDEDOR
 * lança uma nova Venda.
 */
@Data
public class VendaVendedorRequestDTO {

    /**
     * O valor total da venda realizada.
     * O Vendedor (ID) será identificado automaticamente pelo Token JWT.
     */
    @NotNull(message = "O valor da venda não pode ser nulo.")
    @Positive(message = "O valor da venda deve ser positivo (maior que zero).")
    private BigDecimal valorVenda;

    // TODO: Adicionar campos opcionais que o vendedor possa preencher
    // (ex: 'private String observacao;')
}