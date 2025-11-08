package apa.ifpb.edu.br.APA.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UnidadeSaudeDTO {

    private Long id;
    private String codigoCnes;
    private String cnpj;
    private String nomeFantasia;
    private String logradouro;
    private String bairro;
    private String municipio;
    private String uf;
}