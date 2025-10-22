package br.com.andrebrandao.comissoes_api.produtos.comissoes.service;

import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.RandomStringUtils; // Import para gerar senha
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // Import Transactional

import br.com.andrebrandao.comissoes_api.core.model.Empresa; // Do Core
import br.com.andrebrandao.comissoes_api.core.repository.EmpresaRepository; // Do Core (necessário para getReferenceById)
import br.com.andrebrandao.comissoes_api.produtos.comissoes.dto.VendedorCriadoResponseDTO; // DTO de Resposta da Criação
import br.com.andrebrandao.comissoes_api.produtos.comissoes.dto.VendedorRequestDTO; // DTO de Requisição da Criação
import br.com.andrebrandao.comissoes_api.produtos.comissoes.dto.VendedorUpdateRequestDTO; // DTO de Requisição da Atualização
import br.com.andrebrandao.comissoes_api.produtos.comissoes.model.Vendedor;
import br.com.andrebrandao.comissoes_api.produtos.comissoes.repository.VendedorRepository;
import br.com.andrebrandao.comissoes_api.security.model.Role; // Do Security
import br.com.andrebrandao.comissoes_api.security.model.User; // Do Security
import br.com.andrebrandao.comissoes_api.security.repository.UserRepository; // Do Security
import br.com.andrebrandao.comissoes_api.security.service.TenantService; // Do Security
import jakarta.persistence.EntityNotFoundException; // Import Exception
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

    /**
     * Cria um novo Vendedor e o User associado a ele.
     * Operação transacional: ou salva os dois, ou não salva nenhum.
     * Garante que o Vendedor seja criado na empresa do Admin logado.
     * Gera uma senha aleatória para o novo usuário.
     *
     * @param dto O DTO com os dados do vendedor (nome, email, percentual).
     * @return O DTO de resposta com os dados do vendedor e a senha temporária gerada.
     * @throws IllegalStateException se o email já estiver em uso.
     */
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
     * Lista todos os Vendedores pertencentes à empresa do usuário ADMIN logado.
     * Garante a segurança Multi-Tenant.
     *
     * @return Lista de entidades Vendedor.
     */
    public List<Vendedor> listar() {
        Long empresaId = tenantService.getEmpresaIdDoUsuarioLogado();
        return vendedorRepository.findByEmpresaId(empresaId);
    }

    /**
     * Busca um Vendedor específico pelo seu ID, mas APENAS se ele pertencer
     * à empresa do usuário ADMIN logado.
     * Garante a segurança Multi-Tenant.
     *
     * @param idDoVendedor O ID do Vendedor a ser buscado.
     * @return A entidade Vendedor.
     * @throws EntityNotFoundException se o vendedor não for encontrado *para esta empresa*.
     */
    public Vendedor buscarPorId(Long idDoVendedor) {
        Long empresaId = tenantService.getEmpresaIdDoUsuarioLogado();
        return vendedorRepository.findByEmpresaIdAndId(empresaId, idDoVendedor)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Vendedor não encontrado com o ID: " + idDoVendedor + " para esta empresa."));
    }

    /**
     * Atualiza o percentual de comissão de um Vendedor existente.
     * Garante que o ADMIN só possa atualizar vendedores da sua própria empresa.
     *
     * @param idDoVendedor O ID do Vendedor a ser atualizado.
     * @param dto O DTO com o novo percentual de comissão.
     * @return A entidade Vendedor atualizada.
     * @throws EntityNotFoundException se o vendedor não for encontrado para esta
     * empresa.
     */
    public Vendedor atualizar(Long idDoVendedor, VendedorUpdateRequestDTO dto) {
        // Busca o vendedor usando o método seguro (já valida empresa e existência)
        Vendedor vendedorExistente = buscarPorId(idDoVendedor);

        // Atualiza apenas o campo permitido
        vendedorExistente.setPercentualComissao(dto.getPercentualComissao());

        // Salva a entidade atualizada (JPA faz o UPDATE)
        return vendedorRepository.save(vendedorExistente);
    }

} // Fim da classe VendedorService