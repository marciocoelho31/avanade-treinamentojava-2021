package com.avanade.aplicacao.utils;

import com.avanade.aplicacao.model.ClienteModel;
import com.avanade.aplicacao.model.PedidoModel;
import com.avanade.aplicacao.model.ItemPedidoModel;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Optional;

@Slf4j
public final class PedidoUtils {

    public static final int TAMANHO_CAMPOS_PEDIDO = 7;
    public static final int TAMANHO_CAMPOS_ITEMPEDIDO = 6;
    private static final SimpleDateFormat SDF;

    static {
         SDF = new SimpleDateFormat("ddMMyyyy");
    }

    private PedidoUtils() {
    }

    public static Optional<PedidoModel> criarPedido(String... campos) {
        if (campos.length != TAMANHO_CAMPOS_PEDIDO) {
            return Optional.empty();
        }

        try {
            Idx idx = new Idx();
            PedidoModel pedido = PedidoModel.builder()
                    .codigo(Integer.valueOf(campos[idx.inc()]))
                    .cliente(criarCliente(idx, campos).orElse(null))
                    .valorTotal(new BigDecimal(campos[idx.inc()]))
                    .numeroCartao(campos[idx.inc()])
                    .data(SDF.parse(campos[idx.inc()]))
                    .build();
            return Optional.of(pedido);
        }
        catch (Exception ex) {
            log.error("Falha ao criar Pedido - Campos: {}", campos, ex);
            return Optional.empty();
        }
    }

    static class Idx {
        private int idx;
        Idx() {
            idx = 1;
        }
        public int inc() {
            return idx++;
        }
    }

    private static Optional<ClienteModel> criarCliente(Idx idx, String...campos) {
        try {
          ClienteModel cliente = ClienteModel.builder()
                  .codigoCliente(Integer.valueOf(campos[idx.inc()]))
                  .nomeCliente(campos[idx.inc()])
                  .build();
          return Optional.of(cliente);
        } catch (Exception ex) {
            log.error("Falha ao criar Cliente - Campos: {}", campos, ex);
            return Optional.empty();
        }
    }

    public static Optional<ItemPedidoModel> criarItemPedido(int codigoPedido, String... campos) {
        if (campos.length != TAMANHO_CAMPOS_ITEMPEDIDO) {
            return Optional.empty();
        }

        try {
            IdxItem idxitem = new IdxItem();
            ItemPedidoModel itempedido = ItemPedidoModel.builder()
                    .codigo(codigoPedido)
                    .codigoProduto(Integer.valueOf(campos[idxitem.inc()]))
                    .quantidade(new BigDecimal(campos[idxitem.inc()]))
                    .nomeProduto(campos[idxitem.inc()])
                    .valorUnitario(new BigDecimal(campos[idxitem.inc()]))
                    .valor(new BigDecimal(campos[idxitem.inc()]))
                    .build();
            return Optional.of(itempedido);
        }
        catch (Exception ex) {
            log.error("Falha ao criar Item de Pedido - Campos: {}", campos, ex);
            return Optional.empty();
        }
    }

    static class IdxItem {
        private int idxitem;
        IdxItem() {
            idxitem = 1;
        }
        public int inc() {
            return idxitem++;
        }
    }

}
