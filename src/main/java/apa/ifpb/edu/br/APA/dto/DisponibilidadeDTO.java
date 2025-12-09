package apa.ifpb.edu.br.APA.dto;

import apa.ifpb.edu.br.APA.model.DiaSemana;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalTime;
import java.util.Set;

@Data
public class DisponibilidadeDTO {

    private Set<DiaSemana> diasTrabalho;

    @JsonFormat(pattern = "HH:mm")
    private LocalTime horaInicio;

    @JsonFormat(pattern = "HH:mm")
    private LocalTime horaFim;

    private Boolean disponivelAtualmente;
}
