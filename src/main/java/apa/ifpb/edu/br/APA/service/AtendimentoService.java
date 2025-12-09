package apa.ifpb.edu.br.APA.service;

import apa.ifpb.edu.br.APA.dto.AtendimentoRequestDTO;
import apa.ifpb.edu.br.APA.dto.AtendimentoResponseDTO;
import apa.ifpb.edu.br.APA.exception.RecursoNaoEncontradoException;
import apa.ifpb.edu.br.APA.mapper.AtendimentoMapper;
import apa.ifpb.edu.br.APA.model.*;
import apa.ifpb.edu.br.APA.repository.AtendimentoRepository;
import apa.ifpb.edu.br.APA.repository.PacienteRepository;
import apa.ifpb.edu.br.APA.repository.UnidadeSaudeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AtendimentoService {

    private final AtendimentoRepository atendimentoRepository;
    private final PacienteRepository pacienteRepository;
    private final UnidadeSaudeRepository unidadeSaudeRepository;
    private final AtendimentoMapper atendimentoMapper;

    @Transactional
    public AtendimentoResponseDTO entrarNaFila(AtendimentoRequestDTO dto) {

        // 1. Busca Paciente e Unidade (Lança erro 404 se não achar)
        Paciente paciente = pacienteRepository.findById(dto.getPacienteId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Paciente não encontrado com ID: " + dto.getPacienteId()));

        UnidadeSaude unidade = unidadeSaudeRepository.findById(dto.getUnidadeSaudeId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Unidade de Saúde não encontrada com ID: " + dto.getUnidadeSaudeId()));

        // 2. Lógica da Senha (P1, P2... PR1, PR2...)
        // Pega o começo do dia de hoje (00:00)
        LocalDateTime inicioDoDia = LocalDate.now().atStartOfDay();

        // Conta quantos já existem hoje com essa prioridade nesta unidade
        Long contagem = atendimentoRepository.contarAtendimentosDoDia(unidade.getId(), dto.getPrioridade(), inicioDoDia);
        Long proximoNumero = contagem + 1;

        // Define o prefixo (P = Normal, PR = Prioridade)
        String prefixo = (dto.getPrioridade() == TipoPrioridade.PRIORIDADE) ? "PR" : "P";
        String senhaGerada = prefixo + proximoNumero;

        // 3. Cria a entidade Atendimento
        // Usamos o mapper para converter o básico, mas definimos os campos calculados manualmente
        Atendimento atendimento = atendimentoMapper.toEntity(dto);

        atendimento.setPaciente(paciente);
        atendimento.setUnidadeSaude(unidade);
        atendimento.setSenha(senhaGerada);
        atendimento.setDataHoraChegada(LocalDateTime.now());
        atendimento.setStatus(StatusAtendimento.AGUARDANDO); // Status inicial

        // 4. Salva no banco
        Atendimento salvo = atendimentoRepository.save(atendimento);

        // 5. Retorna o DTO de resposta
        return atendimentoMapper.toResponseDTO(salvo);
    }

    @Transactional(readOnly = true)
    public List<AtendimentoResponseDTO> listarFilaPorUnidade(Long unidadeId) {
        // Busca atendimentos da unidade com status AGUARDANDO
        // Atenção: Certifique-se que o método findByUnidadeSaudeIdAndStatus aceita StatusAtendimento (Enum) ou String no Repository
        List<Atendimento> atendimentos = atendimentoRepository.findByUnidadeSaudeIdAndStatus(unidadeId, StatusAtendimento.AGUARDANDO);
        
        return atendimentos.stream()
                .map(atendimentoMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<AtendimentoResponseDTO> listarFila(Long unidadeId, String statusString) {

        // 1. Define o intervalo de tempo (O dia de HOJE completo)
        LocalDateTime inicioDia = LocalDate.now().atStartOfDay(); // 00:00:00
        LocalDateTime fimDia = LocalDate.now().atTime(23, 59, 59); // 23:59:59

        List<Atendimento> lista;

        // 2. Verifica se veio um filtro de status
        if (statusString != null && !statusString.isBlank()) {
            try {
                // Converte String para Enum (ex: "AGUARDANDO" -> StatusAtendimento.AGUARDANDO)
                StatusAtendimento status = StatusAtendimento.valueOf(statusString.toUpperCase());
                lista = atendimentoRepository.buscarFilaPorStatus(unidadeId, status, inicioDia, fimDia);
            } catch (IllegalArgumentException e) {
                // Se mandar status inválido, retorna lista vazia (ou poderia lançar erro)
                return List.of();
            }
        } else {
            // Se não veio status, traz tudo do dia
            lista = atendimentoRepository.findByUnidadeSaudeIdAndDataHoraChegadaBetweenOrderByDataHoraChegadaAsc(
                    unidadeId, inicioDia, fimDia);
        }

        // 3. Converte para DTO
        return lista.stream()
                .map(atendimentoMapper::toResponseDTO)
                .collect(Collectors.toList());
    }
}