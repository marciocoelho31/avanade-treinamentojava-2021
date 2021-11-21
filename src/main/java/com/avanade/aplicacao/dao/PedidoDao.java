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
    private final LogDao logDao;

    public PedidoDao() throws SQLException {
        connection = criarConexao();
        itemPedidoDao = new ItemPedidoDao();
        logDao = new LogDao();
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
        if (pedidoQry.isPresent()) {
            atualizar(pedido);

            String mensagem = "Tentativa de inclusão do pedido " + pedido.getCodigo() + " já existente, dados atualizados";
            logDao.inserir(mensagem);

            return pedido;
        }

        String sb = " insert into Pedidos " +
                "      ( codigo " +
                "      , codigo_cliente " +
                "      , valor_total " +
                "      , numero_cartao " +
                "      , data ) " +
                " values " +
                "      ( ? " +
                "      , ? " +
                "      , ? " +
                "      , ? " +
                "      , ? ) ";

        int idx = 1;
        PreparedStatement pst = connection.prepareStatement(sb);
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
                String mensagemErro = "Falha ao inserir item do pedido no banco [" + itemPedido + "]";
                log.error(mensagemErro, ex);
                try {
                    logDao.inserir(mensagemErro);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }));

        return pedido;
    }

    public PedidoModel atualizar(PedidoModel pedido) throws SQLException {

        String sb = " update Pedidos set " +
                "   codigo_cliente = ? " +
                " , valor_total = ? " +
                " , numero_cartao = ? " +
                " , data = ? " +
                " where codigo = ? ";

        int idx = 1;
        PreparedStatement pst = connection.prepareStatement(sb);
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

        String sb = " select p.codigo " +
                "      , p.codigo_cliente " +
                "      , c.nome " +
                "      , p.valor_total " +
                "      , p.numero_cartao " +
                "      , p.data " +
                " from pedidos p " +
                " left join clientes c on p.codigo_cliente=c.codigo " +
                " where p.codigo = ? " +
                " order by p.codigo ";

        int idx = 1;
        PreparedStatement pst = connection.prepareStatement(sb);
        pst.setInt(idx, codigoQry);

        Statement st = connection.createStatement();
        ResultSet rs = pst.executeQuery();

        if (!rs.next()) {
            return Optional.empty();
        }

        Integer codigo = rs.getInt(idx++);
        Integer codigoCliente = rs.getInt(idx++);
        String nomeCliente = rs.getString(idx++);
        BigDecimal valorTotal = rs.getBigDecimal(idx++);
        String numeroCartao = rs.getString(idx++);
        Date data = rs.getDate(idx);

        ClienteModel cliente = ClienteModel.builder()
                .codigoCliente(codigoCliente)
                .nomeCliente(nomeCliente)
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
