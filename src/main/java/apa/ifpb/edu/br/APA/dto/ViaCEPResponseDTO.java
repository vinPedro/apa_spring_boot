package apa.ifpb.edu.br.APA.dto;

import lombok.Data;

@Data
public class ViaCEPResponseDTO {
    private String cep;
    private String logradouro;
    private String bairro;
    private String localidade;
    private String uf;
    private boolean erro;
}
