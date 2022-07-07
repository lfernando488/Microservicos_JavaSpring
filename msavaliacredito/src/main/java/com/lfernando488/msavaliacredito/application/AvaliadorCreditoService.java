package com.lfernando488.msavaliacredito.application;

import com.lfernando488.msavaliacredito.application.domain.model.CartaoCliente;
import com.lfernando488.msavaliacredito.application.domain.model.DadosCliente;
import com.lfernando488.msavaliacredito.application.domain.model.SituacaoCliente;
import com.lfernando488.msavaliacredito.application.ex.DadosClienteNotFoundException;
import com.lfernando488.msavaliacredito.application.ex.ErroComunicacaoMicroservicesException;
import com.lfernando488.msavaliacredito.infra.clientes.CartoesResourceClient;
import com.lfernando488.msavaliacredito.infra.clientes.ClienteResourceClient;
import feign.FeignException;
import feign.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AvaliadorCreditoService {

    private final ClienteResourceClient clienteResourceClient;
    private final CartoesResourceClient cartoesResourceClient;

    public SituacaoCliente obterSituacaoCliente(String cpf) throws DadosClienteNotFoundException, ErroComunicacaoMicroservicesException {
        try {
            ResponseEntity<DadosCliente> dadosClienteResponse = clienteResourceClient.dadosCliente(cpf);
            ResponseEntity<List<CartaoCliente>> cartoesResponse = cartoesResourceClient.getCartoesByCliente(cpf);

            return SituacaoCliente
                    .builder()
                    .cliente(dadosClienteResponse.getBody())
                    .cartoes(cartoesResponse.getBody())
                    .build();
        } catch (FeignException.FeignClientException fce) {
            int status = fce.status();

            if (HttpStatus.NOT_FOUND.value() == status) {
                throw new DadosClienteNotFoundException();
            }

            throw new ErroComunicacaoMicroservicesException(fce.getMessage(), status);
        }
    }

}
