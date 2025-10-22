package br.com.andrebrandao.comissoes_api.produtos.comissoes.dto;

import java.math.BigDecimal;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * DTO para *receber* dados ao ATUALIZAR um Vendedor existente.
 * Por enquanto, permite atualizar apenas o percentual de comissão.
 */
@Data
public class VendedorUpdateRequestDTO {

    @NotNull(message = "O percentual de comissão não pode ser nulo.")
    @DecimalMin(value = "0.0", inclusive = true, message = "O percentual não pode ser negativo.")
    private BigDecimal percentualComissao;

}