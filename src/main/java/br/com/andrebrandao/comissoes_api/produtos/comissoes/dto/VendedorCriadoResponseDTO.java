package br.com.andrebrandao.comissoes_api.produtos.comissoes.dto;

import java.math.BigDecimal;

import br.com.andrebrandao.comissoes_api.produtos.comissoes.model.Vendedor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO (Data Transfer Object) para *retornar* os dados após a criação
 * bem-sucedida de um Vendedor, incluindo a senha temporária gerada.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VendedorCriadoResponseDTO {

    // --- Dados do Vendedor Criado ---
    private Long idVendedor;
    private Long idUsuario;
    private String nome;
    private String email;
    private BigDecimal percentualComissao;
    private Long idEmpresa;

    // --- Senha Temporária ---
    private String senhaTemporaria; // A senha gerada que o Admin deve passar ao Vendedor

    /**
     * Método de fábrica (helper) para converter uma entidade Vendedor
     * e a senha gerada neste DTO.
     * @param vendedor A entidade Vendedor salva.
     * @param senhaGerada A senha em texto plano que foi gerada.
     * @return O DTO preenchido.
     */
    public static VendedorCriadoResponseDTO fromEntity(Vendedor vendedor, String senhaGerada) {
        if (vendedor == null) {
            return null;
        }
        return VendedorCriadoResponseDTO.builder()
                .idVendedor(vendedor.getId())
                .idUsuario(vendedor.getUsuario() != null ? vendedor.getUsuario().getId() : null)
                .nome(vendedor.getUsuario() != null ? vendedor.getUsuario().getNome() : null) // Pega o nome do User
                .email(vendedor.getUsuario() != null ? vendedor.getUsuario().getEmail() : null) // Pega o email do User
                .percentualComissao(vendedor.getPercentualComissao())
                .idEmpresa(vendedor.getEmpresa() != null ? vendedor.getEmpresa().getId() : null)
                .senhaTemporaria(senhaGerada) // Inclui a senha!
                .build();
    }
}