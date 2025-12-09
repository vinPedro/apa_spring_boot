package apa.ifpb.edu.br.APA.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ProntuarioResponseDTO {
    private Long id;
    private Long atendimentoId;
    private String nomePaciente;
    private String nomeMedico;
    private String diagnostico;
    private LocalDateTime dataHoraFinalizacao;
}