package apa.ifpb.edu.br.APA.mapper;

import apa.ifpb.edu.br.APA.dto.ExameRequestDTO;
import apa.ifpb.edu.br.APA.dto.ExameResponseDTO;
import apa.ifpb.edu.br.APA.model.Exame;
import apa.ifpb.edu.br.APA.model.Paciente;
import apa.ifpb.edu.br.APA.model.Profissional;
import org.springframework.stereotype.Component;

@Component
public class ExameMapper {

    public Exame toEntity(ExameRequestDTO dto, Paciente paciente, Profissional profissional) {
        Exame exame = new Exame();
        exame.setPaciente(paciente);
        exame.setProfissional(profissional);
        exame.setTipoExame(dto.tipoExame());
        exame.setDataSolicitacao(dto.dataSolicitacao());
        exame.setDescricao(dto.descricao());
        exame.setPrioridade(dto.prioridade());
        exame.setStatus(dto.status());
        return exame;
    }

    public ExameResponseDTO toResponseDTO(Exame exame) {
        return new ExameResponseDTO(
                exame.getId(),
                exame.getPaciente().getNomeCompleto(),
                exame.getPaciente().getCpf(),
                exame.getProfissional().getNomeCompleto(),
                exame.getTipoExame(),
                exame.getDataSolicitacao(),
                exame.getDescricao(),
                exame.getPrioridade(),
                exame.getStatus()
        );
    }
}