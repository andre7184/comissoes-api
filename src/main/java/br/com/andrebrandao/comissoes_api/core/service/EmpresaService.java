package br.com.andrebrandao.comissoes_api.core.service;

import java.time.LocalDateTime;
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

    
    public List<Empresa> listarTodas() {
        return empresaRepository.findAll();
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
        novaEmpresa.setDataCadastro(LocalDateTime.now());
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
}