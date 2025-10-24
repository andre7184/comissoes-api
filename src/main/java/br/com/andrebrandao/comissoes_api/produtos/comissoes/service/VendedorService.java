package br.com.andrebrandao.comissoes_api.produtos.comissoes.service;

import java.util.List;
import java.util.stream.Collectors; // Import necessário
import java.util.Optional; // Import de Optional (mantido)
import java.math.BigDecimal;

import org.apache.commons.lang3.RandomStringUtils; 
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // Import Transactional

// --- IMPORTS OBRIGATÓRIOS ---
import br.com.andrebrandao.comissoes_api.core.model.Empresa; 
import br.com.andrebrandao.comissoes_api.core.repository.EmpresaRepository; 
import br.com.andrebrandao.comissoes_api.produtos.comissoes.dto.VendedorCriadoResponseDTO; 
import br.com.andrebrandao.comissoes_api.produtos.comissoes.dto.VendedorRequestDTO; 
import br.com.andrebrandao.comissoes_api.produtos.comissoes.dto.VendedorUpdateRequestDTO; 
import br.com.andrebrandao.comissoes_api.produtos.comissoes.dto.VendedorResponseDTO; // <-- NOVO
import br.com.andrebrandao.comissoes_api.produtos.comissoes.model.Vendedor;
import br.com.andrebrandao.comissoes_api.produtos.comissoes.repository.VendedorRepository; // <-- Necessário
import br.com.andrebrandao.comissoes_api.produtos.comissoes.repository.projection.VendedorComVendasProjection; // <-- NOVO
import br.com.andrebrandao.comissoes_api.security.model.Role; 
import br.com.andrebrandao.comissoes_api.security.model.User; 
import br.com.andrebrandao.comissoes_api.security.repository.UserRepository; 
import br.com.andrebrandao.comissoes_api.security.service.TenantService; // <-- Necessário

import jakarta.persistence.EntityNotFoundException; 
import lombok.RequiredArgsConstructor;

/**
 * Serviço com a lógica de negócio para a entidade Vendedor.
 * Inclui a criação do User associado e garante a segurança Multi-Tenant.
 */
@Service
@RequiredArgsConstructor
public class VendedorService {

    // --- Dependências Injetadas ---
    private final VendedorRepository vendedorRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TenantService tenantService;
    private final EmpresaRepository empresaRepository; // Necessário para getReferenceById

    // ... método criar() (Mantido, sem alteração na assinatura)
    @Transactional
    public VendedorCriadoResponseDTO criar(VendedorRequestDTO dto) {

        Long empresaId = tenantService.getEmpresaIdDoUsuarioLogado();

        if (userRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new IllegalStateException("O email informado já está em uso.");
        }

        String senhaGerada = RandomStringUtils.randomAlphanumeric(10);

        User novoUsuario = User.builder()
                .nome(dto.getNome())
                .email(dto.getEmail())
                .senha(passwordEncoder.encode(senhaGerada))
                .role(Role.ROLE_VENDEDOR)
                .empresa(empresaRepository.getReferenceById(empresaId))
                .build();
        User usuarioSalvo = userRepository.save(novoUsuario);

        Vendedor novoVendedor = Vendedor.builder()
                .percentualComissao(dto.getPercentualComissao())
                .empresa(empresaRepository.getReferenceById(empresaId))
                .usuario(usuarioSalvo)
                .build();
        Vendedor vendedorSalvo = vendedorRepository.save(novoVendedor);

        return VendedorCriadoResponseDTO.fromEntity(vendedorSalvo, senhaGerada);
    }


