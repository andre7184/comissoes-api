package br.com.andrebrandao.comissoes_api.core.dto;

import java.util.Set;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * DTO (Data Transfer Object) para *receber* a lista de IDs de módulos
 * que devem ser ativados para uma empresa.
 * Será usado no endpoint: PUT /api/superadmin/empresas/{id}/modulos
 */
@Data
public class AtualizarModulosEmpresaRequestDTO {

    /**
     * Um conjunto (Set) de IDs de Módulos.
     * Ex: [1, 3, 5]
     * (Usamos Set em vez de List para garantir que não haja IDs duplicados)
     */
    @NotNull(message = "A lista de IDs de módulos não pode ser nula (pode ser vazia).")
    private Set<Long> moduloIds;
    
}