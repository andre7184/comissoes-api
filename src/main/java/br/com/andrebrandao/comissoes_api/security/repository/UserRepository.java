package br.com.andrebrandao.comissoes_api.security.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

import br.com.andrebrandao.comissoes_api.security.model.Role;
import br.com.andrebrandao.comissoes_api.security.model.User;

/**
 * Repositório para a entidade User.
 */
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * O Spring Data JPA é inteligente. Ao ver um método com este nome,
     * ele automaticamente cria a query: "SELECT * FROM usuario WHERE email = ?"
     * * Usamos Optional<> porque o usuário pode não existir (evita NullPointerException).
     * * @param email O email a ser buscado.
     * @return um Optional contendo o User (se encontrado) ou vazio.
     */
    Optional<User> findByEmail(String email);

    /**
     * Conta o número de Usuários associados a uma Empresa específica.
     * Query Mágica: "SELECT COUNT(u) FROM User u WHERE u.empresa.id = ?"
     * @param empresaId O ID da Empresa.
     * @return A contagem de usuários.
     */
    Long countByEmpresaId(Long empresaId);

    /**
     * Conta o número de Usuários associados a uma Empresa específica E com um Role específico.
     * Query Mágica: "SELECT COUNT(u) FROM User u WHERE u.empresa.id = ? AND u.role = ?"
     * @param empresaId O ID da Empresa.
     * @param role O Role a ser contado (ex: Role.ROLE_ADMIN).
     * @return A contagem de usuários com esse role na empresa.
     */
    Long countByEmpresaIdAndRole(Long empresaId, Role role); // <-- NOVO MÉTODO
}