// src/main/java/br/com/andrebrandao/comissoes_api/security/dto/UsuarioResponseDTO.java
package br.com.andrebrandao.comissoes_api.security.dto;

import br.com.andrebrandao.comissoes_api.security.model.Role;
import br.com.andrebrandao.comissoes_api.security.model.User;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * DTO para retornar dados de um Usuário de forma segura (sem senha).
 * Inclui o ID da empresa associada.
 */
@Data
@Builder
public class UsuarioResponseDTO {
    private Long id;
    private String nome;
    private String email;
    private Role role;
    private LocalDateTime dataCriacao;
    private Long empresaId; // Apenas o ID da empresa

    public static UsuarioResponseDTO fromEntity(User user) {
        if (user == null) {
            return null;
        }
        return UsuarioResponseDTO.builder()
            .id(user.getId())
            .nome(user.getNome())
            .email(user.getEmail())
            .role(user.getRole())
            .dataCriacao(user.getDataCriacao())
            .empresaId(user.getEmpresa() != null ? user.getEmpresa().getId() : null) // Pega só o ID
            .build();
    }
}