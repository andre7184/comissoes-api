// src/main/java/br/com/andrebrandao/comissoes_api/produtos/comissoes/repository/projection/VendedorComVendasProjection.java

package br.com.andrebrandao.comissoes_api.produtos.comissoes.repository.projection; 

import br.com.andrebrandao.comissoes_api.produtos.comissoes.model.Vendedor;

/**
 * Projeção DTO usada no VendedorRepository...
 */
public class VendedorComVendasProjection {
    
    private Vendedor vendedor;
    private Long qtdVendas;

    // Construtor usado na consulta JPQL (SELECT NEW ...)
    public VendedorComVendasProjection(Vendedor vendedor, Long qtdVendas) {
        this.vendedor = vendedor;
        this.qtdVendas = qtdVendas != null ? qtdVendas : 0L;
    }

    // Getters
    public Vendedor getVendedor() {
        return vendedor;
    }

    public Long getQtdVendas() {
        return qtdVendas;
    }
}