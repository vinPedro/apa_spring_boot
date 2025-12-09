package apa.ifpb.edu.br.APA.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ProntuarioRequestDTO {

    @NotNull(message = "O ID do atendimento é obrigatório")
    private Long atendimentoId;

    @NotBlank(message = "A queixa principal é obrigatória")
    private String queixaPrincipal;

    private String historicoDoenca;
    private String exameFisico;

    @NotBlank(message = "O diagnóstico é obrigatório")
    private String diagnostico;

    private String prescricaoMedica;
    private String examesSolicitados;
    private Integer atestadoDias;

    private boolean mudarStatusMedicoParaIndisponivel;
}