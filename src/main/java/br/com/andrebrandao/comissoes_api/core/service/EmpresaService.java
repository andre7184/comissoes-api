package br.com.andrebrandao.comissoes_api.core.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

// --- IMPORTS ADICIONADOS ---
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // Import mais importante!

import br.com.andrebrandao.comissoes_api.core.dto.EmpresaRequestDTO;
import br.com.andrebrandao.comissoes_api.core.dto.EmpresaUpdateRequestDTO;
import br.com.andrebrandao.comissoes_api.core.model.Empresa;
import br.com.andrebrandao.comissoes_api.core.model.Modulo;
import br.com.andrebrandao.comissoes_api.core.repository.EmpresaRepository;
import br.com.andrebrandao.comissoes_api.core.repository.ModuloRepository;
import br.com.andrebrandao.comissoes_api.security.model.Role; // Import de Segurança
import br.com.andrebrandao.comissoes_api.security.model.User; // Import de Segurança
import br.com.andrebrandao.comissoes_api.security.repository.UserRepository; // Import de Segurança
import br.com.andrebrandao.comissoes_api.security.service.TenantService;
import br.com.andrebrandao.comissoes_api.core.dto.AdminUsuarioRequestDTO;
import br.com.andrebrandao.comissoes_api.core.dto.EmpresaDetalhesDTO;
import br.com.andrebrandao.comissoes_api.core.dto.EmpresaComAdminsDTO;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmpresaService {

    private final EmpresaRepository empresaRepository;
    private final ModuloRepository moduloRepository;
    
    // --- DEPENDÊNCIAS ADICIONADAS ---
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private final TenantService tenantService;
    
    /**
     * Lista todas as Empresas cadastradas, incluindo seus módulos ativos e
     * a lista de usuários ROLE_ADMIN associados.
     * Garante o carregamento dos dados LAZY (módulos).
     *
     * @return Lista de DTOs EmpresaComAdminsDTO.
     */
    @Transactional(readOnly = true) // <-- Adicionar @Transactional para gerenciar a sessão e LAZY loading
    public List<EmpresaComAdminsDTO> listarTodas() { // <-- TIPO DE RETORNO ALTERADO
        // 1. Busca todas as entidades Empresa
        List<Empresa> empresas = empresaRepository.findAll();
        List<EmpresaComAdminsDTO> dtos = new ArrayList<>();

        // 2. Itera sobre cada empresa para buscar seus admins e carregar módulos
        for (Empresa empresa : empresas) {
            // 3. Busca a lista de usuários ROLE_ADMIN para esta empresa
            List<User> admins = userRepository.findByEmpresaIdAndRole(empresa.getId(), Role.ROLE_ADMIN);

            // 4. Acessa a coleção LAZY de módulos DENTRO da transação para carregá-la
            // O próprio DTO já faz isso no método fromEntity, mas podemos garantir aqui também (opcional)
            // empresa.getModulosAtivos().size(); // Força o carregamento

            // 5. Constrói o DTO usando o método de fábrica
            EmpresaComAdminsDTO dto = EmpresaComAdminsDTO.fromEntity(empresa, admins);
            dtos.add(dto);
        }

        // 6. Retorna a lista de DTOs
        return dtos;
    }

    
    public Empresa buscarPorId(Long id) {
        return empresaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Empresa não encontrada com o ID: " + id));
    }

    
    // --- MÉTODO 'CRIAR' TOTALMENTE REESCRITO ---
    /**
     * Cria uma nova Empresa e seu primeiro usuário Administrador (Onboarding).
     * Associa automaticamente os módulos marcados como "Padrão".
     * Esta é uma operação transacional: ou tudo dá certo, ou nada é salvo.
     *
     * @param dto O DTO com os dados da empresa e do seu admin.
     * @return A entidade Empresa que foi salva.
     */
    @Transactional // 1. Garante que se o 'save do User' falhar, o 'save da Empresa'
                   // é desfeito (rollback).
    public Empresa criar(EmpresaRequestDTO dto) {
        
        // --- PASSO 1: Criar a Empresa ---
        Empresa novaEmpresa = new Empresa();
        novaEmpresa.setNomeFantasia(dto.getNomeFantasia());
        novaEmpresa.setCnpj(dto.getCnpj());
        novaEmpresa.setRazaoSocial(dto.getRazaoSocial());
        // Salva a empresa primeiro para obter um ID
        Empresa empresaSalva = empresaRepository.save(novaEmpresa);

        // --- PASSO 2: Associar Módulos Padrão (se houver) ---
        List<Modulo> modulosPadrao = moduloRepository.findByIsPadrao(true);
        if (modulosPadrao != null && !modulosPadrao.isEmpty()) {
            empresaSalva.setModulosAtivos(new HashSet<>(modulosPadrao));
            empresaSalva = empresaRepository.save(empresaSalva); // Salva a associação
        }

        // --- PASSO 3: Criar o Usuário Admin da Empresa ---
        // (Validação se o email já existe pode ser adicionada aqui)
        User adminCliente = User.builder()
                .nome(dto.getAdminNome())
                .email(dto.getAdminEmail())
                .senha(passwordEncoder.encode(dto.getAdminSenha())) // Criptografa a senha
                .role(Role.ROLE_ADMIN) // Define o papel de Admin do Cliente
                .empresa(empresaSalva) // Associa o usuário à empresa que acabamos
                                       // de salvar
                .build();
        
        userRepository.save(adminCliente);

        // 4. Retorna a empresa criada
        return empresaSalva;
    }

    
    public Empresa atualizar(Long id, EmpresaUpdateRequestDTO dto) {
        Empresa empresaExistente = buscarPorId(id);
        empresaExistente.setNomeFantasia(dto.getNomeFantasia());
        empresaExistente.setCnpj(dto.getCnpj());
        return empresaRepository.save(empresaExistente);
    }

    
    public Empresa atualizarModulosAtivos(Long empresaId, Set<Long> moduloIds) {
        Empresa empresa = buscarPorId(empresaId);
        Set<Modulo> novosModulos = new HashSet<>();

        if (moduloIds != null && !moduloIds.isEmpty()) {
            novosModulos = moduloRepository.findAllByIdIn(moduloIds);
            if (novosModulos.size() != moduloIds.size()) {
                throw new EntityNotFoundException("Um ou mais IDs de Módulo não foram encontrados.");
            }
        }
        
        empresa.setModulosAtivos(novosModulos);
        return empresaRepository.save(empresa);
    }

/**
     * Busca os detalhes da Empresa à qual o usuário ADMIN logado pertence,
     * incluindo a contagem total de usuários ADMIN associados a essa empresa. // <-- DESCRIÇÃO ATUALIZADA
     *
     * @return O DTO EmpresaDetalhesDTO preenchido.
     * @throws EntityNotFoundException se a empresa do usuário logado não for encontrada.
     */
    @Transactional(readOnly = true)
    public EmpresaDetalhesDTO buscarDetalhesEmpresaLogada() {
        // 1. Pega o ID da Empresa do usuário ADMIN logado
        Long empresaId = tenantService.getEmpresaIdDoUsuarioLogado();
        if (empresaId == null) {
            throw new IllegalStateException("Usuário logado não está associado a uma empresa.");
        }

        // 2. Busca a entidade Empresa
        Empresa empresa = empresaRepository.findById(empresaId)
                .orElseThrow(() -> new EntityNotFoundException("Empresa não encontrada com o ID: " + empresaId));

        // 3. Usa o UserRepository para contar APENAS os usuários ADMIN da empresa
        Long qtdAdmins = userRepository.countByEmpresaIdAndRole(empresaId, Role.ROLE_ADMIN); // <-- CHAMADA MODIFICADA

        // 4. Mapeia para o DTO de resposta (o nome do parâmetro deve coincidir com o do DTO)
        return EmpresaDetalhesDTO.fromEntity(empresa, qtdAdmins); // <-- PASSANDO A CONTAGEM DE ADMINS
    }

    /**
     * Cria um novo usuário com ROLE_ADMIN para uma empresa existente.
     * Executado pelo Super Admin.
     *
     * @param empresaId O ID da empresa à qual o novo admin pertencerá.
     * @param dto Os dados do novo admin (nome, email, senha).
     * @return A entidade User do admin criado.
     * @throws EntityNotFoundException se a empresa não for encontrada.
     * @throws IllegalStateException se o email já estiver em uso.
     */
    @Transactional // Garante atomicidade
    public User criarAdminParaEmpresa(Long empresaId, AdminUsuarioRequestDTO dto) {
        // 1. Busca a empresa para garantir que ela existe
        Empresa empresa = buscarPorId(empresaId); // Reutiliza o método buscarPorId

        // 2. Verifica se o email já está em uso
        if (userRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new IllegalStateException("O email informado já está em uso: " + dto.getEmail());
        }

        // 3. Cria o novo usuário Admin
        User novoAdmin = User.builder()
                .nome(dto.getNome())
                .email(dto.getEmail())
                .senha(passwordEncoder.encode(dto.getSenha())) // Criptografa a senha
                .role(Role.ROLE_ADMIN) // Define o papel
                .empresa(empresa) // Associa à empresa encontrada
                // dataCriacao será preenchida automaticamente
                .build();

        // 4. Salva e retorna o usuário criado
        return userRepository.save(novoAdmin);
    }

    /**
     * Cria um novo usuário com ROLE_ADMIN para a EMPRESA DO ADMIN LOGADO.
     * Executado pelo Admin da própria empresa.
     *
     * @param dto Os dados do novo admin (nome, email, senha).
     * @return A entidade User do admin criado.
     * @throws EntityNotFoundException se a empresa do admin logado não for encontrada (improvável).
     * @throws IllegalStateException se o email já estiver em uso ou se o usuário logado não pertencer a uma empresa.
     */
    @Transactional
    public User criarAdminParaMinhaEmpresa(AdminUsuarioRequestDTO dto) {
        // 1. Pega o ID da Empresa do ADMIN logado
        Long empresaId = tenantService.getEmpresaIdDoUsuarioLogado();
        if (empresaId == null) {
            throw new IllegalStateException("Usuário logado não está associado a uma empresa válida para criar admins.");
        }

        // 2. Busca a referência da empresa (não precisa carregar tudo, só a referência para associação)
        Empresa empresa = empresaRepository.getReferenceById(empresaId);

        // 3. Verifica se o email já está em uso
        if (userRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new IllegalStateException("O email informado já está em uso: " + dto.getEmail());
        }

        // 4. Cria o novo usuário Admin
        User novoAdmin = User.builder()
                .nome(dto.getNome())
                .email(dto.getEmail())
                .senha(passwordEncoder.encode(dto.getSenha()))
                .role(Role.ROLE_ADMIN)
                .empresa(empresa) // Associa à empresa do admin logado
                .build();

        // 5. Salva e retorna o usuário criado
        return userRepository.save(novoAdmin);
    }
}