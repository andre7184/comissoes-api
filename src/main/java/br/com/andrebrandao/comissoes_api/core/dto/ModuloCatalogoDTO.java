// src/main/java/br/com/andrebrandao/comissoes_api/core/dto/ModuloCatalogoDTO.java
package br.com.andrebrandao.comissoes_api.core.dto;

import java.math.BigDecimal;

import br.com.andrebrandao.comissoes_api.core.model.Modulo;
import lombok.Builder;
import lombok.Data;

/**
 * DTO simplificado para exibir Módulos no catálogo público (/api/modulos/catalogo).
 * Oculta informações internas como 'status' e 'isPadrao'.
 */
@Data
@Builder
public class ModuloCatalogoDTO {

    private Long id;
    private String nome;
    private String chave; // A chave pode ser útil para o frontend identificar o módulo
    private String descricaoCurta;
    private BigDecimal precoMensal;

    public static ModuloCatalogoDTO fromEntity(Modulo modulo) {
        if (modulo == null) {
            return null;
        }
        return ModuloCatalogoDTO.builder()
                .id(modulo.getId())
                .nome(modulo.getNome())
                .chave(modulo.getChave())
                .descricaoCurta(modulo.getDescricaoCurta())
                .precoMensal(modulo.getPrecoMensal())
                .build();
    }
}