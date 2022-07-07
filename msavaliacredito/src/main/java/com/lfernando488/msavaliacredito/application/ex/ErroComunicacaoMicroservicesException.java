package com.lfernando488.msavaliacredito.application.ex;

import lombok.Getter;

@Getter
public class ErroComunicacaoMicroservicesException extends Exception{

    private Integer status;

    public ErroComunicacaoMicroservicesException(String msg, Integer status) {
        super(msg);
        this.status = status;
    }
}
