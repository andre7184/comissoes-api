// src/main/java/br/com/andrebrandao/comissoes_api/produtos/comissoes/repository/projection/HistoricoRendimentoProjection.java

package br.com.andrebrandao.comissoes_api.produtos.comissoes.repository.projection;

import java.math.BigDecimal;

/**
 * Interface de Projeção para mapear resultados de consultas SQL Nativas.
 */
public interface HistoricoRendimentoProjection {

    String getMesAno();
    BigDecimal getValorVendido();
    BigDecimal getValorComissao();
}
