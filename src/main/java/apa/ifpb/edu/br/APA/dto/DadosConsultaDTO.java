package apa.ifpb.edu.br.APA.dto;

import java.time.LocalDate;

// DTO composto para popular a tela do médico
public record DadosConsultaDTO(

        String nomePaciente,
        String cpfPaciente,
        LocalDate dataNascimento,

        Double peso,
        Double altura,
        String pressaoArterial,
        Double temperatura,
        String sintomasTriagem
) {}