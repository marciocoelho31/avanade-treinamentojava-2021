package com.avanade.aplicacao.dao;

import com.avanade.aplicacao.model.LogModel;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Properties;

@Slf4j
public class LogDao {

    private Connection connection;

    public LogDao() throws SQLException {
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

    public void inserir(String mensagem) throws SQLException {

        String sb = " insert into log " +
                "      ( data_hora " +
                "      , mensagem ) " +
                " values " +
                "      ( now() " +
                "      , ? ) ";

        int idx = 1;
        PreparedStatement pst = connection.prepareStatement(sb);
        pst.setString(idx, mensagem);

        int qtdLinhas = pst.executeUpdate();
        if (qtdLinhas == 0) {
            throw new SQLException("Nenhum registro foi inserido para o log []");
        }

    }

}
