package br.com.andrebrandao.comissoes_api.security.config;

// Importações desnecessárias (AuthenticationProvider, DaoAuthenticationProvider) foram removidas
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

// A injeção do CustomUserDetailsService não é mais necessária aqui
// O Spring vai encontrá-lo sozinho
import lombok.RequiredArgsConstructor;

/**
 * Classe principal de configuração do Spring Security.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    // Agora só precisamos injetar o nosso filtro
    private final JwtAuthFilter jwtAuthFilter;

    /**
     * O "filtro" principal que define as regras de segurança HTTP.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/api/auth/**",
                                "/v3/api-docs/**",
                                "/swagger-ui/**")
                        .permitAll()
                        .anyRequest().authenticated())
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // 1. LINHA REMOVIDA: A linha .authenticationProvider(...)
                //    foi removida daqui.
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // 2. MÉTODO REMOVIDO: O bean @Bean public AuthenticationProvider...
    //    foi completamente removido.

    /**
     * O Bean que o Spring usará para CRIPTOGRAFAR as senhas.
     * O Spring vai encontrá-lo e usá-lo automaticamente.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * O Gerenciador de Autenticação. Ele será configurado automaticamente
     * pelo Spring para usar nosso CustomUserDetailsService e PasswordEncoder.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}