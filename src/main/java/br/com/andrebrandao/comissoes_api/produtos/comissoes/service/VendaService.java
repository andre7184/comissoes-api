package br.com.andrebrandao.comissoes_api.produtos.comissoes.service;

import java.math.BigDecimal;
import java.math.RoundingMode; // Importar RoundingMode
import java.util.List; // Importar List

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.andrebrandao.comissoes_api.core.repository.EmpresaRepository; // Do Core
import br.com.andrebrandao.comissoes_api.produtos.comissoes.dto.DashboardResponseDTO;
import br.com.andrebrandao.comissoes_api.produtos.comissoes.dto.HistoricoRendimentoDTO;
import br.com.andrebrandao.comissoes_api.produtos.comissoes.dto.VendaDetalheDTO;
import br.com.andrebrandao.comissoes_api.produtos.comissoes.dto.VendaRequestDTO;
import br.com.andrebrandao.comissoes_api.produtos.comissoes.model.Venda;
import br.com.andrebrandao.comissoes_api.produtos.comissoes.model.Vendedor;
import br.com.andrebrandao.comissoes_api.produtos.comissoes.repository.VendaRepository;
import br.com.andrebrandao.comissoes_api.produtos.comissoes.repository.VendedorRepository;
import br.com.andrebrandao.comissoes_api.security.service.TenantService; // Do Security
import br.com.andrebrandao.comissoes_api.produtos.comissoes.dto.VendaResponseDTO;
import br.com.andrebrandao.comissoes_api.produtos.comissoes.dto.VendedorRankingDTO;
import jakarta.persistence.EntityNotFoundException; // Import Exception
import lombok.RequiredArgsConstructor;

/**
 * Serviço com a lógica de negócio para a entidade Venda.
 * Inclui cálculo de comissão e garante a segurança Multi-Tenant.
 */
@Service
@RequiredArgsConstructor
public class VendaService {

    // --- Dependências Injetadas ---
    private final VendaRepository vendaRepository;
    private final VendedorRepository vendedorRepository; // Para buscar o vendedor e %
    private final EmpresaRepository empresaRepository; // Para criar a referência da empresa
    private final TenantService tenantService; // Para segurança Multi-Tenant

    /**
     * Lança uma nova Venda no sistema para um Vendedor específico.
     * Calcula a comissão automaticamente.
     * Garante que a venda seja lançada na empresa do Admin logado e
     * que o vendedor pertença a essa empresa.
     *
     * @param dto O DTO com os dados da venda (vendedorId, valorVenda).
     * @return A entidade Venda que foi salva (com comissão calculada e data).
     * @throws EntityNotFoundException se o vendedor não for encontrado para esta
     * empresa.
     */
    @Transactional // Garante a atomicidade
    public Venda lancar(VendaRequestDTO dto) {

        // 1. Pega o ID da Empresa do ADMIN logado
        Long empresaId = tenantService.getEmpresaIdDoUsuarioLogado();

        // 2. Busca o Vendedor, *validando* se ele pertence à empresa do Admin
        Vendedor vendedor = vendedorRepository.findByEmpresaIdAndId(empresaId, dto.getVendedorId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Vendedor não encontrado com o ID: " + dto.getVendedorId() + " para esta empresa."));

        // 3. Pega o percentual de comissão do Vendedor
        BigDecimal percentualComissao = vendedor.getPercentualComissao();
        if (percentualComissao == null) {
            // Poderia lançar um erro ou assumir 0, vamos assumir 0 por segurança
            percentualComissao = BigDecimal.ZERO;
            // Log.warn("Vendedor ID {} sem percentual de comissão definido.",
            // vendedor.getId()); // Adicionar logging seria bom
        }

        // 4. Calcula o valor da comissão
        BigDecimal valorComissao = dto.getValorVenda()
                .multiply(percentualComissao) // valorVenda * percentual
                .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP); // divide por 100, com 2 casas decimais e
                                                                         // arredondamento padrão

        // 5. Cria a entidade Venda
        Venda novaVenda = Venda.builder()
                .valorVenda(dto.getValorVenda())
                .valorComissaoCalculado(valorComissao) // Comissão calculada
                .vendedor(vendedor) // Associa ao vendedor encontrado
                .empresa(empresaRepository.getReferenceById(empresaId)) // Associa à empresa do Admin
                // A dataVenda será preenchida automaticamente pelo @CreationTimestamp
                .build();

        // 6. Salva a nova Venda no banco e a retorna
        return vendaRepository.save(novaVenda);
    }

    /**
     * Lista todas as Vendas pertencentes à empresa do usuário ADMIN logado.
     * Garante a segurança Multi-Tenant.
     *
     * @return Lista de DTOs VendaResponseDTO.
     */
    @Transactional(readOnly = true)
    public List<VendaResponseDTO> listar() {
        // 1. Pega o ID da Empresa do ADMIN logado
        Long empresaId = tenantService.getEmpresaIdDoUsuarioLogado();

        List<Venda> vendas = vendaRepository.findByEmpresaIdComVendedor(empresaId);

        // 2. Usa o método do repositório que filtra por empresa_id
        return vendas.stream()
                .map(VendaResponseDTO::fromEntity)
                .toList();
    }

