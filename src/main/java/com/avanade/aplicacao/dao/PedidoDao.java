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

    public PedidoDao() throws SQLException {
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

    public PedidoModel inserir(PedidoModel pedido) throws SQLException {

        StringBuilder sb = new StringBuilder();
        sb.append(" insert into pedidos ");
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
        pst.setDate(idx, (java.sql.Date) pedido.getData());

        // TODO se o pedido ja existir, chamar o metodo 'atualizar' abaixo
        // if antes consultando pelo codigo

        int qtdLinhas = pst.executeUpdate();
        if (qtdLinhas == 0) {
            throw new SQLException("Nenhum registro foi inserido para o pedido []");
        }

        return pedido;

    }

    public PedidoModel atualizar(PedidoModel pedido) {
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
