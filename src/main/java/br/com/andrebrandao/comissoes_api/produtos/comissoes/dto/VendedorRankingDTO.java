// src/main/java/br/com/andrebrandao/comissoes_api/produtos/comissoes/dto/VendedorRankingDTO.java
package br.com.andrebrandao.comissoes_api.produtos.comissoes.dto;

import java.math.BigDecimal;
import lombok.Builder;
import lombok.Data;
import lombok.AllArgsConstructor; // Para construtor de projeção JPQL

@Data
@Builder
@AllArgsConstructor
public class VendedorRankingDTO {
    
    // O JPQL precisará buscar estes campos e mapear.
    private String nomeVendedor;
    private Long idVendedor;
    private BigDecimal valorTotal;
    private Long qtdVendas;
    
    // Construtor implícito de projeção é fornecido pelo @AllArgsConstructor
}