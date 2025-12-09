package apa.ifpb.edu.br.APA.mapper;

import apa.ifpb.edu.br.APA.dto.FichamentoDTO;
import apa.ifpb.edu.br.APA.model.Fichamento;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface FichamentoMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "atendimento", ignore = true) // Será buscado no service
    @Mapping(target = "dataHora", ignore = true) // Será definido no service
    Fichamento toEntity(FichamentoDTO dto);

    @Mapping(source = "atendimento.id", target = "atendimentoId")
    FichamentoDTO toDTO(Fichamento fichamento);
}