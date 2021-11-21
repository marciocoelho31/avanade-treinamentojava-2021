package com.avanade.aplicacao.dao;

import com.avanade.aplicacao.model.ClienteModel;
import com.avanade.aplicacao.model.PedidoModel;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.sql.*;
import java.util.Date;
import java.util.Optional;
import java.util.Properties;

@Slf4j
public class PedidoDao {

    private Connection connection;
    private final ItemPedidoDao itemPedidoDao;

    public PedidoDao() throws SQLException {
        connection = criarConexao();
        itemPedidoDao = new ItemPedidoDao();
        log.info("Conexão executada com sucesso");
    }

    private Connection criarConexao() throws SQLException {
        String url = "jdbc:postgresql://localhost/treinamentojava";
        Properties props = new Properties();
        props.setProperty("user","postgres");
        props.setProperty("password","postgres");
        return DriverManager.getConnection(url, props);
    }

    public PedidoModel inserir(PedidoModel pedido) throws SQLException {

        Optional<PedidoModel> pedidoQry = buscaPorCodigo(pedido.getCodigo());
        if (!pedidoQry.isEmpty()) {
            atualizar(pedido);
            return pedido;
        }

        StringBuilder sb = new StringBuilder();
        sb.append(" insert into Pedidos ");
        sb.append("      ( codigo ");
        sb.append("      , codigo_cliente ");
        sb.append("      , valor_total ");
        sb.append("      , numero_cartao ");
        sb.append("      , data ) ");
        sb.append(" values ");
        sb.append("      ( ? ");
        sb.append("      , ? ");
        sb.append("      , ? ");
        sb.append("      , ? ");
        sb.append("      , ? ) ");

        int idx = 1;
        PreparedStatement pst = connection.prepareStatement(sb.toString());
        pst.setInt(idx++, pedido.getCodigo());
        pst.setInt(idx++, pedido.getCliente().getCodigoCliente());
        pst.setBigDecimal(idx++, pedido.getValorTotal());
        pst.setString(idx++, pedido.getNumeroCartao());
        pst.setDate(idx, new java.sql.Date(pedido.getData().getTime()));

        int qtdLinhas = pst.executeUpdate();
        if (qtdLinhas == 0) {
            throw new SQLException("Nenhum registro foi inserido para o pedido []");
        }

        pedido.getItens().forEach((itemPedido -> {
            try {
                itemPedidoDao.inserir(itemPedido);
            } catch (SQLException ex) {
                log.error("Falha ao inserir item do pedido no banco [{}]", itemPedido, ex);
                // TODO Alimentar lista/log de erro
            }
        }));

        return pedido;
    }

    public PedidoModel atualizar(PedidoModel pedido) throws SQLException {

        StringBuilder sb = new StringBuilder();
        sb.append(" update Pedidos set ");
        sb.append("   codigo_cliente = ? ");
        sb.append(" , valor_total = ? ");
        sb.append(" , numero_cartao = ? ");
        sb.append(" , data = ? ");
        sb.append(" where codigo = ? ");

        int idx = 1;
        PreparedStatement pst = connection.prepareStatement(sb.toString());
        pst.setInt(idx++, pedido.getCliente().getCodigoCliente());
        pst.setBigDecimal(idx++, pedido.getValorTotal());
        pst.setString(idx++, pedido.getNumeroCartao());
        pst.setDate(idx++, new java.sql.Date(pedido.getData().getTime()));
        pst.setInt(idx, pedido.getCodigo());

        int qtdLinhas = pst.executeUpdate();
        if (qtdLinhas == 0) {
            throw new SQLException("Nenhum registro foi atualizado para o pedido []");
        }

        return pedido;
    }

    public Optional<PedidoModel> buscaPorCodigo(Integer codigoQry) throws SQLException {

        StringBuilder sb = new StringBuilder();
        sb.append(" select codigo ");
        sb.append("      , codigo_cliente ");
        sb.append("      , valor_total ");
        sb.append("      , numero_cartao ");
        sb.append("      , data ");
        sb.append(" from pedidos ");
        sb.append(" where codigo = ?");

        // TODO criar tabela clientes e relacionar com o pedido, e fazer um JOIN acima pra trazer o cliente

        int idx = 1;
        PreparedStatement pst = connection.prepareStatement(sb.toString());
        pst.setInt(idx, codigoQry);
        // pode usar com ++ para indicar outros parametros

        Statement st = connection.createStatement();
        ResultSet rs = pst.executeQuery();

        if (!rs.next()) {
            return Optional.empty();
        }

        idx = 1;
        Integer codigo = rs.getInt(idx++);
        Integer codigoCliente = rs.getInt(idx++);
        BigDecimal valorTotal = rs.getBigDecimal(idx++);
        String numeroCartao = rs.getString(idx++);
        Date data = rs.getDate(idx);

        ClienteModel cliente = ClienteModel.builder()
                .codigoCliente(codigoCliente)
                .build();

        PedidoModel pedido = PedidoModel.builder()
                .codigo(codigo)
                .cliente(cliente)
                .valorTotal(valorTotal)
                .numeroCartao(numeroCartao)
                .data(data)
                .build();

        return Optional.of(pedido);
    }

}
