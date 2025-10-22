package br.com.andrebrandao.comissoes_api.core.dto;

import java.math.BigDecimal;
import br.com.andrebrandao.comissoes_api.core.model.ModuloStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * DTO (Data Transfer Object) para *receber* dados ao criar ou atualizar um Módulo.
 * Inclui validações dos campos de entrada.
 */
@Data
public class ModuloRequestDTO {

    @NotBlank(message = "O nome do módulo não pode ser vazio.") // 1. Validação: Não
                                                               // pode ser nulo
                                                               // nem vazio
    @Size(min = 3, max = 100, message = "O nome deve ter entre 3 e 100 caracteres.") // 2.
                                                                                    // Validação:
                                                                                    // Tamanho
    private String nome;

    @NotBlank(message = "A chave do módulo não pode ser vazia.")
    @Size(min = 3, max = 50, message = "A chave deve ter entre 3 e 50 caracteres.")
    private String chave; // Ex: "HELPDESK"

    @NotNull(message = "O status do módulo não pode ser nulo.") // 3. Validação:
                                                               // Não pode ser
                                                               // nulo
    private ModuloStatus status;

    @Size(max = 500, message = "A descrição curta não pode exceder 500 caracteres.")
    private String descricaoCurta;

    @NotNull(message = "O preço mensal não pode ser nulo.")
    @PositiveOrZero(message = "O preço mensal deve ser zero ou positivo.") // 4. // Validação: // Não pode ser negativo
    private BigDecimal precoMensal;

    /**
     * Define se este módulo é um módulo "padrão" (associado a novas empresas).
     * Usamos 'Boolean' (objeto) em vez de 'boolean' (primitivo)
     * para podermos usar a validação @NotNull.
     */
    @NotNull(message = "É necessário informar se o módulo é padrão (true ou false).")
    private Boolean isPadrao; // Indica se o módulo é padrão
}