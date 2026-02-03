package apa.ifpb.edu.br.APA.dto;

import apa.ifpb.edu.br.APA.model.StatusAtendimento;
import apa.ifpb.edu.br.APA.model.TipoPrioridade;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class AtendimentoResponseDTO {
    private Long id;
    private String senha;
    private LocalDateTime dataHoraChegada;
    private StatusAtendimento status;
    private TipoPrioridade prioridade;
    private Long pacienteId;
    private String pacienteNome;
    private String unidadeSaudeNome;
}