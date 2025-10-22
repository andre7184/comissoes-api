package br.com.andrebrandao.comissoes_api.empresa.controller;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

// Importes dos seus models e repositórios
import br.com.andrebrandao.comissoes_api.core.model.Empresa;
import br.com.andrebrandao.comissoes_api.core.model.Modulo;
import br.com.andrebrandao.comissoes_api.core.model.ModuloStatus;
import br.com.andrebrandao.comissoes_api.core.repository.EmpresaRepository;
import br.com.andrebrandao.comissoes_api.core.repository.ModuloRepository;
import br.com.andrebrandao.comissoes_api.security.model.Role;
import br.com.andrebrandao.comissoes_api.security.model.User;
import br.com.andrebrandao.comissoes_api.security.repository.UserRepository;

// Importes estáticos para os testes
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.hamcrest.Matchers.hasSize;

/**
 * Teste de Integração para o EmpresaAdminController.
 * Carrega o contexto completo e usa o banco H2.
 */
@SpringBootTest
@AutoConfigureMockMvc
public class EmpresaAdminControllerTest {

    @Autowired
    private MockMvc mockMvc; // O "Insomnia" automatizado

    // Injeta os repositórios reais para "arrumar" (Arrange) o banco de dados
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmpresaRepository empresaRepository;

    @Autowired
    private ModuloRepository moduloRepository;

    @Autowired
    private PasswordEncoder passwordEncoder; // Para criar a senha criptografada

    private User adminDaPadaria; // O usuário que vamos usar para logar

    /**
     * Roda ANTES de cada teste. Prepara o banco H2 com dados limpos.
     */
    @BeforeEach
    void setUp() {
        // 1. Limpa tudo para garantir um teste isolado
        userRepository.deleteAll();
        empresaRepository.deleteAll();
        moduloRepository.deleteAll();

        // --- 2. Arrange (Arrumar o cenário) ---

        // Cria o Módulo de Comissões
        Modulo moduloComissoes = new Modulo();
        moduloComissoes.setNome("Módulo de Comissões");
        moduloComissoes.setChave("COMISSOES_CORE");
        moduloComissoes.setStatus(ModuloStatus.PRONTO_PARA_PRODUCAO);
        moduloComissoes.setPrecoMensal(BigDecimal.ZERO);
        moduloComissoes.setPadrao(true);
        moduloRepository.save(moduloComissoes);

        // Cria a Empresa "Padaria Teste"
        Empresa padaria = new Empresa();
        padaria.setNomeFantasia("Padaria Teste");
        padaria.setCnpj("11.222.333/0001-44");
        padaria.setDataCadastro(LocalDateTime.now());
        // Associa o módulo à empresa
        padaria.setModulosAtivos(Set.of(moduloComissoes));
        empresaRepository.save(padaria);

        // Cria o Usuário ROLE_ADMIN para a "Padaria Teste"
        this.adminDaPadaria = User.builder()
                .nome("Admin da Padaria")
                .email("admin@padariateste.com")
                .senha(passwordEncoder.encode("senha123"))
                .role(Role.ROLE_ADMIN)
                .empresa(padaria) // Associa o usuário à empresa
                .build();
        userRepository.save(this.adminDaPadaria);
    }

    // --- TESTE 1: O de Sucesso (Happy Path) ---
    @Test
    void deveListarModulosComSucesso_QuandoUsuarioForAdmin() throws Exception {
        
        // --- Act (Agir) ---
        // Simula uma requisição GET para a URL...
        var acao = mockMvc.perform(get("/api/empresa/meus-modulos")
                .contentType(MediaType.APPLICATION_JSON)
                // 1. A "MÁGICA" DO LOGIN:
                // ".with(user(this.adminDaPadaria))"
                // Isso diz ao Spring Security: "Execute esta requisição
                // como se o usuário 'adminDaPadaria' estivesse logado."
                .with(user(this.adminDaPadaria)));

        // --- Assert (Verificar) ---
        acao.andExpect(status().isOk()) // Espera um 200 OK
                .andExpect(jsonPath("$", hasSize(1))) // Espera que a resposta seja
                                                      // uma lista de 1 item
                .andExpect(jsonPath("$[0].chave").value("COMISSOES_CORE")); // Espera
                                                                           // que
                                                                           // a
                                                                           // chave
                                                                           // do
                                                                           // módulo
                                                                           // seja
                                                                           // "COMISSOES_CORE"
    }

    // --- TESTE 2: O de Segurança (Negative Path) ---
    @Test
    void deveRetornar403Forbidden_QuandoAdminTentaAcessarEndpointSuperAdmin() throws Exception {
        
        // --- Act (Agir) ---
        // Tenta acessar um endpoint de SUPER_ADMIN...
        var acao = mockMvc.perform(get("/api/superadmin/modulos")
                .contentType(MediaType.APPLICATION_JSON)
                // ...logado como um simples ADMIN (da Padaria)
                .with(user(this.adminDaPadaria)));

        // --- Assert (Verificar) ---
        acao.andExpect(status().isForbidden()); // Espera um 403 Forbidden!
    }
}
