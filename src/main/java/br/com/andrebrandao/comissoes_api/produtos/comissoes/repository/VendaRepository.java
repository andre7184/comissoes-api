package br.com.andrebrandao.comissoes_api.produtos.comissoes.repository;

import java.util.List; // 1. Importar List

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import br.com.andrebrandao.comissoes_api.produtos.comissoes.model.Venda;

/**
 * Repositório para a entidade Venda.
 */
public interface VendaRepository extends JpaRepository<Venda, Long> {

    /**
     * Busca todas as Vendas que pertencem a uma Empresa específica.
     * Essencial para relatórios e listagens Multi-Tenant.
     * Query Mágica: "SELECT * FROM venda WHERE empresa_id = ?"
     *
     * @param empresaId O ID da Empresa.
     * @return Lista de Vendas.
     */
    List<Venda> findByEmpresaId(Long empresaId);

    /**
     * Busca todas as Vendas de um Vendedor específico.
     * Útil para o futuro Portal do Vendedor e relatórios individuais.
     * Query Mágica: "SELECT * FROM venda WHERE vendedor_id = ?"
     *
     * @param vendedorId O ID do Vendedor.
     * @return Lista de Vendas.
     */
    List<Venda> findByVendedorId(Long vendedorId);

    /**
     * Busca as Vendas carregando EAGERLY os relacionamentos Vendedor e User.
     * Necessário para conversão em DTO e evitar LazyInitializationException.
     */
    @Query("SELECT v FROM Venda v JOIN FETCH v.vendedor vend JOIN FETCH vend.usuario u WHERE v.empresa.id = :empresaId")
    List<Venda> findByEmpresaIdComVendedor(Long empresaId);
    
    // TODO: Adicionar métodos com filtros de data (findByEmpresaIdAndDataVendaBetween)
    // quando formos fazer os relatórios.

    // SQL Nativo para agregados do mês atual
    @Query(value = "SELECT " +
           "    COALESCE(SUM(v.valor_venda), 0) AS totalVendas, " +
           "    COALESCE(SUM(v.valor_comissao_calculado), 0) AS totalComissoes, " +
           "    COUNT(v.id) AS qtdVendas " +
           "FROM " +
           "    venda v " +
           "WHERE " +
           "    v.empresa_id = :empresaId AND " +
           "    TO_CHAR(v.data_venda, 'YYYY-MM') = TO_CHAR(CURRENT_TIMESTAMP, 'YYYY-MM')",
           nativeQuery = true)
    List<Object[]> findTotaisDoMes(Long empresaId);

    // SQL Nativo para Ranking de Vendedores (mês atual)
    @Query(value = "SELECT " +
           "    u.nome AS nomeVendedor, " +
           "    vendedor.id AS idVendedor, " +
           "    COALESCE(SUM(venda.valor_venda), 0) AS valorTotal, " +
           "    COUNT(venda.id) AS qtdVendas " +
           "FROM " +
           "    venda venda " +
           "INNER JOIN " +
           "    vendedor vendedor ON vendedor.id = venda.vendedor_id " +
           "INNER JOIN " +
           "    usuario u ON u.id = vendedor.user_id " +
           "WHERE " +
           "    venda.empresa_id = :empresaId AND " +
           "    TO_CHAR(venda.data_venda, 'YYYY-MM') = TO_CHAR(CURRENT_TIMESTAMP, 'YYYY-MM') " +
           "GROUP BY " +
           "    u.nome, vendedor.id " +
           "ORDER BY " +
           "    valorTotal DESC, qtdVendas DESC " +
           "LIMIT 5",
           nativeQuery = true)
    List<Object[]> findRankingVendedores(Long empresaId);

    // HQL para buscar as N maiores vendas (necessita JOIN FETCH)
    // Ordena por valor e limita o resultado (ex: 5)
    @Query("SELECT v FROM Venda v " +
           "JOIN FETCH v.vendedor vend " +
           "JOIN FETCH vend.usuario u " +
           "WHERE v.empresa.id = :empresaId " +
           "ORDER BY v.valorVenda DESC")
    List<Venda> findMaioresVendas(Long empresaId, org.springframework.data.domain.Pageable pageable);

    // HQL para buscar as N últimas vendas (necessita JOIN FETCH)
    // Ordena por data e limita o resultado (ex: 5)
    @Query("SELECT v FROM Venda v " +
           "JOIN FETCH v.vendedor vend " +
           "JOIN FETCH vend.usuario u " +
           "WHERE v.empresa.id = :empresaId " +
           "ORDER BY v.dataVenda DESC")
    List<Venda> findUltimasVendas(Long empresaId, org.springframework.data.domain.Pageable pageable);

    // SQL Nativo para Histórico Mensal de TODAS as vendas da Empresa
    @Query(value = "SELECT " +
           "    TO_CHAR(v.data_venda, 'YYYY-MM') AS mesAno, " +
           "    COALESCE(SUM(v.valor_venda), 0) AS valorVendido, " +
           "    COALESCE(SUM(v.valor_comissao_calculado), 0) AS valorComissao " +
           "FROM " +
           "    venda v " +
           "WHERE " +
           "    v.empresa_id = :empresaId " +
           "GROUP BY " +
           "    TO_CHAR(v.data_venda, 'YYYY-MM') " +
           "ORDER BY " +
           "    mesAno DESC",
           nativeQuery = true)
    List<Object[]> findHistoricoVendasMensal(Long empresaId);
}