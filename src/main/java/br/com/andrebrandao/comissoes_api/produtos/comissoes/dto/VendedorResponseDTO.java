package br.com.andrebrandao.comissoes_api.produtos.comissoes.dto;

import java.math.BigDecimal;
// import java.util.List;
// import java.util.stream.Collectors;

import br.com.andrebrandao.comissoes_api.produtos.comissoes.model.Vendedor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO (Data Transfer Object) para *retornar* os dados de um Vendedor 
 * nos endpoints de listagem e busca. Inclui campos do Vendedor e do User associado.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VendedorResponseDTO {

    // --- Dados do Vendedor ---
    private Long idVendedor;
    private BigDecimal percentualComissao;
    private Long qtdVendas; 
    private BigDecimal valorTotalVendas; 
    
    // --- Dados do Usuário (User) associado ---
    private Long idUsuario;
    private String nome;
    private String email;
    
    /**
     * Método de fábrica (helper) para converter uma entidade Vendedor neste DTO.
     * @param vendedor A entidade Vendedor salva.
     * @param qtdVendas A quantidade de vendas calculada.
     * @return O DTO preenchido.
     */
    public static VendedorResponseDTO fromEntity(Vendedor vendedor, Long qtdVendas, BigDecimal valorTotalVendas) { // <-- Assinatura AJUSTADA
        if (vendedor == null) {
            return null;
        }
        
        // Acessa o relacionamento 'usuario' que será carregado Lazy dentro do Service
        return VendedorResponseDTO.builder()
                .idVendedor(vendedor.getId())
                .percentualComissao(vendedor.getPercentualComissao())
                .qtdVendas(qtdVendas) 
                .valorTotalVendas(valorTotalVendas) 
                .idUsuario(vendedor.getUsuario() != null ? vendedor.getUsuario().getId() : null)
                .nome(vendedor.getUsuario() != null ? vendedor.getUsuario().getNome() : null)
                .email(vendedor.getUsuario() != null ? vendedor.getUsuario().getEmail() : null)
                
                .build();
    }
    
    /**
     * NOTA: Este método foi removido da lógica do DTO e será feito no Service, 
     * pois ele não sabe como calcular o qtdVendas.
     */
}