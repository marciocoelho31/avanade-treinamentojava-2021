package com.avanade.aplicacao.model;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data @Builder
public class ItemPedidoModel {

    private Integer codigo;
    private Integer codigoProduto;
    private BigDecimal quantidade;
    private String nomeProduto;
    private BigDecimal valorUnitario;
    private BigDecimal valor;

}
