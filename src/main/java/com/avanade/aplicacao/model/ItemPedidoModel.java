package com.avanade.aplicacao.model;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data @Builder
public class ItemPedidoModel {

    private Integer codigoProduto;
    private String nomeProduto;
    private BigDecimal quantidade;
    private BigDecimal valorUnitario;
    private BigDecimal valor;

}
