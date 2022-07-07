package com.lfernando488.msavaliacredito.application;

import com.lfernando488.msavaliacredito.application.domain.model.*;
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

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

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

    public RetornoAvaliacaoCliente realizarAvaliacao(String cpf, Long renda) throws DadosClienteNotFoundException, ErroComunicacaoMicroservicesException{
        try{
            ResponseEntity<DadosCliente> dadosClienteResponse = clienteResourceClient.dadosCliente(cpf);
            ResponseEntity<List<Cartao>> cartoesResponse = cartoesResourceClient.getCartoesRendaAteh(renda);

            List<Cartao> cartoes = cartoesResponse.getBody();

            var listaCartoesAprovados = cartoes
                    .stream()
                    .map( cartao -> {

                        DadosCliente dadosCliente = dadosClienteResponse.getBody();

                        BigDecimal limitebasico = cartao.getLimiteBasico();
                        BigDecimal idadeBD = BigDecimal.valueOf(dadosCliente.getIdade());
                        var fator = idadeBD.divide(BigDecimal.valueOf(10));
                        BigDecimal limiteAprovado = fator.multiply(limitebasico);

                        CartaoAprovado aprovado = new CartaoAprovado();
                        aprovado.setCartao(cartao.getNome());
                        aprovado.setBandeira(cartao.getBandeira());
                        aprovado.setLimiteAprovado(limiteAprovado);

                        return aprovado;
                    }).collect(Collectors.toList());

            return new RetornoAvaliacaoCliente(listaCartoesAprovados);

        }catch (FeignException.FeignClientException e){
            int status = e.status();
            if(HttpStatus.NOT_FOUND.value() == status){
                throw new DadosClienteNotFoundException();
            }
            throw new ErroComunicacaoMicroservicesException(e.getMessage(), status);
        }
    }

}
