// VendedorSimplesDTO.java (Novo arquivo)
package br.com.andrebrandao.comissoes_api.produtos.comissoes.dto;

import br.com.andrebrandao.comissoes_api.produtos.comissoes.model.Vendedor;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

/**
 * DTO SIMPLIFICADO para representar o Vendedor dentro do VendaResponseDTO.
 */
@Data
@Builder
public class VendedorSimplesDTO {
    
    private Long idVendedor;
    private String nome; 
    private String email;
    private BigDecimal percentualComissao; 

    public static VendedorSimplesDTO fromEntity(Vendedor vendedor) {
        if (vendedor == null) {
            return null;
        }

        // Acessa o relacionamento 'usuario' que deve ser carregado no Service
        return VendedorSimplesDTO.builder()
                .idVendedor(vendedor.getId())
                .percentualComissao(vendedor.getPercentualComissao())
                .nome(vendedor.getUsuario() != null ? vendedor.getUsuario().getNome() : "Nome Indisponível")
                .email(vendedor.getUsuario() != null ? vendedor.getUsuario().getEmail() : "Email Indisponível")
                .build();
    }
}