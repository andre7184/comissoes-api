package br.com.andrebrandao.comissoes_api.produtos.comissoes.model;

import java.math.BigDecimal;

import br.com.andrebrandao.comissoes_api.core.model.Empresa;
import br.com.andrebrandao.comissoes_api.security.model.User;
import com.fasterxml.jackson.annotation.JsonIgnore; // Import mantido
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entidade que representa um Vendedor (funcionário) de uma Empresa cliente.
 * Cada Vendedor está associado a um User para login no futuro Portal do Vendedor.
 */
@Entity
@Table(name = "vendedor")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Vendedor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(precision = 5, scale = 2) // Ex: 123.45% (precisa ajustar se quiser > 999%)
    private BigDecimal percentualComissao;

    // --- LIGAÇÃO MULTI-TENANT ---
    @ManyToOne(fetch = FetchType.LAZY) 
    @JoinColumn(name = "empresa_id", nullable = false)
    @JsonIgnore // <-- MANTIDO: Evita erro de serialização do campo Lazy e não solicitado.
    private Empresa empresa;

    // --- LIGAÇÃO COM O LOGIN ---
    @OneToOne(fetch = FetchType.LAZY) 
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false, unique = true)
    // @JsonIgnore <-- REMOVIDO: O relacionamento precisa ser acessado dentro da transação para popular o DTO.
    private User usuario;

    // TODO: Adicionar outros campos se necessário (ex: nome completo, CPF,
    // data_admissao)
    // Se adicionar nome/cpf aqui, não precisa pegar do User associado sempre.
}