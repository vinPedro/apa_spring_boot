package apa.ifpb.edu.br.APA.service;

import apa.ifpb.edu.br.APA.dto.ExameRequestDTO;
import apa.ifpb.edu.br.APA.dto.ExameResponseDTO;
import apa.ifpb.edu.br.APA.exception.RecursoNaoEncontradoException;
import apa.ifpb.edu.br.APA.mapper.ExameMapper;
import apa.ifpb.edu.br.APA.model.Exame;
import apa.ifpb.edu.br.APA.model.Paciente;
import apa.ifpb.edu.br.APA.model.Profissional;
import apa.ifpb.edu.br.APA.repository.ExameRepository;
import apa.ifpb.edu.br.APA.repository.PacienteRepository;
import apa.ifpb.edu.br.APA.repository.ProfissionalRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ExameService {

    private final ExameRepository exameRepository;
    private final PacienteRepository pacienteRepository;
    private final ProfissionalRepository profissionalRepository;
    private final ExameMapper exameMapper;

    public ExameService(ExameRepository exameRepository,
                        PacienteRepository pacienteRepository,
                        ProfissionalRepository profissionalRepository,
                        ExameMapper exameMapper) {
        this.exameRepository = exameRepository;
        this.pacienteRepository = pacienteRepository;
        this.profissionalRepository = profissionalRepository;
        this.exameMapper = exameMapper;
    }

    @Transactional
    public ExameResponseDTO criarExame(ExameRequestDTO dto) {
        Paciente paciente = pacienteRepository.findById(dto.pacienteId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Paciente não encontrado com ID: " + dto.pacienteId()));

        Profissional profissional = profissionalRepository.findById(dto.profissionalId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Profissional não encontrado com ID: " + dto.profissionalId()));

        Exame exame = exameMapper.toEntity(dto, paciente, profissional);
        Exame salvo = exameRepository.save(exame);

        return exameMapper.toResponseDTO(salvo);
    }

    public List<ExameResponseDTO> listarTodos() {
        return exameRepository.findAll().stream()
                .map(exameMapper::toResponseDTO)
                .collect(Collectors.toList());
    }
}