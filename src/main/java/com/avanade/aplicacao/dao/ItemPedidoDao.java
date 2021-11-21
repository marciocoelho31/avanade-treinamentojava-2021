package com.avanade.aplicacao.dao;

import com.avanade.aplicacao.model.ItemPedidoModel;
import lombok.extern.slf4j.Slf4j;

import java.sql.*;
import java.util.Properties;

@Slf4j
public class ItemPedidoDao {

    private Connection connection;

    public ItemPedidoDao() throws SQLException {
        connection = criarConexao();
        log.info("Conexão executada com sucesso");
    }

    private Connection criarConexao() throws SQLException {
        String url = "jdbc:postgresql://localhost/treinamentojava";
        Properties props = new Properties();
        props.setProperty("user","postgres");
        props.setProperty("password","postgres");
        return DriverManager.getConnection(url, props);
    }

    public ItemPedidoModel inserir(ItemPedidoModel itemPedido) throws SQLException {

        StringBuilder sb = new StringBuilder();
        sb.append(" insert into ItensPedidos ");
        sb.append("      ( codigo ");
        sb.append("      , codigo_produto ");
        sb.append("      , quantidade ");
        sb.append("      , nome_produto ");
        sb.append("      , valor_unitario ");
        sb.append("      , valor ) ");
        sb.append(" values ");
        sb.append("      ( ? ");
        sb.append("      , ? ");
        sb.append("      , ? ");
        sb.append("      , ? ");
        sb.append("      , ? ");
        sb.append("      , ? ) ");

        int idx = 1;
        PreparedStatement pst = connection.prepareStatement(sb.toString());
        pst.setInt(idx++, itemPedido.getCodigo());
        pst.setInt(idx++, itemPedido.getCodigoProduto());
        pst.setBigDecimal(idx++, itemPedido.getQuantidade());
        pst.setString(idx++, itemPedido.getNomeProduto());
        pst.setBigDecimal(idx++, itemPedido.getValorUnitario());
        pst.setBigDecimal(idx++, itemPedido.getValor());

        int qtdLinhas = pst.executeUpdate();
        if (qtdLinhas == 0) {
            throw new SQLException("Nenhum registro foi inserido para o item do pedido []");
        }

        return itemPedido;
    }

}
