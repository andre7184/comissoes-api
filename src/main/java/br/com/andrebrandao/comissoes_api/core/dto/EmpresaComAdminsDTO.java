// src/main/java/br/com/andrebrandao/comissoes_api/core/dto/EmpresaComAdminsDTO.java
package br.com.andrebrandao.comissoes_api.core.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import br.com.andrebrandao.comissoes_api.core.model.Empresa;
import br.com.andrebrandao.comissoes_api.core.model.Modulo;
import br.com.andrebrandao.comissoes_api.security.dto.UsuarioAdminInfoDTO;
import br.com.andrebrandao.comissoes_api.security.model.User;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EmpresaComAdminsDTO {

    private Long id;
    private String nomeFantasia;
    private String cnpj;
    private LocalDateTime dataCadastro;
    private String razaoSocial;
    private Set<Modulo> modulosAtivos;
    private List<UsuarioAdminInfoDTO> usuariosAdmin;

    public static EmpresaComAdminsDTO fromEntity(Empresa empresa, List<User> admins) {
        if (empresa == null) return null;

        List<UsuarioAdminInfoDTO> adminDTOs = admins.stream()
            .map(UsuarioAdminInfoDTO::fromEntity)
            .collect(Collectors.toList());

        // Acessar modulosAtivos aqui dentro do builder para inicializar LAZY loading se necessário
        Set<Modulo> modulosCarregados = empresa.getModulosAtivos();
        // Para garantir que foi carregado (opcional, mas seguro)
        if (modulosCarregados != null) modulosCarregados.size();


        return EmpresaComAdminsDTO.builder()
            .id(empresa.getId())
            .nomeFantasia(empresa.getNomeFantasia())
            .cnpj(empresa.getCnpj())
            .dataCadastro(empresa.getDataCadastro())
            .razaoSocial(empresa.getRazaoSocial())
            .modulosAtivos(modulosCarregados) // Usa a coleção inicializada
            .usuariosAdmin(adminDTOs)
            .build();
    }
}