    /**
     * Lista todos os Vendedores (OTIMIZADO com Projeção).
     *
     * @return Lista de DTOs VendedorResponseDTO.
     */
    @Transactional(readOnly = true) // <-- ADICIONADO: Mantém a sessão ativa para carregar o User
    public List<VendedorResponseDTO> listar() { // <-- ASSINATURA ALTERADA
        Long empresaId = tenantService.getEmpresaIdDoUsuarioLogado();
        
        // 1. Chama a consulta otimizada
        List<VendedorComVendasProjection> projections = vendedorRepository.findAllWithVendasCount(empresaId);
        
        // 2. Mapeia a lista de Projeções para a lista final de DTOs de Resposta
        return projections.stream().map(projection -> {
            Vendedor vendedor = projection.getVendedor();
            Long qtdVendas = projection.getQtdVendas();
            BigDecimal valorTotalVendas = projection.getValorTotalVendas();
            
            return VendedorResponseDTO.fromEntity(vendedor, qtdVendas, valorTotalVendas);
        }).collect(Collectors.toList());
    }

    /**
     * Busca um Vendedor específico pelo seu ID.
     *
     * @param idDoVendedor O ID do Vendedor a ser buscado.
     * @return O DTO VendedorResponseDTO.
     */
    @Transactional(readOnly = true) // <-- ADICIONADO: Mantém a sessão ativa para carregar o User
    public VendedorResponseDTO buscarPorId(Long idDoVendedor) { // <-- ASSINATURA ALTERADA
        Long empresaId = tenantService.getEmpresaIdDoUsuarioLogado();
        Vendedor vendedor = vendedorRepository.findByEmpresaIdAndId(empresaId, idDoVendedor)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Vendedor não encontrado com o ID: " + idDoVendedor + " para esta empresa."));
        
        // Calcula a qtdVendas
        Long qtdVendas = vendedorRepository.contarVendasPorVendedorId(vendedor.getId());
        
        // Calcula o valor total de vendas (NOVO MÉTODO NECESSÁRIO NO REPOSITÓRIO)
        BigDecimal valorTotalVendas = vendedorRepository.somarVendasPorVendedorId(vendedor.getId()); 
        
        // Trata NULL para ZERO, caso o vendedor não tenha vendas
        if (valorTotalVendas == null) {
             valorTotalVendas = BigDecimal.ZERO;
        }
        // Mapeia a Entidade para o DTO
        return VendedorResponseDTO.fromEntity(vendedor, qtdVendas, valorTotalVendas);
    }

    /**
     * Atualiza o percentual de comissão de um Vendedor existente.
     *
     * @param idDoVendedor O ID do Vendedor a ser atualizado.
     * @param dto O DTO com o novo percentual de comissão.
     * @return O DTO VendedorResponseDTO do vendedor atualizado.
     */
    @Transactional // <-- ADICIONADO: Garante a transação para salvar e mapear
    public VendedorResponseDTO atualizar(Long idDoVendedor, VendedorUpdateRequestDTO dto) { // <-- ASSINATURA ALTERADA
        Long empresaId = tenantService.getEmpresaIdDoUsuarioLogado();
        Vendedor vendedorExistente = vendedorRepository.findByEmpresaIdAndId(empresaId, idDoVendedor)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Vendedor não encontrado com o ID: " + idDoVendedor + " para esta empresa."));

        vendedorExistente.setPercentualComissao(dto.getPercentualComissao());

        Vendedor vendedorAtualizado = vendedorRepository.save(vendedorExistente);
        
        // Recalcula a qtdVendas para o DTO de resposta
        Long qtdVendas = vendedorRepository.contarVendasPorVendedorId(vendedorAtualizado.getId());
        
        // Recalcula a valor total de vendas (NOVO MÉTODO NECESSÁRIO NO REPOSITÓRIO)
        BigDecimal valorTotalVendas = vendedorRepository.somarVendasPorVendedorId(vendedorAtualizado.getId());
        
        if (valorTotalVendas == null) {
             valorTotalVendas = BigDecimal.ZERO;
        }   

        // Mapeia e retorna o DTO
        return VendedorResponseDTO.fromEntity(vendedorAtualizado, qtdVendas, valorTotalVendas);
    }
}