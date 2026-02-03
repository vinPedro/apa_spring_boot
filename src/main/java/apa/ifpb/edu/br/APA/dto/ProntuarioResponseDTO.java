package apa.ifpb.edu.br.APA.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ProntuarioResponseDTO {
    private Long id;
    private Long atendimentoId;
    private String nomePaciente;
    private String nomeMedico;
    private String nomeUnidade;

    // --- NOVOS CAMPOS ADICIONADOS ---
    // Esses campos agora serão preenchidos automaticamente pelo Mapper
    // e enviados para o Front-end.
    private String queixaPrincipal;
    private String historicoDoenca;
    private String exameFisico;
    private String diagnostico;
    private String prescricaoMedica;
    // --------------------------------

    private LocalDateTime dataHoraFinalizacao;
}