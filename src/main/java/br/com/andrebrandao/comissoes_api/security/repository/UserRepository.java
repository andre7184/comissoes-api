package br.com.andrebrandao.comissoes_api.security.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
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

}