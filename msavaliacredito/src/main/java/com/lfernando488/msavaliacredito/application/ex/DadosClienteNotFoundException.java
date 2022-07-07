package com.lfernando488.msavaliacredito.application.ex;

public class DadosClienteNotFoundException extends Exception{

    public DadosClienteNotFoundException(){
        super("Dados do cliente não encontados para este CPF");
    }

}
