package apa.ifpb.edu.br.APA.dto;

import apa.ifpb.edu.br.APA.model.RacaCor;
import apa.ifpb.edu.br.APA.model.Sexo;
import lombok.Data;
import java.time.LocalDate;

@Data
public class PacienteResponseDTO {

    private Long id;

    private String cns;
    private String cpf;
    private String nomeCompleto;
    private LocalDate dataNascimento;
    private Sexo sexo;
    private RacaCor racacor;
    private String telefone;
    private String email;

    private String logradouro;
    private String bairro;
    private String municipio;
    private String uf;
    private String cep;

    private Long unidadeSaudeId;

}