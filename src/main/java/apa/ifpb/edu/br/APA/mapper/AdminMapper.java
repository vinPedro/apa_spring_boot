package apa.ifpb.edu.br.APA.mapper;

import apa.ifpb.edu.br.APA.dto.AdminResponseDTO;
import apa.ifpb.edu.br.APA.model.Admin;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AdminMapper {
    AdminResponseDTO toResponseDTO(Admin admin);
}
