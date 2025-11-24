package apa.ifpb.edu.br.APA.cliente;

import apa.ifpb.edu.br.APA.dto.ViaCEPResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "viaCepClient", url = "https://viacep.com.br/ws")
public interface ViaCEPCliente {

    @GetMapping("/{cep}/json/")
    ViaCEPResponseDTO buscarCEP(@PathVariable("cep") String cep);
}