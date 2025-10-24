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

}