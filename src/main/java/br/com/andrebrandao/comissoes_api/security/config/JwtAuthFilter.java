package br.com.andrebrandao.comissoes_api.security.config;

import java.io.IOException;

import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import br.com.andrebrandao.comissoes_api.security.service.CustomUserDetailsService;
import br.com.andrebrandao.comissoes_api.security.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

/**
 * Filtro que intercepta CADA requisição para validar o Token JWT.
 * Roda apenas uma vez por requisição (OncePerRequestFilter).
 */
@Component // 1. Marca como um Componente do Spring (Bean)
@RequiredArgsConstructor // 2. Lombok: cria um construtor com os campos 'final'
public class JwtAuthFilter extends OncePerRequestFilter { // 3. Filtro especial do
                                                          // Spring

    private final JwtService jwtService; // 4. Injeta nosso serviço de JWT
    private final CustomUserDetailsService userDetailsService; // 5. Injeta nosso
                                                              // serviço de
                                                              // buscar usuário

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {

        // 1. Pega o cabeçalho "Authorization" da requisição
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String userEmail;

        // 2. Se o cabeçalho não existir ou não começar com "Bearer ",
        // não é uma requisição autenticada por token.
        // Apenas passa para o próximo filtro e sai do método.
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 3. Extrai o token (remove o "Bearer ")
        jwt = authHeader.substring(7); // 7 é o tamanho de "Bearer "

        // 4. Extrai o email de dentro do token usando o JwtService
        userEmail = jwtService.extractUsername(jwt);

        // 5. Se o email existe E o usuário ainda não está "autenticado"
        // nesta requisição...
        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            // 6. ...Carrega o usuário do banco de dados (usando o email)
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);

            // 7. ...Verifica se o token é válido (assinatura bate e não expirou)
            if (jwtService.isTokenValid(jwt, userDetails)) {

                // 8. Se for válido, "autentica" o usuário para esta requisição!
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null, // Não precisamos de credenciais (senha) aqui,
                              // já validamos pelo token
                        userDetails.getAuthorities());

                authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request));

                // 9. Informa ao Spring Security que este usuário está
                // autenticado
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        // 10. Passa a requisição (agora autenticada) para o próximo filtro
        filterChain.doFilter(request, response);
    }
}