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
import br.com.andrebrandao.comissoes_api.produtos.comissoes.model.VendaStatus;
import br.com.andrebrandao.comissoes_api.produtos.comissoes.model.Vendedor;
import br.com.andrebrandao.comissoes_api.produtos.comissoes.repository.VendaRepository;
import br.com.andrebrandao.comissoes_api.produtos.comissoes.repository.VendedorRepository;
// Adicione estas duas linhas na sua seção de imports
import br.com.andrebrandao.comissoes_api.produtos.comissoes.dto.VendaVendedorRequestDTO;
import br.com.andrebrandao.comissoes_api.security.model.User;
import br.com.andrebrandao.comissoes_api.security.service.TenantService; // Do Security
import br.com.andrebrandao.comissoes_api.produtos.comissoes.dto.VendaResponseDTO;
import br.com.andrebrandao.comissoes_api.produtos.comissoes.dto.VendedorRankingDTO;
import br.com.andrebrandao.comissoes_api.produtos.comissoes.dto.VendaUpdateRequestDTO;
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
        // --- MUDANÇA AQUI ---
        Venda novaVenda = Venda.builder()
                .valorVenda(dto.getValorVenda())
                .valorComissaoCalculado(valorComissao)
                .vendedor(vendedor)
                .empresa(empresaRepository.getReferenceById(empresaId))
                .status(VendaStatus.CONFIRMADA) // 2. Define o status como CONFIRMADA
                .build();

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

    /**
     * Atualiza o valor e/ou descrição de uma venda existente.
     * **Recalcula a comissão** com base no novo valor e no percentual atual do vendedor.
     * Garante que a venda pertença à empresa do Admin logado.
     *
     * @param idVenda O ID da venda a ser atualizada.
     * @param dto O DTO com o novo valorVenda e/ou descricaoVenda.
     * @return O DTO de resposta da venda atualizada.
     */
    @Transactional
    public VendaResponseDTO atualizarVenda(Long idVenda, VendaUpdateRequestDTO dto) {
        // 1. Pega o ID da Empresa do ADMIN logado
        Long empresaId = tenantService.getEmpresaIdDoUsuarioLogado();

        // 2. Busca a Venda, garantindo que ela pertença à empresa logada
        // Precisamos buscar a Venda e o Vendedor LAZY (se não tiver JOIN FETCH, a transação deve estar aberta)
        Venda vendaExistente = vendaRepository.findByEmpresaId(empresaId)
                .stream()
                .filter(v -> v.getId().equals(idVenda))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException(
                        "Venda não encontrada com o ID: " + idVenda + " para esta empresa."));
        
        // 3. Atualiza os campos: valorVenda e descricaoVenda
        BigDecimal novoValorVenda = dto.getValorVenda();
        vendaExistente.setValorVenda(novoValorVenda);
        vendaExistente.setDescricaoVenda(dto.getDescricaoVenda());
        
        // 4. Recalcula a Comissão
        // O Vendedor é LAZY, acessamos aqui dentro da transação:
        Vendedor vendedor = vendaExistente.getVendedor();
        if (vendedor == null) {
            throw new IllegalStateException("Venda não associada a um vendedor válido.");
        }

        BigDecimal percentualComissao = vendedor.getPercentualComissao();
        if (percentualComissao == null) {
            percentualComissao = BigDecimal.ZERO;
        }
        
        // Novo Cálculo
        BigDecimal novoValorComissao = novoValorVenda
                .multiply(percentualComissao)
                .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);

        vendaExistente.setValorComissaoCalculado(novoValorComissao);

        // 5. Salva (o save é necessário para garantir o flush, embora @Transactional // faça o trabalho)
        Venda vendaAtualizada = vendaRepository.save(vendaExistente);

        // 6. Retorna o DTO de resposta
        return VendaResponseDTO.fromEntity(vendaAtualizada);
    }

    // ========================================================================
    // MÉTODOS PARA O VENDEDOR (ROLE_VENDEDOR)
    // ========================================================================

    /**
     * Lança uma nova Venda no sistema para o *próprio vendedor logado*.
     * A Venda é criada com o status 'PENDENTE' para aprovação do Admin.
     *
     * @param dto O DTO com os dados da venda (apenas valorVenda).
     * @return A entidade Venda que foi salva (com status PENDENTE).
     * @throws EntityNotFoundException se o usuário logado não for um vendedor.
     */
    @Transactional
    public Venda lancarPeloVendedor(VendaVendedorRequestDTO dto) {

        // 1. Pega o *usuário* logado (pelo Token JWT)
        User usuarioLogado = tenantService.getUsuarioLogado();
        Long empresaId = usuarioLogado.getEmpresa().getId();

        // 2. Encontra o Vendedor associado a esse usuário
        //    (Usa o método que criamos no VendedorRepository)
        Vendedor vendedor = vendedorRepository.findByUsuarioId(usuarioLogado.getId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Nenhum perfil de vendedor encontrado para o usuário logado."));

        // 3. Pega o percentual de comissão deste vendedor
        BigDecimal percentualComissao = vendedor.getPercentualComissao();
        if (percentualComissao == null) {
            percentualComissao = BigDecimal.ZERO;
        }

        // 4. Calcula o valor da comissão
        BigDecimal valorComissao = dto.getValorVenda()
                .multiply(percentualComissao)
                .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);

        // 5. Cria a entidade Venda
        Venda novaVenda = Venda.builder()
                .valorVenda(dto.getValorVenda())
                .valorComissaoCalculado(valorComissao)
                .vendedor(vendedor) // Associa ao vendedor logado
                .empresa(empresaRepository.getReferenceById(empresaId)) // Associa à empresa
                .status(VendaStatus.PENDENTE) // 6. Venda do Vendedor é PENDENTE
                .build();

        return vendaRepository.save(novaVenda);
    }

    /**
     * Lista todas as Vendas *apenas* do Vendedor logado.
     *
     * @return Lista de entidades Venda.
     * @throws EntityNotFoundException se o usuário logado não for um vendedor.
     */
    public List<Venda> listarMinhasVendas() {
        
        // 1. Pega o usuário logado
        User usuarioLogado = tenantService.getUsuarioLogado();

        // 2. Encontra o Vendedor associado
        Vendedor vendedor = vendedorRepository.findByUsuarioId(usuarioLogado.getId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Nenhum perfil de vendedor encontrado para o usuário logado."));
        
        // 3. Usa o método do repositório para buscar vendas APENAS desse vendedor
        return vendaRepository.findByVendedorId(vendedor.getId());
    }

    // ========================================================================
    // MÉTODOS DE GERENCIAMENTO (PARA O ADMIN)
    // ========================================================================

    /**
     * Altera o status de uma Venda (que deve ser 'PENDENTE') para 'CONFIRMADA'.
     * Apenas o Admin da empresa dona da venda pode fazer isso.
     *
     * @param vendaId O ID da Venda a ser aprovada.
     * @return A Venda atualizada com o status CONFIRMADA.
     * @throws EntityNotFoundException se a venda não for encontrada.
     * @throws IllegalStateException se a venda não estiver PENDENTE.
     */
    @Transactional
    public Venda aprovarVenda(Long vendaId) {
        Long empresaId = tenantService.getEmpresaIdDoUsuarioLogado();

        // 1. Busca a venda usando o método seguro que acabamos de criar no
        // repositório
        Venda venda = vendaRepository.findByEmpresaIdAndId(empresaId, vendaId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Venda não encontrada com o ID: " + vendaId + " para esta empresa."));

        // 2. Regra de Negócio: Só podemos aprovar vendas PENDENTES
        if (venda.getStatus() != VendaStatus.PENDENTE) {
            throw new IllegalStateException(
                    "Apenas vendas com status PENDENTE podem ser aprovadas. Status atual: " + venda.getStatus());
        }

        // 3. Atualiza o status
        venda.setStatus(VendaStatus.CONFIRMADA);

        // 4. Salva a Venda (JPA faz o UPDATE)
        return vendaRepository.save(venda);
    }

    /**
     * Altera o status de uma Venda para 'CANCELADA'.
     * Apenas o Admin da empresa dona da venda pode fazer isso.
     * (Permite cancelar vendas PENDENTES ou CONFIRMADAS - ex: devolução)
     *
     * @param vendaId O ID da Venda a ser cancelada.
     * @return A Venda atualizada com o status CANCELADA.
     * @throws EntityNotFoundException se a venda não for encontrada.
     */
    @Transactional
    public Venda cancelarVenda(Long vendaId) {
        Long empresaId = tenantService.getEmpresaIdDoUsuarioLogado();

        // 1. Busca a venda usando o método seguro
        Venda venda = vendaRepository.findByEmpresaIdAndId(empresaId, vendaId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Venda não encontrada com o ID: " + vendaId + " para esta empresa."));

        // 2. Regra de Negócio (Opcional): Não podemos cancelar algo já
        // cancelado
        if (venda.getStatus() == VendaStatus.CANCELADA) {
            throw new IllegalStateException("Esta venda já está cancelada.");
        }

        // 3. Atualiza o status
        venda.setStatus(VendaStatus.CANCELADA);

        // 4. Salva a Venda
        return vendaRepository.save(venda);
    }
}