package br.com.andrebrandao.comissoes_api.security.service;

import java.util.stream.Collectors;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import br.com.andrebrandao.comissoes_api.core.model.Modulo;
import br.com.andrebrandao.comissoes_api.security.dto.LoginRequest;
import br.com.andrebrandao.comissoes_api.security.dto.LoginResponse;
import br.com.andrebrandao.comissoes_api.security.model.User;
import br.com.andrebrandao.comissoes_api.security.repository.UserRepository;
import lombok.RequiredArgsConstructor;

/**
 * Serviço que orquestra a lógica de autenticação.
 */
@Service
@RequiredArgsConstructor
public class AutenticacaoService {

    // 1. Injeta o gerenciador do Spring que sabe validar senhas
    private final AuthenticationManager authenticationManager;

    // 2. Injeta nosso repositório de usuário para buscar dados completos
    private final UserRepository userRepository;

    // 3. Injeta nosso serviço que sabe criar o token
    private final JwtService jwtService;

    /**
     * Método principal que executa o login.
     *
     * @param request O DTO com email e senha.
     * @return o DTO de Resposta com o Token e as Permissões.
     */
    public LoginResponse login(LoginRequest request) {
        
        // 4. Dispara o processo de autenticação do Spring Security.
        // Ele vai:
        // a. Chamar nosso CustomUserDetailsService para buscar o usuário pelo email
        // b. Chamar nosso PasswordEncoder para comparar a senha do DTO com a senha
        // criptografada do banco.
        // c. Se a senha ou usuário estiverem errados, ele lança uma exceção aqui.
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getSenha()));

        // 5. Se chegou até aqui, o usuário e senha estão CORRETOS.
        // Agora, buscamos o usuário no banco *novamente* para ter o objeto
        // completo,
        // incluindo a Empresa e os Módulos.
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado após autenticação"));

        // 6. Geramos o token JWT para este usuário.
        // O JwtService vai extrair o nome e role de dentro do objeto 'user'.
        String jwtToken = jwtService.generateToken(user);

        // 7. Esta é a lógica "multi-tenant" crucial.
        // Pegamos a lista de Módulos Ativos da Empresa do usuário
        // e transformamos em uma lista de Strings (as "chaves").
        var permissoes = user.getEmpresa().getModulosAtivos()
                .stream()
                .map(Modulo::getChave) // Pega a string "chave" de cada objeto Modulo
                .collect(Collectors.toSet());

        // 8. Construímos e retornamos nossa Resposta.
        return LoginResponse.builder()
                .token(jwtToken)
                .permissoesModulos(permissoes)
                .build();
    }

}