package br.com.andrebrandao.comissoes_api.conta.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.andrebrandao.comissoes_api.conta.dto.AlterarSenhaRequestDTO;
import br.com.andrebrandao.comissoes_api.security.model.User; // Import do User
import br.com.andrebrandao.comissoes_api.security.repository.UserRepository; // Import do Repo
import br.com.andrebrandao.comissoes_api.security.service.TenantService; // Import do TenantService
import lombok.RequiredArgsConstructor;

/**
 * Serviço com a lógica de negócio para operações da conta do usuário logado
 * (qualquer que seja o Role).
 */
@Service
@RequiredArgsConstructor
public class ContaService {

    // --- Dependências Injetadas ---
    private final TenantService tenantService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder; // O BCrypt que definimos no SecurityConfig

    /**
     * Altera a senha do usuário atualmente logado,
     * após verificar sua senha atual.
     *
     * @param dto O DTO contendo a senha atual e a nova senha.
     * @throws IllegalStateException se a senha atual estiver incorreta.
     */
    @Transactional // Garante que a operação de salvar seja atômica
    public void alterarMinhaSenha(AlterarSenhaRequestDTO dto) {

        // 1. Pega o objeto User completo do usuário que fez a requisição (pelo Token)
        User usuarioLogado = tenantService.getUsuarioLogado();

        // 2. Verifica se a "senhaAtual" enviada no DTO bate com a
        //    senha criptografada que está no banco.
        if (!passwordEncoder.matches(dto.getSenhaAtual(), usuarioLogado.getSenha())) {
            
            // 3. Se não bater, lança uma exceção. O Spring vai capturar isso
            //    e retornar um erro 4xx ou 500 (vamos tratar depois no controller).
            throw new IllegalStateException("A senha atual está incorreta.");
        }

        // 4. Se a senha atual bateu, criptografa a "novaSenha"
        String novaSenhaCriptografada = passwordEncoder.encode(dto.getNovaSenha());

        // 5. Atualiza o campo 'senha' no objeto do usuário
        usuarioLogado.setSenha(novaSenhaCriptografada);

        // 6. Salva o usuário atualizado no banco de dados.
        userRepository.save(usuarioLogado);
    }
}