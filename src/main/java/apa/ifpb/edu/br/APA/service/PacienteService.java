package apa.ifpb.edu.br.APA.service;

import apa.ifpb.edu.br.APA.dto.PacienteRequestDTO;
import apa.ifpb.edu.br.APA.dto.PacienteResponseDTO;
import apa.ifpb.edu.br.APA.exception.OperacaoInvalidaException;
import apa.ifpb.edu.br.APA.exception.RecursoNaoEncontradoException;
import apa.ifpb.edu.br.APA.mapper.PacienteMapper;
import apa.ifpb.edu.br.APA.model.Paciente;
import apa.ifpb.edu.br.APA.model.UnidadeSaude;
import apa.ifpb.edu.br.APA.repository.PacienteRepository;
import apa.ifpb.edu.br.APA.repository.UnidadeSaudeRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PacienteService {

    @Autowired
    private PacienteRepository pacienteRepository;

    @Autowired
    private UnidadeSaudeRepository unidadeSaudeRepository;

    @Autowired
    private PacienteMapper pacienteMapper;

    @Transactional
    public PacienteResponseDTO criarPaciente(PacienteRequestDTO dto) {

        validarCpfECns(dto.getCpf(), dto.getCns(), null);

        Paciente paciente = pacienteMapper.toEntity(dto);

        UnidadeSaude ubs = buscarUbsPorId(dto.getUnidadeSaudeId());
        paciente.setUnidadeSaudeVinculada(ubs);

        // ADD CRIPTOGRAFIA
        paciente.setSenha(dto.getSenha());

        Paciente pacienteSalvo = pacienteRepository.save(paciente);

        return pacienteMapper.toResponseDTO(pacienteSalvo);
    }

    @Transactional(readOnly = true)
    public PacienteResponseDTO buscarPorId(Long id) {
        Paciente paciente = buscarPacienteExistente(id);
        return pacienteMapper.toResponseDTO(paciente);
    }


    @Transactional(readOnly = true)
    public List<PacienteResponseDTO> listarTodos() {
        return pacienteRepository.findAll()
                .stream()
                .map(pacienteMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public PacienteResponseDTO atualizarPaciente(Long id, PacienteRequestDTO dto) {

        Paciente pacienteExistente = buscarPacienteExistente(id);
        validarCpfECns(dto.getCpf(), dto.getCns(), id);

        pacienteMapper.updateEntityFromDTO(dto, pacienteExistente);

        Long idUbsDto = dto.getUnidadeSaudeId();
        Long idUbsExistente = pacienteExistente.getUnidadeSaudeVinculada().getId();

        if (!idUbsDto.equals(idUbsExistente)) {
            UnidadeSaude novaUbs = buscarUbsPorId(idUbsDto);
            pacienteExistente.setUnidadeSaudeVinculada(novaUbs);
        }

        Paciente pacienteAtualizado = pacienteRepository.save(pacienteExistente);

        return pacienteMapper.toResponseDTO(pacienteAtualizado);
    }

    @Transactional
    public void deletarPaciente(Long id) {
        Paciente paciente = buscarPacienteExistente(id);

        pacienteRepository.delete(paciente);
    }

    private Paciente buscarPacienteExistente(Long id) {
        return pacienteRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Paciente não encontrado com ID: " + id));
    }

    private UnidadeSaude buscarUbsPorId(Long id) {
        return unidadeSaudeRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Unidade de Saúde não encontrada com ID: " + id));
    }

    private void validarCpfECns(String cpf, String cns, Long pacienteId) {
        // Valida CPF
        pacienteRepository.findByCpf(cpf).ifPresent(paciente -> {
            if (pacienteId == null || !paciente.getId().equals(pacienteId)) {
                throw new OperacaoInvalidaException("CPF já cadastrado para outro paciente.");
            }
        });

        // Valida CNS
        pacienteRepository.findByCns(cns).ifPresent(paciente -> {
            if (pacienteId == null || !paciente.getId().equals(pacienteId)) {
                throw new OperacaoInvalidaException("CNS já cadastrado para outro paciente.");
            }
        });
    }
}