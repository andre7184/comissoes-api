package br.com.andrebrandao.comissoes_api.core.model;

public enum ModuloStatus {
    /**
     * O módulo foi cadastrado pelo Super Admin, mas ainda está sendo codificado.
     * Não pode ser vendido ou ativado para clientes.
     */
    EM_DESENVOLVIMENTO,
    
    /**
     * O módulo está funcional e sendo testado internamente pela equipe de admin.
     * Não pode ser vendido ainda.
     */
    EM_TESTE,
    
    /**
     * Módulo testado, estável e pronto para ser ativado para clientes pagantes.
     * Aparece na lista de "módulos disponíveis para venda" do Super Admin.
     */
    PRONTO_PARA_PRODUCAO,
    
    /**
     * Módulo antigo que não é mais vendido para novos clientes,
     * mas pode continuar ativo para clientes legados.
     */
    ARQUIVADO
}
