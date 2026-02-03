package apa.ifpb.edu.br.APA.mapper;

import apa.ifpb.edu.br.APA.dto.AtendimentoRequestDTO;
import apa.ifpb.edu.br.APA.dto.AtendimentoResponseDTO;
import apa.ifpb.edu.br.APA.model.Atendimento;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AtendimentoMapper {

    // Do DTO para Entidade:
    // Ignoramos o ID (gerado auto), senha e data (gerados no service),
    // e status (padrão AGUARDANDO).
    // Ignoramos paciente e unidade aqui pois buscaremos no Service pelos IDs.
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "senha", ignore = true)
    @Mapping(target = "dataHoraChegada", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "paciente", ignore = true)
    @Mapping(target = "unidadeSaude", ignore = true)
    Atendimento toEntity(AtendimentoRequestDTO dto);

    @Mapping(source = "paciente.id", target = "pacienteId")

    // Da Entidade para DTO de Resposta:
    // Pegamos os nomes de dentro dos objetos relacionados
    @Mapping(source = "paciente.nomeCompleto", target = "pacienteNome")
    @Mapping(source = "unidadeSaude.nome", target = "unidadeSaudeNome")
    AtendimentoResponseDTO toResponseDTO(Atendimento atendimento);
}