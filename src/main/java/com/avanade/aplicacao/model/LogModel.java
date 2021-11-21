package com.avanade.aplicacao.model;

import lombok.Builder;
import lombok.Data;

import java.sql.Date;

@Data @Builder
public class LogModel {

    private Date DataHora;
    private String Mensagem;

}
