// src/main/java/br/com/andrebrandao/comissoes_api/security/service/UsuarioService.java
package br.com.andrebrandao.comissoes_api.security.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.authentication.BadCredentialsException; // Para erro de senha atual

import br.com.andrebrandao.comissoes_api.security.dto.AlterarSenhaRequestDTO;
import br.com.andrebrandao.comissoes_api.security.model.User;
import br.com.andrebrandao.comissoes_api.security.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException; // Para usuário não encontrado
import lombok.RequiredArgsConstructor;

/**
 * Serviço para operações relacionadas a Usuários (ex: alteração de senha).
 */
@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TenantService tenantService; // Para buscar o usuário logado

    /**
     * Altera a senha do usuário atualmente logado.
     * Verifica se a senha atual fornecida corresponde à senha armazenada antes de atualizar.
     *
     * @param dto O DTO contendo a senha atual e a nova senha.
     * @throws EntityNotFoundException se o usuário logado não for encontrado (improvável).
     * @throws BadCredentialsException se a senha atual fornecida estiver incorreta.
     */
    @Transactional // Garante atomicidade
    public void alterarSenhaUsuarioLogado(AlterarSenhaRequestDTO dto) {
        // 1. Busca o usuário logado
        User usuarioLogado = tenantService.getUsuarioLogado();
        if (usuarioLogado == null) {
            // Isso só aconteceria se o filtro de segurança falhasse
            throw new EntityNotFoundException("Usuário logado não encontrado.");
        }

        // 2. Verifica se a "senhaAtual" fornecida bate com a senha no banco
        if (!passwordEncoder.matches(dto.getSenhaAtual(), usuarioLogado.getPassword())) {
            throw new BadCredentialsException("A senha atual fornecida está incorreta.");
        }

        // 3. Criptografa a nova senha
        String novaSenhaCriptografada = passwordEncoder.encode(dto.getNovaSenha());

        // 4. Atualiza a senha no objeto do usuário
        usuarioLogado.setSenha(novaSenhaCriptografada);

        // 5. Salva o usuário com a nova senha
        // Como o método é @Transactional, o Hibernate detecta a mudança e faz o UPDATE
        userRepository.save(usuarioLogado);

        // Não retorna nada, sucesso implica status 200 OK ou 204 No Content no Controller
    }
}