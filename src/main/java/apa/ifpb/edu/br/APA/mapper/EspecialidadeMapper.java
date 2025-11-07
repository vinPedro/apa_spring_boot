package apa.ifpb.edu.br.APA.mapper;

import apa.ifpb.edu.br.APA.dto.EspecialidadeDTO;
import apa.ifpb.edu.br.APA.model.Especialidade;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface EspecialidadeMapper {

    @Mapping(target = "id", ignore = true)
    Especialidade toEspecialidade(EspecialidadeDTO dto);

    EspecialidadeDTO toDTO(Especialidade especialidade);

    @Mapping(target = "id", ignore = true)
    void updateFromDto(EspecialidadeDTO dto, @MappingTarget Especialidade especialidade);
}
