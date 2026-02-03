package apa.ifpb.edu.br.APA.dto;

import apa.ifpb.edu.br.APA.model.StatusExame;
import apa.ifpb.edu.br.APA.model.TipoPrioridade;

import java.time.LocalDate;

public record ExameResponseDTO(
        Long id,
        String nomePaciente,
        String cpfPaciente,
        String nomeProfissional,
        String tipoExame,
        LocalDate dataSolicitacao,
        String descricao,
        TipoPrioridade prioridade,
        StatusExame status
) {}