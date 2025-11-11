package br.com.andrebrandao.comissoes_api.produtos.comissoes.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import br.com.andrebrandao.comissoes_api.core.model.Empresa; // 1. Importa do Módulo Core
import com.fasterxml.jackson.annotation.JsonIgnore; // NOVO IMPORT
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
import org.hibernate.annotations.CreationTimestamp; // Para data automática

/**
 * Entidade que representa uma Venda realizada por um Vendedor,
 * incluindo o valor da comissão calculado.
 */
@Entity
@Table(name = "venda")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Venda {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, precision = 15, scale = 2) // Maior precisão para valor
    private BigDecimal valorVenda;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal valorComissaoCalculado;

    @CreationTimestamp // 2. O Hibernate preenche a data/hora automaticamente
    @Column(nullable = false, updatable = false)
    private LocalDateTime dataVenda;

    // --- CAMPO ADICIONADO ---
    /**
     * Define o status atual da venda (Pendente, Confirmada, Cancelada).
     */
    @Enumerated(EnumType.STRING) // 3. Diz ao JPA para salvar o *texto* do Enum (ex: "PENDENTE")
    @Column(nullable = false) // 4. Garante que uma venda NUNCA seja salva sem um status
    private VendaStatus status;

    // --- LIGAÇÕES ---

    @ManyToOne(fetch = FetchType.LAZY) // 3. Muitas Vendas pertencem a UM Vendedor
    @JoinColumn(name = "vendedor_id", nullable = false) //
    @JsonIgnore // <-- ADICIONADO: Ignora na serialização para resolver o erro LAZY.
    private Vendedor vendedor;

    @Column(nullable = true) // 6. Permite nulo inicialmente
    private String descricaoVenda;

    @ManyToOne(fetch = FetchType.LAZY) // 4. Muitas Vendas pertencem a UMA Empresa
    @JoinColumn(name = "empresa_id", nullable = false) // 5. Redundante, mas ESSENCIAL | para Multi-Tenancy
    @JsonIgnore // <-- ADICIONADO: Ignora na serialização para resolver o erro LAZY.
    private Empresa empresa;
    

}