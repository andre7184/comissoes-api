package br.com.andrebrandao.comissoes_api.produtos.comissoes.model;

/**
 * Define os status possíveis de uma Venda no sistema.
 */
public enum VendaStatus {

    /**
     * A Venda foi lançada (provavelmente pelo Vendedor), mas ainda não foi
     * revisada ou aprovada pelo Admin da empresa.
     * Não deve entrar no cálculo de comissão ainda.
     */
    PENDENTE,

    /**
     * A Venda foi lançada pelo Admin ou foi lançada pelo Vendedor e
     * subsequentemente aprovada pelo Admin.
     * Esta é a única Venda que deve contar para o cálculo de comissões.
     */
    CONFIRMADA,

    /**
     * A Venda foi revisada pelo Admin e considerada inválida (ex: duplicada,
     * cliente desistiu).
     * Não deve entrar no cálculo de comissão.
     */
    CANCELADA
}