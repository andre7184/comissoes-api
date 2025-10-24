package br.com.andrebrandao.comissoes_api.produtos.comissoes.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import br.com.andrebrandao.comissoes_api.produtos.comissoes.model.Vendedor;
import br.com.andrebrandao.comissoes_api.produtos.comissoes.repository.projection.VendedorComVendasProjection; // NOVO IMPORT

import java.util.List;
import java.util.Optional;

@Repository
public interface VendedorRepository extends JpaRepository<Vendedor, Long> {

    List<Vendedor> findByEmpresaId(Long empresaId);
    Optional<Vendedor> findByEmpresaIdAndId(Long empresaId, Long id);

    // Contagem para busca individual/atualização
    @Query("SELECT COUNT(v) FROM Venda v WHERE v.vendedor.id = :vendedorId")
    Long contarVendasPorVendedorId(Long vendedorId);

    // Consulta OTIMIZADA para listagem (resolve N+1)
    @Query("SELECT NEW br.com.andrebrandao.comissoes_api.produtos.comissoes.repository.projection.VendedorComVendasProjection(v, COUNT(vn.id)) " +
           "FROM Vendedor v " +
           "LEFT JOIN v.usuario u " + 
           "LEFT JOIN Venda vn ON vn.vendedor = v " +
           "WHERE v.empresa.id = :empresaId " +
           "GROUP BY v.id, v.percentualComissao, u.id, u.nome, u.email") // Adicionar campos do User ao GROUP BY
    List<VendedorComVendasProjection> findAllWithVendasCount(Long empresaId);

}