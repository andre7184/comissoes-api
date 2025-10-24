// src/main/java/br/com/andrebrandao/comissoes_api/produtos/comissoes/repository/projection/VendedorComVendasProjection.java

package br.com.andrebrandao.comissoes_api.produtos.comissoes.repository.projection; 

import java.math.BigDecimal;

import br.com.andrebrandao.comissoes_api.produtos.comissoes.model.Vendedor;

/**
 * Projeção DTO usada no VendedorRepository...
 */
public class VendedorComVendasProjection {
    
    private Vendedor vendedor;
    private Long qtdVendas;
    private BigDecimal valorTotalVendas;

    // Construtor usado na consulta JPQL (SELECT NEW ...)
    public VendedorComVendasProjection(Vendedor vendedor, Long qtdVendas, BigDecimal valorTotalVendas) {
        this.vendedor = vendedor;
        this.qtdVendas = qtdVendas != null ? qtdVendas : 0L;
        this.valorTotalVendas = valorTotalVendas != null ? valorTotalVendas : BigDecimal.ZERO;
    }

    // Getters
    public Vendedor getVendedor() {
        return vendedor;
    }

    public Long getQtdVendas() {
        return qtdVendas;
    }

    public BigDecimal getValorTotalVendas() {
        return valorTotalVendas;
    }
}