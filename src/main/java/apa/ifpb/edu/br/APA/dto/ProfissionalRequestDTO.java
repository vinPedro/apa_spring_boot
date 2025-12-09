package apa.ifpb.edu.br.APA.dto;

import apa.ifpb.edu.br.APA.model.ConselhoProfissional;
import lombok.Data;

@Data
public class ProfissionalRequestDTO {

    private String nomeCompleto;
    private String cpf;
    private String cns;
    private ConselhoProfissional conselhoProfissional;
    private String registroConselho;
    private String ufConselho;
    private Long ubsVinculadaId;
    private String emailInstitucional;
    private String telefoneContato;
    private String senha;
}