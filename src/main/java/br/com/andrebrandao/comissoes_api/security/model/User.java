package br.com.andrebrandao.comissoes_api.security.model;
import java.util.Collection;
import java.util.List;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import br.com.andrebrandao.comissoes_api.core.model.Empresa; // 1. Importa a
                                                           // Entidade do
                                                           // outro módulo
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entidade que representa um Usuário de login.
 * Implementa a interface UserDetails do Spring Security para integração de
 * login.
 */
@Entity
@Table(name = "usuario") // 2. "usuario" é melhor que "user" (palavra reservada
                         // de SQL)
@Data
@Builder // 3. Padrão de projeto "Builder", útil para criar objetos
@NoArgsConstructor
@AllArgsConstructor
public class User implements UserDetails { // 4. Implementa a interface do Spring
                                           // Security

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false, unique = true) // 5. O email será o "username" para
                                             // login
    private String email;

    @Column(nullable = false)
    private String senha; // 6. Esta será a senha criptografada (ex: BCrypt)

    @Enumerated(EnumType.STRING) // 7. Salva o texto do "Role" no banco
    @Column(nullable = false)
    private Role role;

    // --- LIGAÇÃO MULTI-TENANT ---
    @ManyToOne(fetch = FetchType.LAZY) // 8. Um Usuário pertence a UMA Empresa
    @JoinColumn(name = "empresa_id", nullable = false) // 9. Chave Estrangeira.
                                                      // Todo usuário (exceto
                                                      // SUPER_ADMIN)
                                                      // DEVE ter uma empresa.
    private Empresa empresa;

    // --- MÉTODOS OBRIGATÓRIOS DO "USERDETAILS" (Spring Security) ---
    // 10.
    // O Spring Security usa estes métodos para saber quem o usuário é.

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Retorna a "permissão" do usuário
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public String getPassword() {
        // Retorna a senha
        return this.senha;
    }

    @Override
    public String getUsername() {
        // Retorna o campo que usamos como "username" (login)
        return this.email;
    }

    // Por enquanto, vamos deixar tudo como 'true'.
    // No futuro, podemos adicionar colunas no banco (ex: "isAtivo")
    // para controlar isso.
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}