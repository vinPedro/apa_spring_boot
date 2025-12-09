package apa.ifpb.edu.br.APA.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class FichamentoDTO {

    private Long id;

    @NotNull(message = "O ID do atendimento é obrigatório")
    private Long atendimentoId;

    private Double peso;
    private Double altura;
    private String pressaoArterial;
    private Double temperatura;
    private String sintomas;
    private String observacao;

    private LocalDateTime dataHora;
}