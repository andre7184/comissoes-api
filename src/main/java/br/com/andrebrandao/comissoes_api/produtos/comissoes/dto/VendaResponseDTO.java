// VendaResponseDTO.java (Novo arquivo)
package br.com.andrebrandao.comissoes_api.produtos.comissoes.dto;

import br.com.andrebrandao.comissoes_api.produtos.comissoes.model.Venda;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class VendaResponseDTO {

    private Long id;
    private BigDecimal valorVenda;
    private String descricaoVenda;
    private BigDecimal valorComissaoCalculado;
    private LocalDateTime dataVenda;
    private VendedorSimplesDTO vendedor;

    // Método estático para conversão
    public static VendaResponseDTO fromEntity(Venda venda) {
        return VendaResponseDTO.builder()
            .id(venda.getId())
            .valorVenda(venda.getValorVenda())
            .descricaoVenda(venda.getDescricaoVenda())
            .valorComissaoCalculado(venda.getValorComissaoCalculado())
            .dataVenda(venda.getDataVenda())
            .vendedor(VendedorSimplesDTO.fromEntity(venda.getVendedor()))
            .build();
    }
}