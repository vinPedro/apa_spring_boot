package apa.ifpb.edu.br.APA.mapper;

import apa.ifpb.edu.br.APA.dto.ProfissionalRequestDTO;
import apa.ifpb.edu.br.APA.dto.ProfissionalResponseDTO;
import apa.ifpb.edu.br.APA.model.Profissional;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ProfissionalMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "ubsVinculada", ignore = true)
    Profissional toEntity(ProfissionalRequestDTO dto);

    @Mapping(source = "ubsVinculada.id", target = "ubsVinculadaId")
    ProfissionalResponseDTO toResponseDTO(Profissional profissional);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "senha", ignore = true)
    @Mapping(target = "ubsVinculada", ignore = true)
    void updateEntityFromDTO(ProfissionalRequestDTO dto, @MappingTarget Profissional profissional);
}