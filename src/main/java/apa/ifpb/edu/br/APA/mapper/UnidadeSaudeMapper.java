package apa.ifpb.edu.br.APA.mapper;

import apa.ifpb.edu.br.APA.dto.UnidadeSaudeDTO;
import apa.ifpb.edu.br.APA.model.UnidadeSaude;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UnidadeSaudeMapper {

    @Mapping(target = "id", ignore = true)
    UnidadeSaude toUnidadeSaude(UnidadeSaudeDTO dto);

    UnidadeSaudeDTO toDTO(UnidadeSaude unidadeSaude);

    @Mapping(target = "id", ignore = true)
    void updateFromDto(UnidadeSaudeDTO dto, @MappingTarget UnidadeSaude unidadeSaude);
}