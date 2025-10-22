package br.com.andrebrandao.comissoes_api.produtos.comissoes.repository;

import java.util.List;
import java.util.Optional; // 1. Importar Optional

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.andrebrandao.comissoes_api.produtos.comissoes.model.Vendedor;

/**
 * Repositório para a entidade Vendedor.
 */
public interface VendedorRepository extends JpaRepository<Vendedor, Long> {

    /**
     * Busca todos os Vendedores que pertencem a uma Empresa específica.
     * Essencial para a lógica Multi-Tenant (um admin só vê seus vendedores).
     * Query Mágica: "SELECT * FROM vendedor WHERE empresa_id = ?"
     *
     * @param empresaId O ID da Empresa.
     * @return Lista de Vendedores.
     */
    List<Vendedor> findByEmpresaId(Long empresaId);

    /**
     * Busca um Vendedor específico pelo seu ID *E* pelo ID da Empresa.
     * Garante que um admin não possa buscar um vendedor de outra empresa
     * mesmo que saiba o ID do vendedor.
     * Query Mágica: "SELECT * FROM vendedor WHERE empresa_id = ? AND id = ?"
     *
     * @param empresaId O ID da Empresa.
     * @param id        O ID do Vendedor.
     * @return um Optional contendo o Vendedor (se encontrado para aquela empresa)
     * ou vazio.
     */
    Optional<Vendedor> findByEmpresaIdAndId(Long empresaId, Long id);

    // TODO: Adicionar um método findByUsuarioId(Long usuarioId) se necessário
    // para o RelatorioVendedorController

}