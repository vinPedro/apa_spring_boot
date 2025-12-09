package apa.ifpb.edu.br.APA.service;

import apa.ifpb.edu.br.APA.dto.ProntuarioRequestDTO;
import apa.ifpb.edu.br.APA.dto.ProntuarioResponseDTO;
import apa.ifpb.edu.br.APA.exception.OperacaoInvalidaException;
import apa.ifpb.edu.br.APA.exception.RecursoNaoEncontradoException;
import apa.ifpb.edu.br.APA.mapper.ProntuarioMapper;
import apa.ifpb.edu.br.APA.model.*;
import apa.ifpb.edu.br.APA.repository.AtendimentoRepository;
import apa.ifpb.edu.br.APA.repository.ProfissionalRepository;
import apa.ifpb.edu.br.APA.repository.ProntuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProntuarioService {

    private final ProntuarioRepository prontuarioRepository;
    private final AtendimentoRepository atendimentoRepository;
    private final ProfissionalRepository profissionalRepository;
    private final ProntuarioMapper prontuarioMapper;

    @Transactional
    public ProntuarioResponseDTO salvarProntuario(ProntuarioRequestDTO dto) {

        Atendimento atendimento = atendimentoRepository.findById(dto.getAtendimentoId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Atendimento não encontrado"));

        if (atendimento.getStatus() != StatusAtendimento.EM_CONSULTA) {
            throw new OperacaoInvalidaException("Não é possível gerar prontuário. O atendimento não está em andamento.");
        }

        Prontuario prontuario = prontuarioMapper.toEntity(dto);
        prontuario.setAtendimento(atendimento);
        prontuario.setDataHoraFinalizacao(LocalDateTime.now());

        Prontuario salvo = prontuarioRepository.save(prontuario);

        atendimento.setStatus(StatusAtendimento.CONCLUIDO);
        atendimentoRepository.save(atendimento);

        if (dto.isMudarStatusMedicoParaIndisponivel()) {
            Profissional medico = atendimento.getMedico();
            if (medico != null) {
                medico.setDisponivelAtualmente(false);
                profissionalRepository.save(medico);
            }
        }

        return prontuarioMapper.toDTO(salvo);
    }

    @Transactional(readOnly = true)
    public List<ProntuarioResponseDTO> buscarHistorico(String cpfPaciente, Long medicoId, LocalDate inicio, LocalDate fim) {

        String cpfLimpo = null;
        if (cpfPaciente != null && !cpfPaciente.isBlank()) {
            cpfLimpo = cpfPaciente.replaceAll("\\D", "");
        }

        LocalDateTime dataInicio = (inicio != null) ? inicio.atStartOfDay() : null;
        LocalDateTime dataFim = (fim != null) ? fim.atTime(23, 59, 59) : null;

        return prontuarioRepository.buscarHistorico(cpfLimpo, medicoId, dataInicio, dataFim)
                .stream()
                .map(prontuarioMapper::toDTO)
                .collect(Collectors.toList());
    }

}