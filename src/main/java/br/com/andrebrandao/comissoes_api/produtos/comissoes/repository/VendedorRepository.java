package br.com.andrebrandao.comissoes_api.produtos.comissoes.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import br.com.andrebrandao.comissoes_api.produtos.comissoes.dto.HistoricoRendimentoDTO;
import br.com.andrebrandao.comissoes_api.produtos.comissoes.model.Vendedor;
import br.com.andrebrandao.comissoes_api.produtos.comissoes.repository.projection.HistoricoRendimentoProjection;
import br.com.andrebrandao.comissoes_api.produtos.comissoes.repository.projection.VendedorComVendasProjection; // NOVO IMPORT

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface VendedorRepository extends JpaRepository<Vendedor, Long> {

    List<Vendedor> findByEmpresaId(Long empresaId);
    Optional<Vendedor> findByEmpresaIdAndId(Long empresaId, Long id);

    // Contagem para busca individual/atualização
    @Query("SELECT COUNT(v) FROM Venda v WHERE v.vendedor.id = :vendedorId")
    Long contarVendasPorVendedorId(Long vendedorId);

    // Soma para busca individual/atualização
    @Query("SELECT SUM(v.valorVenda) FROM Venda v WHERE v.vendedor.id = :vendedorId")
    BigDecimal somarVendasPorVendedorId(Long vendedorId); // NOVO MÉTODO PARA SOMA

    // Consulta OTIMIZADA para listagem (resolve N+1)
    @Query("SELECT NEW br.com.andrebrandao.comissoes_api.produtos.comissoes.repository.projection.VendedorComVendasProjection(" + 
            "v, " + 
            "COUNT(vn.id), " + 
            "SUM(vn.valorVenda)" +
            ") " +
            "FROM Vendedor v " +
            "LEFT JOIN v.usuario u " + 
            "LEFT JOIN Venda vn ON vn.vendedor = v " +
            "WHERE v.empresa.id = :empresaId " +
            "GROUP BY v.id, v.percentualComissao, u.id, u.nome, u.email") // Adicionar campos do User ao GROUP BY
    List<VendedorComVendasProjection> findAllWithVendasCount(Long empresaId);
    
    // VendedorRepository.java (Apenas a Query findHistoricoRendimentosMensais)
    @Query(value = "SELECT " +
           "    TO_CHAR(v.data_venda, 'YYYY-MM') AS mesAno, " + // Coluna 1
           "    COALESCE(SUM(v.valor_venda), 0) AS valorVendido, " + // Coluna 2
           "    COALESCE(SUM(v.valor_comissao_calculado), 0) AS valorComissao " + // Coluna 3
           "FROM " +
           "    venda v " +
           "WHERE " +
           "    v.vendedor_id = :vendedorId " +
           "GROUP BY " +
           "    TO_CHAR(v.data_venda, 'YYYY-MM') " +
           "ORDER BY " +
           "    mesAno DESC",
           nativeQuery = true) // <--- ATENÇÃO: AGORA É SQL PURO
    List<HistoricoRendimentoProjection> findHistoricoRendimentosMensais(Long vendedorId);
}