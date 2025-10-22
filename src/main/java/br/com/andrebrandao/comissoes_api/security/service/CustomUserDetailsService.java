package br.com.andrebrandao.comissoes_api.security.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import br.com.andrebrandao.comissoes_api.security.repository.UserRepository;
import lombok.RequiredArgsConstructor;

/**
 * Serviço que busca os detalhes do usuário (UserDetails) no banco de dados.
 * É a ponte entre o Spring Security e o nosso UserRepository.
 */
@Service // 1. Marca como um Serviço (Bean) do Spring
@RequiredArgsConstructor // 2. Anotação do Lombok: cria um construtor com os
                         // campos 'final'
public class CustomUserDetailsService implements UserDetailsService { // 3. Implementa a
                                                                    // interface do
                                                                    // Spring

    // 4. Injeta o nosso repositório de usuários (JPA)
    private final UserRepository userRepository;

    /**
     * Este é o único método que o Spring Security nos obriga a implementar.
     * Ele é chamado automaticamente pelo Spring durante o processo de autenticação.
     *
     * @param email O "username" que o usuário digitou (no nosso caso, o email).
     * @return um objeto UserDetails (a nossa classe 'User')
     * @throws UsernameNotFoundException se o usuário não for encontrado no banco.
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        // 5. Usa o método customizado que criamos no UserRepository
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado com o email: " + email));
        // 6. Se o usuário for encontrado, ele o retorna.
        // Se não, lança uma exceção (que o Spring Security vai tratar como "Login
        // inválido").
    }

}