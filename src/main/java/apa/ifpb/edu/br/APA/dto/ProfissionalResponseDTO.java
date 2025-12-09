package apa.ifpb.edu.br.APA.dto;

import apa.ifpb.edu.br.APA.model.ConselhoProfissional;
import apa.ifpb.edu.br.APA.model.DiaSemana;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalTime;
import java.util.Set;

@Data
public class ProfissionalResponseDTO {

    private Long id;
    private String nomeCompleto;
    private String cpf;
    private String cns;
    private ConselhoProfissional conselhoProfissional;
    private String registroConselho;
    private String ufConselho;
    private Long ubsVinculadaId;
    private String emailInstitucional;
    private String telefoneContato;

    private Set<DiaSemana> diasTrabalho;

    @JsonFormat(pattern = "HH:mm")
    private LocalTime horaInicio;

    @JsonFormat(pattern = "HH:mm")
    private LocalTime horaFim;

    private boolean disponivelAtualmente;
}