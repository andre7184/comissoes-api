package br.com.andrebrandao.comissoes_api.security.dto;

import java.util.Collection;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO que representa o JSON de Resposta de um login bem-sucedido.
 * É o que a API vai devolver para o frontend.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {

    private String token; // O Token JWT gerado
    private Collection<String> permissoesModulos; // A lista de chaves (ex: ["HELPDESK", "COMISSOES_CORE"])

}