package apa.ifpb.edu.br.APA.mapper;

import apa.ifpb.edu.br.APA.dto.PacienteRequestDTO;
import apa.ifpb.edu.br.APA.dto.PacienteResponseDTO;
import apa.ifpb.edu.br.APA.model.Paciente;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface PacienteMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "unidadeSaudeVinculada", ignore = true)
    Paciente toEntity(PacienteRequestDTO dto);

    @Mapping(source = "unidadeSaudeVinculada.id", target = "unidadeSaudeId")
    PacienteResponseDTO toResponseDTO(Paciente paciente);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "senha", ignore = true)
    @Mapping(target = "unidadeSaudeVinculada", ignore = true)
    void updateEntityFromDTO(PacienteRequestDTO dto, @MappingTarget Paciente paciente);
}