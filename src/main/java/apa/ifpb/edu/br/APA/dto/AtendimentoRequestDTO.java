package apa.ifpb.edu.br.APA.dto;

import apa.ifpb.edu.br.APA.model.TipoPrioridade;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AtendimentoRequestDTO {
    @NotNull(message = "O ID do paciente é obrigatório")
    private Long pacienteId;

    @NotNull(message = "O ID da Unidade de Saúde é obrigatório")
    private Long unidadeSaudeId;

    @NotNull(message = "A prioridade é obrigatória")
    private TipoPrioridade prioridade;
}