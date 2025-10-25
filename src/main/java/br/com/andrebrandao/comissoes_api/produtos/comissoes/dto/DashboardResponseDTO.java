// src/main/java/br/com/andrebrandao/comissoes_api/produtos/comissoes/dto/DashboardResponseDTO.java
package br.com.andrebrandao.comissoes_api.produtos.comissoes.dto;

import java.math.BigDecimal;
import java.util.List;

import lombok.Builder;
import lombok.Data;

/**
 * DTO principal para a resposta do Dashboard de Vendas.
 * Consolida métricas gerais, rankings de vendedores e histórico.
 */
@Data
@Builder
public class DashboardResponseDTO {

    // --- Métricas Gerais do Mês (Top Line Metrics) ---
    private BigDecimal totalVendasMes;
    private BigDecimal totalComissoesMes;
    private Long qtdVendasMes;
    private BigDecimal mediaVendaMes; // Calculado no Service: totalVendasMes / qtdVendasMes
    private BigDecimal mediaComissaoMes; // Calculado no Service: totalComissoesMes / qtdVendasMes
    // --- Ranking e Vendas Detalhadas ---
    private List<VendedorRankingDTO> rankingVendedores;
    private List<VendaDetalheDTO> maioresVendas;
    private List<VendaDetalheDTO> ultimasVendas;

    // --- Histórico para Gráficos ---
    // Reutilizamos o DTO de histórico mensal criado anteriormente
    private List<HistoricoRendimentoDTO> historicoVendasMensal;
}