package br.com.andrebrandao.comissoes_api.config.startup;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import br.com.andrebrandao.comissoes_api.core.model.Empresa;
import br.com.andrebrandao.comissoes_api.core.repository.EmpresaRepository;
import br.com.andrebrandao.comissoes_api.security.model.Role;
import br.com.andrebrandao.comissoes_api.security.model.User;
import br.com.andrebrandao.comissoes_api.security.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import java.time.LocalDateTime;

/**
 * Esta classe é um "Bean" do Spring que implementa CommandLineRunner.
 * Isso significa que o método "run()" será executado AUTOMATICAMENTE
 * uma única vez, logo após a aplicação Spring Boot iniciar.
 * * Vamos usá-la para "semear" (seed) nosso usuário Super Admin no banco.
 */
@Component // 1. Registra esta classe como um "Bean" gerenciado pelo Spring
@RequiredArgsConstructor // 2. Lombok para injetar as dependências
public class AdminUserInitializer implements CommandLineRunner {

    // 3. Injeta os repositórios que precisamos
    private final UserRepository userRepository;
    private final EmpresaRepository empresaRepository;
    private final PasswordEncoder passwordEncoder; // 4. Injeta o BCrypt que
                                                   // definimos no SecurityConfig

    @Override
    public void run(String... args) throws Exception {
        
        // 5. Define o email do nosso admin
        String adminEmail = "admin@comissoes.com";

        // 6. Verifica se o usuário JÁ EXISTE no banco
        if (userRepository.findByEmail(adminEmail).isEmpty()) {
            
            System.out.println(">>> Usuário Super Admin não encontrado. Criando novo usuário...");

            // 7. Se não existe, primeiro cria a "Empresa-Mãe" (nossa empresa)
            Empresa adminEmpresa = new Empresa();
            adminEmpresa.setNomeFantasia("Brandão SaaS"); // Mude para o nome da
                                                          // sua empresa
            adminEmpresa.setCnpj("00.000.000/0001-00"); // CNPJ fictício
            adminEmpresa.setDataCadastro(LocalDateTime.now());
            
            // Salva a empresa no banco
            empresaRepository.save(adminEmpresa);

            // 8. Agora, cria o objeto User para o Super Admin
            User superAdmin = User.builder()
                    .nome("Super Administrador")
                    .email(adminEmail)
                    .senha(passwordEncoder.encode("admin123")) // 9. CRIPTOGRAFA A
                                                               // SENHA!
                    .role(Role.ROLE_SUPER_ADMIN) // 10. Define o papel
                    .empresa(adminEmpresa) // 11. Associa com a Empresa-Mãe
                    .build();
            
            // 12. Salva o usuário no banco
            userRepository.save(superAdmin);

            System.out.println(">>> Usuário SUPER_ADMIN criado com sucesso! <<<");
            System.out.println("    Email: " + adminEmail);
            System.out.println("    Senha: admin123 (mude esta senha em produção!)");
        } else {
            System.out.println(">>> Usuário Super Admin já existe. Nenhum usuário foi criado. <<<");
        }
    }
}