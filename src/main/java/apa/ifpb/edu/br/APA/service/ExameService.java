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
        Paciente paciente;

        // LÓGICA HÍBRIDA: Tenta pelo ID, se não der, tenta pelo CPF
        if (dto.pacienteId() != null) {
            paciente = pacienteRepository.findById(dto.pacienteId())
                    .orElseThrow(() -> new RecursoNaoEncontradoException("Paciente não encontrado com ID: " + dto.pacienteId()));
        } else if (dto.pacienteCpf() != null && !dto.pacienteCpf().isBlank()) {
            paciente = pacienteRepository.findByCpf(dto.pacienteCpf())
                    .orElseThrow(() -> new RecursoNaoEncontradoException("Paciente não encontrado com CPF: " + dto.pacienteCpf()));
        } else {
            throw new IllegalArgumentException("É obrigatório informar o ID ou o CPF do paciente.");
        }

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

    public List<ExameResponseDTO> recuperarPorCpf(String cpf) {
        List<Exame> exames = exameRepository.findByPacienteCpf(cpf);

        return exames.stream()
                .map(exameMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    public ExameResponseDTO recuperarPorProtocolo(Long id) {
        Exame exame = exameRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Exame não encontrado com o protocolo: " + id));
        return exameMapper.toResponseDTO(exame);
    }
}