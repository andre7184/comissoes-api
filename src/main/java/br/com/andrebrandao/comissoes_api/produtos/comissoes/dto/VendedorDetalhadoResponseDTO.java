// src/main/java/br/com/andrebrandao/comissoes_api/produtos/comissoes/dto/VendedorDetalhadoResponseDTO.java
package br.com.andrebrandao.comissoes_api.produtos.comissoes.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import br.com.andrebrandao.comissoes_api.produtos.comissoes.model.Vendedor;
import lombok.Builder;
import lombok.Data;

/**
 * DTO para retornar dados detalhados e métricas de um Vendedor,
 * incluindo o histórico de rendimentos mensais (para gráficos).
 */
@Data
@Builder
public class VendedorDetalhadoResponseDTO {

    // --- Campos da interface Vendedor ---
    private Long id; // Mapeia idVendedor
    private String nome;
    private String email;
    private BigDecimal percentualComissao;
    private Long idEmpresa;

    // --- Campos Detalhados (VendedorDetalhado) ---
    private LocalDateTime dataCadastro; // Mapeia dataCadastro (Usaremos a data de criação do User)
    private Long qtdVendas;
    private BigDecimal valorTotalVendas;
    private BigDecimal mediaComissao; // (valorTotalComissao / qtdVendas)

    // --- Estrutura para dados de gráfico ---
    private List<HistoricoRendimentoDTO> historicoRendimentos;


    public static VendedorDetalhadoResponseDTO fromEntity(
            Vendedor vendedor, 
            Long qtdVendas,
            BigDecimal valorTotalVendas,
            List<HistoricoRendimentoDTO> historicoRendimentos) {

        if (vendedor == null) return null;
        
        // 1. Calcular Valor Total de Comissão e Média
        BigDecimal valorTotalComissao = historicoRendimentos.stream()
                .map(HistoricoRendimentoDTO::getValorComissao)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal mediaComissao = BigDecimal.ZERO;
        if (qtdVendas != null && qtdVendas > 0) {
            // Reutiliza a soma para obter a média por venda
            mediaComissao = valorTotalComissao
                    .divide(new BigDecimal(qtdVendas), 2, java.math.RoundingMode.HALF_UP);
        }

        // 2. Cria o DTO
        return VendedorDetalhadoResponseDTO.builder()
                .id(vendedor.getId())
                .percentualComissao(vendedor.getPercentualComissao())
                .idEmpresa(vendedor.getEmpresa() != null ? vendedor.getEmpresa().getId() : null)
                .qtdVendas(qtdVendas != null ? qtdVendas : 0L)
                .valorTotalVendas(valorTotalVendas != null ? valorTotalVendas : BigDecimal.ZERO)
                .mediaComissao(mediaComissao)
                .historicoRendimentos(historicoRendimentos)
                // Dados do User (Nome e Email)
                .nome(vendedor.getUsuario() != null ? vendedor.getUsuario().getNome() : null)
                .email(vendedor.getUsuario() != null ? vendedor.getUsuario().getEmail() : null)
                // Data de Cadastro (AGORA USA O NOVO GETTER dataCriacao)
                .dataCadastro(vendedor.getUsuario() != null ? vendedor.getUsuario().getDataCriacao() : null) 
                .build();
    }
}