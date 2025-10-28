// src/main/java/br/com/andrebrandao/comissoes_api/security/dto/UsuarioAdminInfoDTO.java
package br.com.andrebrandao.comissoes_api.security.dto;

import br.com.andrebrandao.comissoes_api.security.model.User;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UsuarioAdminInfoDTO {
    private Long id;
    private String email;
    private String nome;

    public static UsuarioAdminInfoDTO fromEntity(User user) {
        if (user == null) return null;
        return UsuarioAdminInfoDTO.builder()
            .id(user.getId())
            .email(user.getEmail())
            .nome(user.getNome())
            .build();
    }
}