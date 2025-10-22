package br.com.andrebrandao.comissoes_api.security.model;
/**
 * Define os papéis (permissões) que um usuário pode ter dentro do sistema.
 * O Spring Security usa isso para controlar o acesso.
 */
public enum Role {
    
    /**
     * O "Super Administrador" (você). Tem acesso a tudo,
     * incluindo o painel de Super Admin.
     */
    ROLE_SUPER_ADMIN,
    
    /**
     * O administrador da empresa-cliente (seu cliente).
     * Pode cadastrar vendedores, lançar vendas, ver relatórios.
     */
    ROLE_ADMIN,
    
    /**
     * O vendedor (funcionário da empresa-cliente).
     * Só pode acessar o "Portal do Vendedor" para ver suas próprias vendas/metas.
     */
    ROLE_VENDEDOR
}
