package apa.ifpb.edu.br.APA.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ChamarPacienteDTO {
    @NotNull(message = "O ID da unidade é obrigatório")
    private Long unidadeSaudeId;

    @NotNull(message = "O ID do médico é obrigatório")
    private Long medicoId;
}