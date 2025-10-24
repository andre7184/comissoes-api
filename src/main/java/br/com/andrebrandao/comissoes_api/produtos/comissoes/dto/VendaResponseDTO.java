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
    private BigDecimal valorComissaoCalculado;
    private LocalDateTime dataVenda;

    // Campos solicitados
    private Long idVendedor; 
    private String nomeVendedor; 

    // Método estático para conversão
    public static VendaResponseDTO fromEntity(Venda venda) {
        return VendaResponseDTO.builder()
                .id(venda.getId())
                .valorVenda(venda.getValorVenda())
                .valorComissaoCalculado(venda.getValorComissaoCalculado())
                .dataVenda(venda.getDataVenda())
                // Acessa o Vendedor, que deve ser carregado via JOIN FETCH no Service
                .idVendedor(venda.getVendedor() != null ? venda.getVendedor().getId() : null)
                .nomeVendedor(venda.getVendedor() != null && venda.getVendedor().getUsuario() != null ?
                        venda.getVendedor().getUsuario().getNome() : "Vendedor Desconhecido")
                .build();
    }
}