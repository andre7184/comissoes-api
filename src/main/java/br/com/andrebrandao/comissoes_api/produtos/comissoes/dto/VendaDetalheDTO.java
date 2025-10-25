// src/main/java/br/com/andrebrandao/comissoes_api/produtos/comissoes/dto/VendaDetalheDTO.java
package br.com.andrebrandao.comissoes_api.produtos.comissoes.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import br.com.andrebrandao.comissoes_api.produtos.comissoes.model.Venda;
import lombok.Builder;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/**
 * DTO para representar uma venda dentro de listas (Maiores/Últimas).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor // NOVO: Adiciona construtor para facilitar mapeamento
public class VendaDetalheDTO {
    
    private Long idVenda;
    private String nomeVendedor;
    private Long idVendedor;
    private BigDecimal valorVenda;
    private LocalDateTime dataVenda;
    
    // Método de fábrica para mapear a entidade Venda (com Vendedor carregado)
    public static VendaDetalheDTO fromEntity(Venda venda) {
        if (venda == null || venda.getVendedor() == null || venda.getVendedor().getUsuario() == null) {
            return null;
        }

        return VendaDetalheDTO.builder()
                .idVenda(venda.getId())
                .idVendedor(venda.getVendedor().getId())
                .nomeVendedor(venda.getVendedor().getUsuario().getNome()) // Acessa o nome do User
                .valorVenda(venda.getValorVenda())
                .dataVenda(venda.getDataVenda())
                .build();
    }
}