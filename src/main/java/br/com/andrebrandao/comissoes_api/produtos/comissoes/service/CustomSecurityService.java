package br.com.andrebrandao.comissoes_api.produtos.comissoes.service; // Verifique se este é o package correto

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // Importante

import br.com.andrebrandao.comissoes_api.core.model.Empresa;
import br.com.andrebrandao.comissoes_api.core.model.Modulo;
import br.com.andrebrandao.comissoes_api.security.model.User; // Seu UserDetails

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;

import java.util.Set; // Import necessário para Set

@Service("customSecurityService") // Define o nome do Bean para usar no @PreAuthorize
@RequiredArgsConstructor
public class CustomSecurityService {

    @PersistenceContext
    private EntityManager entityManager; // Para re-anexar entidades se necessário

    /**
     * Verifica se o usuário autenticado pertence a uma empresa que possui um módulo específico ativo.
     * Executado dentro de uma transação para permitir o carregamento LAZY dos módulos.
     *
     * @param authentication O objeto Authentication do Spring Security (contém o User logado).
     * @param nomeChaveModulo A 'chave' (String) do módulo a ser verificado (ex: "COMISSAO_CORE").
     * @return true se o usuário tem o módulo, false caso contrário.
     */
    @Transactional(readOnly = true) // Abre uma transação apenas para leitura
    public boolean hasModulo(Authentication authentication, String nomeChaveModulo) {
        // Verifica se a autenticação é válida e se o principal é do tipo User
        if (authentication == null || !(authentication.getPrincipal() instanceof User)) {
            return false;
        }

        User user = (User) authentication.getPrincipal();
        
        // Garante que o User está anexado à sessão atual do Hibernate.
        // Isso é crucial se o User foi carregado em outra transação (ex: no login).
        User managedUser = entityManager.merge(user); 
        
        Empresa empresa = managedUser.getEmpresa();
        
        // Verifica se o usuário tem uma empresa associada
        if (empresa == null) {
            return false;
        }
        
        // Carrega explicitamente a coleção de módulos dentro da transação.
        // Isso resolve a LazyInitializationException.
        // Usamos Hibernate.initialize ou apenas acessamos a coleção.
        Set<Modulo> modulosAtivos = empresa.getModulosAtivos(); 
        
        if (modulosAtivos == null || modulosAtivos.isEmpty()) {
            return false;
        }

        // Itera sobre os módulos ativos (agora carregados) para encontrar a chave
        for (Modulo modulo : modulosAtivos) {
            if (nomeChaveModulo.equals(modulo.getChave())) {
                return true; // Encontrou o módulo!
            }
        }
        
        return false; // Não encontrou o módulo
    }
}