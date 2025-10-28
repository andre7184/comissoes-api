package br.com.andrebrandao.comissoes_api.core.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data; // Importa o Lombok
import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Entidade que representa um Módulo (produto) vendável no SaaS.
 * Controla o catálogo de funcionalidades do sistema.
 */
@Entity // 1. Informa ao JPA que esta classe é uma tabela no banco
@Table(name = "modulo") // 2. Define o nome da tabela no banco (boa prática)
@Data // 3. Anotação do Lombok: cria getters, setters, equals, hashCode e toString
      // automaticamente
public class Modulo {

    @Id // 4. Define que este campo é a Chave Primária (PK)
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 5. Define que o banco (Postgres)
                                                        // vai autoincrementar este número
    private Long id;

    @Column(nullable = false, unique = true) // 6. Campo não-nulo e único
    private String nome; // Ex: "Módulo de Helpdesk"

    /**
     * Chave interna usada pelo código para checagem de permissão.
     * Ex: "HELPDESK", "RELATORIOS_METAS"
     */
    @Column(nullable = false, unique = true)
    private String chave;

    @Enumerated(EnumType.STRING) // 7. Informa ao JPA para salvar o *texto* do Enum (ex:
                                 // "EM_DESENVOLVIMENTO")
    @Column(nullable = false)
    private ModuloStatus status;

    @Column(length = 500) // 8. Define um tamanho customizado (padrão é 255)
    private String descricaoCurta;

    @Column(precision = 10, scale = 2) // 9. Define a precisão para dinheiro (ex: // 12345678.99)
    private BigDecimal precoMensal;

    @Column(columnDefinition = "boolean default false")
    private boolean isPadrao; // "IsPadrao" = é padrão?

    // --- GETTER MANUAL PARA isPadrao COM A ANOTAÇÃO ---
    @JsonProperty("isPadrao") // <-- ADICIONAR ANOTAÇÃO AQUI
    public boolean isPadrao() {
        return isPadrao;
    }
}