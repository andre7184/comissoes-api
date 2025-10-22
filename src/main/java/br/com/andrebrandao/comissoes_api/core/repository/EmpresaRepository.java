package br.com.andrebrandao.comissoes_api.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import br.com.andrebrandao.comissoes_api.core.model.Empresa;

/**
 * Repositório para a entidade Empresa.
 */
public interface EmpresaRepository extends JpaRepository<Empresa, Long> {
    // Spring Data JPA vai prover os métodos:
    // save(), findById(), findAll(), deleteById(), ...
}