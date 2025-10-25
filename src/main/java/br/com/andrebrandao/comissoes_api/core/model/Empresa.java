package br.com.andrebrandao.comissoes_api.core.model;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import org.hibernate.annotations.CreationTimestamp;

/**
 * Entidade que representa o Cliente (Tenant) do SaaS.
 * É a "empresa" que contrata o sistema.
 */
@Entity
@Table(name = "empresa")
@Data
public class Empresa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nomeFantasia;

    @Column(nullable = false, unique = true)
    private String cnpj; // Documento principal (ex: CNPJ)

    @CreationTimestamp // 1. Anotação do Hibernate: preenche com a data/hora
                       // de criação automaticamente
    @Column(nullable = false, updatable = false)
    private LocalDateTime dataCadastro;

    @Column(nullable = true, length = 255) // Permite nulo inicialmente
    private String razaoSocial;
    
    // --- AQUI ESTÁ A LÓGICA DE MÓDULOS ---

    /**
     * Define a lista de módulos que esta empresa *contratou* (tem ativo).
     * Esta é uma relação Muitos-para-Muitos.
     */
    @ManyToMany(fetch = FetchType.LAZY) // 2. Define a relação N-para-N.
                                        // LAZY = só carregar esta lista do banco
                                        // quando alguém chamar o método
                                        // getModulosAtivos()
    @JoinTable(
        name = "empresa_modulos_ativos",  // 3. Nome da "tabela de junção"
                                          // que será criada
        joinColumns = @JoinColumn(name = "empresa_id"), // 4. Coluna que referencia
                                                       // esta entidade (Empresa)
        inverseJoinColumns = @JoinColumn(name = "modulo_id") // 5. Coluna que
                                                             // referencia a
                                                             // *outra* entidade
                                                             // (Modulo)
    )
    private Set<Modulo> modulosAtivos = new HashSet<>(); // 6. É um Set (conjunto)
                                                         // para evitar módulos
                                                         // duplicados
}
