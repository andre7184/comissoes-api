package br.com.andrebrandao.comissoes_api.security.service;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import br.com.andrebrandao.comissoes_api.security.model.User;

/**
 * Serviço auxiliar (helper) focado em Multi-Tenancy.
 * Sua única responsabilidade é identificar qual a empresa (Tenant)
 * do usuário que está logado na requisição atual.
 */
@Service
public class TenantService {

    /**
     * Busca o ID da Empresa (Tenant ID) do usuário atualmente autenticado.
     *
     * @return o 'Long' ID da Empresa do usuário logado.
     * @throws IllegalStateException se nenhum usuário estiver autenticado
     * (o que não deve acontecer em endpoints
     * protegidos).
     */
    public Long getEmpresaIdDoUsuarioLogado() {
        // 1. Pega o objeto de autenticação do contexto de segurança
        //    (O JwtAuthFilter que colocou ele aqui!)
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("Nenhum usuário autenticado encontrado.");
        }

        // 2. Pega o "Principal" (que é o nosso objeto 'User' que implementa
        // UserDetails)
        Object principal = authentication.getPrincipal();

        if (principal instanceof User) {
            // 3. Converte (cast) para User e retorna o ID da empresa
            return ((User) principal).getEmpresa().getId();
        }

        // 4. Se o principal não for um objeto 'User' (ex: um usuário anônimo),
        // lança um erro.
        throw new IllegalStateException("O principal da autenticação não é uma instância de 'User'.");
    }

    /**
     * Retorna o objeto User completo do usuário logado.
     * Útil se você precisar de mais do que apenas o ID (ex: o nome).
     *
     * @return O objeto 'User' logado.
     */
    public User getUsuarioLogado() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("Nenhum usuário autenticado encontrado.");
        }

        if (authentication.getPrincipal() instanceof User) {
            return (User) authentication.getPrincipal();
        }

        throw new IllegalStateException("O principal da autenticação não é uma instância de 'User'.");
    }
}