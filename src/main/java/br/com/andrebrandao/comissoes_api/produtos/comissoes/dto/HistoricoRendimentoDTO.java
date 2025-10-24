// src/main/java/br/com/andrebrandao/comissoes_api/produtos/comissoes/dto/HistoricoRendimentoDTO.java
package br.com.andrebrandao.comissoes_api.produtos.comissoes.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO auxiliar para a projeção do histórico de vendas mensais.
 * Usado na consulta JPQL SELECT NEW.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HistoricoRendimentoDTO {

    private String mesAno; // Ex: "2024-06"
    private BigDecimal valorVendido;
    private BigDecimal valorComissao;
    
}