    /**
     * Gera o Dashboard de Vendas completo para a empresa do Admin logado (Mês Atual e Rankings).
     * @return O DTO DashboardResponseDTO preenchido.
     */
    @Transactional(readOnly = true)
    public DashboardResponseDTO gerarDashboard() {
        Long empresaId = tenantService.getEmpresaIdDoUsuarioLogado();
        
        // --- 1. Busca Métricas Gerais do Mês (SQL Nativo) ---
        List<Object[]> totaisMesResult = vendaRepository.findTotaisDoMes(empresaId);
        
        // Mapeamento dos resultados escalares
        BigDecimal totalVendasMes = BigDecimal.ZERO;
        BigDecimal totalComissoesMes = BigDecimal.ZERO;
        Long qtdVendasMes = 0L;

        if (totaisMesResult != null && !totaisMesResult.isEmpty() && totaisMesResult.get(0).length == 3) {
            Object[] result = totaisMesResult.get(0);
            totalVendasMes = (BigDecimal) result[0];
            totalComissoesMes = (BigDecimal) result[1];
            // O COUNT() no SQL nativo pode retornar um BigInteger ou Long.
            // Para segurança, tratamos como Long.
            if (result[2] instanceof Number) {
                qtdVendasMes = ((Number) result[2]).longValue();
            } else {
                 qtdVendasMes = 0L;
            }
        }
        
        // --- 2. Calcula Média da Venda ---
        BigDecimal mediaVendaMes = BigDecimal.ZERO;
        if (qtdVendasMes > 0) {
            mediaVendaMes = totalVendasMes.divide(new BigDecimal(qtdVendasMes), 2, RoundingMode.HALF_UP);
        }

        // --- 2.1 Calcula Média da Comissão ---  // <-- ADICIONAR ESTE BLOCO
        BigDecimal mediaComissoesMes = BigDecimal.ZERO;
        if (qtdVendasMes > 0) {
            mediaComissoesMes = totalComissoesMes.divide(new BigDecimal(qtdVendasMes), 2, RoundingMode.HALF_UP);
        }

        // --- 3. Ranking de Vendedores (SQL Nativo) ---
        List<VendedorRankingDTO> rankingVendedores = vendaRepository.findRankingVendedores(empresaId).stream()
            .map(obj -> new VendedorRankingDTO(
                (String) obj[0], // nomeVendedor
                (Long) obj[1],   // idVendedor
                (BigDecimal) obj[2], // valorTotal
                ((Number) obj[3]).longValue() // qtdVendas (CAST para Long)
            )).toList();

        // --- 4. Maiores e Últimas Vendas (HQL com JOIN FETCH) ---
        // Usamos PageRequest para limitar os resultados (ex: top 5)
        PageRequest limit5 = PageRequest.of(0, 5);
        
        List<Venda> maioresVendasEntities = vendaRepository.findMaioresVendas(empresaId, limit5);
        List<Venda> ultimasVendasEntities = vendaRepository.findUltimasVendas(empresaId, limit5);
        
        // Mapeamento usando o método fromEntity do DTO
        List<VendaDetalheDTO> maioresVendas = maioresVendasEntities.stream()
            .map(VendaDetalheDTO::fromEntity)
            .toList();
            
        List<VendaDetalheDTO> ultimasVendas = ultimasVendasEntities.stream()
            .map(VendaDetalheDTO::fromEntity)
            .toList();
        
        // --- 5. Histórico Vendas Mensal (SQL Nativo) ---
        // Reutilizamos a query que retorna List<Object[]> e mapeamos para HistoricoRendimentoDTO
        List<HistoricoRendimentoDTO> historicoVendasMensal = vendaRepository.findHistoricoVendasMensal(empresaId).stream()
            .map(obj -> new HistoricoRendimentoDTO(
                (String) obj[0],        // mesAno
                (BigDecimal) obj[1],    // valorVendido
                (BigDecimal) obj[2]     // valorComissao
            ))
            .toList();


        // --- 6. Constrói e Retorna o DTO Principal ---
        return DashboardResponseDTO.builder()
                .totalVendasMes(totalVendasMes)
                .totalComissoesMes(totalComissoesMes)
                .qtdVendasMes(qtdVendasMes)
                .mediaVendaMes(mediaVendaMes)
                .mediaComissaoMes(mediaComissoesMes)
                .rankingVendedores(rankingVendedores)
                .maioresVendas(maioresVendas)
                .ultimasVendas(ultimasVendas)
                .historicoVendasMensal(historicoVendasMensal)
                .build();
    }
}