package com.avanade.aplicacao.servicos;

import com.avanade.aplicacao.model.ItemPedidoModel;
import com.avanade.aplicacao.model.PedidoModel;
import com.avanade.aplicacao.utils.PedidoUtils;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Slf4j
@Builder
public class ServicoLerArquivo {

    public List<PedidoModel> executar(String caminhoArquivo) {

        List<String> linhas;

        try {
            File arquivo = new File(caminhoArquivo);
            linhas = FileUtils.readLines(arquivo, Charset.defaultCharset());

            if (linhas.isEmpty()) {
                return Collections.emptyList();
            }
        } catch (IOException ex) {
            log.error("Falha ao ler arquivo [{}]", caminhoArquivo, ex);
            return Collections.emptyList();
        }
        return criarPedidos(linhas);
    }

    private List<PedidoModel> criarPedidos(List<String> linhas) {

        List<PedidoModel> pedidos = new ArrayList<>();
        PedidoModel pedidoCorrente = null;
        List<ItemPedidoModel> itensPedido = new ArrayList<>();

        String ultimaLinha = linhas.get(linhas.size() - 1);

        for (String linha : linhas) {

            String[] campos = StringUtils.split(linha, ";");
            if (campos.length == 0) {
                continue;
            }

            if (campos[0].equals("C")) {

                if (itensPedido.size() > 0) {
                    itensPedido = gravaPedido(pedidos, pedidoCorrente, itensPedido);
                }

                Optional<PedidoModel> pedidoOpt = PedidoUtils.criarPedido(campos);
                if (pedidoOpt.isEmpty()) {
                    continue;
                }
                pedidoCorrente = pedidoOpt.get();

            } else if (campos[0].equals("D")) {

                Optional<ItemPedidoModel> itempedidoOpt =
                        PedidoUtils.criarItemPedido(pedidoCorrente.getCodigo(), campos);
                if (itempedidoOpt.isEmpty()) {
                    continue;
                }

                itensPedido.add(itempedidoOpt.get());

                if (linha.equals(ultimaLinha)) {
                    itensPedido = gravaPedido(pedidos, pedidoCorrente, itensPedido);
                }

            }

        }

        return pedidos;
    }

    private List<ItemPedidoModel> gravaPedido(List<PedidoModel> pedidos, PedidoModel pedidoCorrente, List<ItemPedidoModel> itensPedido) {
        if (pedidoCorrente != null) {
            pedidoCorrente.setItens(itensPedido);
            pedidos.add(pedidoCorrente);
            itensPedido = new ArrayList<>();
        }
        return itensPedido;
    }
}
