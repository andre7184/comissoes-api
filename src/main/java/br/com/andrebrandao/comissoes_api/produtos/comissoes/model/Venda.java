package br.com.andrebrandao.comissoes_api.produtos.comissoes.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import br.com.andrebrandao.comissoes_api.core.model.Empresa; // 1. Importa do Módulo Core
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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

    // TODO: Adicionar um campo 'status' (ex: PENDENTE, PAGO) no futuro

    // --- LIGAÇÕES ---

    @ManyToOne(fetch = FetchType.LAZY) // 3. Muitas Vendas pertencem a UM Vendedor
    @JoinColumn(name = "vendedor_id", nullable = false)
    private Vendedor vendedor;

    @ManyToOne(fetch = FetchType.LAZY) // 4. Muitas Vendas pertencem a UMA Empresa
    @JoinColumn(name = "empresa_id", nullable = false) // 5. Redundante, mas ESSENCIAL
                                                      // para Multi-Tenancy
    private Empresa empresa;

}