// src/main/java/br/com/andrebrandao/comissoes_api/core/dto/EmpresaDetalhesDTO.java
package br.com.andrebrandao.comissoes_api.core.dto;

import java.time.LocalDateTime;

import br.com.andrebrandao.comissoes_api.core.model.Empresa;
import lombok.Builder;
import lombok.Data;

/**
 * DTO para retornar detalhes da Empresa logada no endpoint /api/empresa/me.
 * Inclui a contagem de vendedores associados.
 */
@Data
@Builder
public class EmpresaDetalhesDTO {

    private Long id;
    private String nomeFantasia;
    // Nota: A razão social não está na sua entidade Empresa.java. Adicione se necessário.
    private String razaoSocial; 
    private String cnpj;
    private LocalDateTime dataCadastro;
    private Long qtdAdmins; // Contagem dos vendedores

    public static EmpresaDetalhesDTO fromEntity(Empresa empresa, Long qtdUsuarios) {
        if (empresa == null) {
            return null;
        }
        return EmpresaDetalhesDTO.builder()
                .id(empresa.getId())
                .nomeFantasia(empresa.getNomeFantasia())
                .razaoSocial(empresa.getRazaoSocial())
                .cnpj(empresa.getCnpj())
                .dataCadastro(empresa.getDataCadastro())
                .qtdAdmins(qtdUsuarios)
                .build();
    }
}