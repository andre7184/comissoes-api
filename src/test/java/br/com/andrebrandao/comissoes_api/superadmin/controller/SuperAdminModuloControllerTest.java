package br.com.andrebrandao.comissoes_api.superadmin.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper; // Para converter DTO em JSON

import br.com.andrebrandao.comissoes_api.core.dto.ModuloRequestDTO;
import br.com.andrebrandao.comissoes_api.core.model.ModuloStatus;
import br.com.andrebrandao.comissoes_api.core.repository.ModuloRepository;

// Importes estáticos para os testes (Arrange, Act, Assert)
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Teste de Integração para o SuperAdminModuloController.
 * Carrega o contexto completo da aplicação e usa um banco em memória (H2).
 */
@SpringBootTest // 1. Carrega a aplicação Spring inteira
@AutoConfigureMockMvc // 2. Configura a ferramenta de simulação de HTTP (MockMvc)
class SuperAdminModuloControllerTest {

    // 3. Ferramentas injetadas pelo Spring
    @Autowired
    private MockMvc mockMvc; // A ferramenta para fazer as requisições (nosso "Insomnia")

    @Autowired
    private ObjectMapper objectMapper; // Converte objetos Java (DTO) para string JSON

    @Autowired
    private ModuloRepository moduloRepository; // Para checar o banco *depois* da
                                               // requisição

    // 4. Limpa o banco de dados ANTES de CADA teste
    @BeforeEach
    void setUp() {
        moduloRepository.deleteAll();
    }

    /**
     * Teste do cenário:
     * DADO que sou um usuário SUPER_ADMIN
     * QUANDO eu envio um POST para /api/superadmin/modulos com um DTO válido
     * ENTÃO o status da resposta deve ser 201 Created
     * E o JSON da resposta deve conter o módulo criado
     * E o módulo deve existir no banco de dados
     */
    @Test // 5. Marca este método como um teste
    @WithMockUser(roles = "SUPER_ADMIN") // 6. A "Mágica"! Finge que estamos logados
                                         // como SUPER_ADMIN.
                                         // Não precisamos de um token JWT real!
    void deveCriarModuloComSucesso_QuandoUsuarioForSuperAdmin() throws Exception {
        
        // --- 1. Arrange (Arrumar) ---
        
        // Cria o DTO que vamos enviar no "body"
        ModuloRequestDTO novoModuloDTO = new ModuloRequestDTO();
        novoModuloDTO.setNome("Módulo de Teste Automatizado");
        novoModuloDTO.setChave("TESTE_AUTO");
        novoModuloDTO.setStatus(ModuloStatus.PRONTO_PARA_PRODUCAO);
        novoModuloDTO.setPrecoMensal(java.math.BigDecimal.TEN);
        novoModuloDTO.setIsPadrao(false);

        // Converte o DTO para uma string JSON
        String dtoAsJson = objectMapper.writeValueAsString(novoModuloDTO);

        // --- 2. Act (Agir) ---
        
        // Simula a requisição POST, exatamente como o Insomnia fez
        var acao = mockMvc.perform(post("/api/superadmin/modulos") // O endpoint
                .contentType(MediaType.APPLICATION_JSON) // O "Content-Type"
                .content(dtoAsJson)); // O "Body"

        // --- 3. Assert (Verificar) ---
        
        // 3.1. Verifica a RESPOSTA HTTP
        acao.andExpect(status().isCreated()) // Esperamos um 201 Created
                .andExpect(jsonPath("$.id").exists()) // Esperamos que a resposta
                                                      // JSON tenha um ID
                .andExpect(jsonPath("$.nome").value("Módulo de Teste Automatizado"))
                .andExpect(jsonPath("$.chave").value("TESTE_AUTO"));

        // 3.2. Verifica o ESTADO DO BANCO DE DADOS
        // (O teste mais importante: realmente salvou?)
        var modulosNoBanco = moduloRepository.findAll();
        assertThat(modulosNoBanco).hasSize(1); // Esperamos que tenha 1 módulo no
                                              // banco
        assertThat(modulosNoBanco.get(0).getNome()).isEqualTo("Módulo de Teste Automatizado");
    }
}