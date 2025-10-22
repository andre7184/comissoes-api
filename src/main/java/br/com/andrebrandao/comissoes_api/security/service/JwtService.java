package br.com.andrebrandao.comissoes_api.security.service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.function.Function;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import br.com.andrebrandao.comissoes_api.security.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

/**
 * Serviço responsável por todas as operações de JSON Web Token (JWT).
 * Gerar, validar, extrair claims (dados), etc.
 */
@Service // 1. Marca esta classe como um Serviço do Spring (para podermos injetá-la
         // em outros lugares)
public class JwtService {

    // 2. Precisamos de uma "chave secreta" para assinar os tokens.
    // Vamos injetar isso do nosso application.properties.
    @Value("${application.security.jwt.secret-key}")
    private String secretKey;

    @Value("${application.security.jwt.expiration}")
    private long jwtExpiration; // Tempo em segundos (ex: 86400 para 24 horas)

    /**
     * Extrai o "username" (nosso email) de dentro de um token JWT.
     * * @param token O token JWT.
     * @return O email do usuário.
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject); // O "Subject" (Assunto) é onde
                                                        // guardamos o username.
    }

    /**
     * Função principal para gerar um novo token JWT para um usuário.
     * * @param userDetails Os detalhes do usuário (do Spring Security).
     * @return Uma string de token JWT.
     */
    public String generateToken(UserDetails userDetails) {
        return buildToken(userDetails, jwtExpiration);
    }

    /**
     * Verifica se um token é válido (não expirou e a assinatura bate).
     * * @param token       O token JWT.
     * @param userDetails Os detalhes do usuário (para comparar o username).
     * @return true se o token for válido, false caso contrário.
     */
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    // --- MÉTODOS PRIVADOS AUXILIARES ---

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private String buildToken(
            UserDetails userDetails,
            long expiration) {

        // Vamos adicionar "claims" (informações) customizadas no token
        // para que o frontend possa usá-las.
        User user = (User) userDetails; // Converte UserDetails para nosso User

        return Jwts.builder()
                .subject(userDetails.getUsername()) // 3. O "Subject" (assunto) é o
                                                  // username (nosso email)
                .claim("nome", user.getNome()) // 4. Adiciona o nome do usuário
                .claim("role", user.getRole().name()) // 5. Adiciona o Role (ex: "ROLE_ADMIN")
                .issuedAt(new Date(System.currentTimeMillis())) // Data de criação
                .expiration(Date.from(Instant.now().plus(expiration, ChronoUnit.SECONDS))) // Data
                                                                                          // de
                                                                                          // expiração
                .signWith(getSignInKey()) // 6. Assina o token com nossa chave secreta
                .compact(); // Constrói a string
    }

    /**
     * Função genérica para extrair qualquer "claim" (dado) de dentro do token.
     */
    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Lê o token e decodifica todos os "claims" usando a chave secreta.
     * Se a assinatura estiver errada ou o token expirado, falha aqui.
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSignInKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * Converte nossa string de chave secreta (do properties) em um objeto
     * SecretKey seguro.
     */
    private SecretKey getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}