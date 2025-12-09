package apa.ifpb.edu.br.APA.mapper;

import apa.ifpb.edu.br.APA.dto.ProntuarioRequestDTO;
import apa.ifpb.edu.br.APA.dto.ProntuarioResponseDTO;
import apa.ifpb.edu.br.APA.model.Prontuario;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProntuarioMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "atendimento", ignore = true)
    @Mapping(target = "dataHoraFinalizacao", ignore = true)
    Prontuario toEntity(ProntuarioRequestDTO dto);

    @Mapping(source = "atendimento.id", target = "atendimentoId")
    @Mapping(source = "atendimento.paciente.nomeCompleto", target = "nomePaciente")
    @Mapping(source = "atendimento.medico.nomeCompleto", target = "nomeMedico")
    @Mapping(source = "atendimento.unidadeSaude.nome", target = "nomeUnidade")
    ProntuarioResponseDTO toDTO(Prontuario prontuario);
}