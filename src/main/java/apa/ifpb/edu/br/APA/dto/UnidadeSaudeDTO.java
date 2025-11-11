package apa.ifpb.edu.br.APA.dto;

import jakarta.validation.constraints.NotBlank; // <-- VERIFIQUE SE IMPORTOU
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UnidadeSaudeDTO {

    private Long id;

    @NotBlank(message = "O código CNES não pode estar em branco")
    private String codigoCnes;

    @NotBlank(message = "O CNPJ não pode estar em branco")
    private String cnpj;

    @NotBlank(message = "O Nome não pode estar em branco")
    private String nome;

    @NotBlank(message = "O Logradouro não pode estar em branco")
    private String logradouro;

    @NotBlank(message = "O Bairro não pode estar em branco")
    private String bairro;

    @NotBlank(message = "O Município não pode estar em branco")
    private String municipio;

    @NotBlank(message = "A UF não pode estar em branco")
    private String uf;
}