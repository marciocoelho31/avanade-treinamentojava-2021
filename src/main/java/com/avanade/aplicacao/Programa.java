package com.avanade.aplicacao;

import com.avanade.aplicacao.servicos.ServicoProcessarArquivos;
import com.avanade.aplicacao.validacoes.ValidarArquivos;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FilenameFilter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.MessageFormat;

@Slf4j

public class Programa  {

    public static void main(String[] args) {

        if (args.length == 0) {
            System.out.println("Parâmetro caminho obrigatório");
            System.out.println("Uso:");
            System.out.println("java Programa [CAMINHO]");
            System.exit(-1);
            return;
        }

        log.info("Iniciando aplicação...");
        Programa programa = new Programa();
        programa.iniciar(args[0]);
    }

    public void iniciar(String caminhoDirEntrada) {
        ServicoProcessarArquivos servico = new ServicoProcessarArquivos(caminhoDirEntrada);
        servico.executar();
    }

}
