package br.com.andrebrandao.comissoes_api.core.repository;

import java.util.List;
import java.util.Set; // Adicionado
import org.springframework.data.jpa.repository.JpaRepository;
import br.com.andrebrandao.comissoes_api.core.model.Modulo;
import br.com.andrebrandao.comissoes_api.core.model.ModuloStatus; // Adicionado

/**
 * Repositório para a entidade Modulo.
 * O Spring Data JPA implementará os métodos CRUD (Create, Read, Update, Delete).
 */
public interface ModuloRepository extends JpaRepository<Modulo, Long> {

    /**
     * Busca todos os módulos que correspondem a um status específico.
     * O Spring Data JPA cria a query "SELECT * FROM modulo WHERE status = ?"
     * automaticamente apenas pelo nome do método.
     *
     * @param status O status (Enum) para filtrar.
     * @return Uma lista de módulos.
     */
    List<Modulo> findByStatus(ModuloStatus status);

    /**
     * Busca todos os módulos cujos IDs estão presentes no conjunto (Set) fornecido.
     * O Spring Data JPA cria a query "SELECT * FROM modulo WHERE id IN (?, ?, ?)"
     * automaticamente.
     *
     * @param ids Um conjunto de IDs de Módulo.
     * @return Uma lista de Módulos encontrados (em um Set).
     */
    Set<Modulo> findAllByIdIn(Set<Long> ids);

    /**
     * Busca todos os módulos marcados como "Padrão" (isPadrao = true).
     * Usado para associar automaticamente a novas empresas.
     * @param IsPadrao O booleano (true ou false) para filtrar.
     * @return Lista de módulos.
     */
    List<Modulo> findByIsPadrao(boolean IsPadrao);

}