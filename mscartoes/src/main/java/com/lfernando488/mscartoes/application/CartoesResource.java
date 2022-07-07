package com.lfernando488.mscartoes.application;

import com.lfernando488.mscartoes.domain.Cartao;
import com.lfernando488.mscartoes.domain.ClienteCartao;
import com.lfernando488.mscartoes.representation.CartaoSaveRequest;
import com.lfernando488.mscartoes.representation.CartoesPorClienteResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("cartoes")
@RequiredArgsConstructor
public class CartoesResource {

    private final CartaoService cartaoService;
    private final ClienteCartaoService clienteCartaoService;

    @GetMapping
    public String status(){
        return "ok";
    }

    @PostMapping
    public ResponseEntity cadastra(@RequestBody CartaoSaveRequest request){
        Cartao cartao = request.toModel();
        cartaoService.save(cartao);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping(params = "renda")
    public ResponseEntity<List<Cartao>> getCartoesRendaAteh(@RequestParam("renda") Long renda){
        List<Cartao> list = cartaoService.getCartoesRendaMenorIgual(renda);
        return ResponseEntity.ok(list);
    }

    @GetMapping(params = "cpf")
    public ResponseEntity <List<CartoesPorClienteResponse>> getCartoesByCliente(@RequestParam ("cpf") String cpf){

        List<ClienteCartao> list =  clienteCartaoService.listCartoesByCpf(cpf);
        List<CartoesPorClienteResponse> listCart = list.stream()
                .map(CartoesPorClienteResponse::fromModel)
                .collect(Collectors.toList());
        return ResponseEntity.ok(listCart);
    }

}
