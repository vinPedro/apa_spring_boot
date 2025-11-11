package apa.ifpb.edu.br.APA.mapper;

import apa.ifpb.edu.br.APA.dto.AdminResponseDTO;
import apa.ifpb.edu.br.APA.model.Admin;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AdminMapper {

    @Mapping(source = "usuario.login", target = "login")
    AdminResponseDTO toResponseDTO(Admin admin);
}
