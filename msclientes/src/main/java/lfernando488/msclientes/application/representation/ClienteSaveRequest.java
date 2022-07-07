package lfernando488.msclientes.application.representation;

import lfernando488.msclientes.domain.Cliente;
import lombok.Data;

@Data
public class ClienteSaveRequest {

    private String cpf;
    private String nome;
    private int idade;

    public Cliente toModel(){
        return  new Cliente(cpf,nome,idade);
    }
}
