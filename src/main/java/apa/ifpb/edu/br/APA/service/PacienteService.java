package apa.ifpb.edu.br.APA.service;

import apa.ifpb.edu.br.APA.cliente.ViaCEPCliente;
import apa.ifpb.edu.br.APA.dto.PacienteRequestDTO;
import apa.ifpb.edu.br.APA.dto.PacienteResponseDTO;
import apa.ifpb.edu.br.APA.dto.ViaCEPResponseDTO;
import apa.ifpb.edu.br.APA.exception.OperacaoInvalidaException;
import apa.ifpb.edu.br.APA.exception.RecursoNaoEncontradoException;
import apa.ifpb.edu.br.APA.mapper.PacienteMapper;
import apa.ifpb.edu.br.APA.model.Paciente;
import apa.ifpb.edu.br.APA.model.Role;
import apa.ifpb.edu.br.APA.model.UnidadeSaude;
import apa.ifpb.edu.br.APA.model.Usuario;
import apa.ifpb.edu.br.APA.repository.PacienteRepository;
import apa.ifpb.edu.br.APA.repository.UnidadeSaudeRepository;
import apa.ifpb.edu.br.APA.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PacienteService {

    private final PacienteRepository pacienteRepository;
    private final UnidadeSaudeRepository unidadeSaudeRepository;
    private final PacienteMapper pacienteMapper;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final ViaCEPCliente viaCEPCliente;

    @Transactional
    public PacienteResponseDTO criarPaciente(PacienteRequestDTO dto) {

        validarCpfECns(dto.getCpf(), dto.getCns(), null);

        usuarioRepository.findByLogin(dto.getEmail()).ifPresent(u -> {
            throw new OperacaoInvalidaException("Email já cadastrado para outro usuário.");
        });

        Paciente paciente = pacienteMapper.toEntity(dto);

        UnidadeSaude ubs = buscarUbsPorId(dto.getUnidadeSaudeId());
        paciente.setUnidadeSaudeVinculada(ubs);

        String senhaCriptografada = passwordEncoder.encode(dto.getSenha());

        Usuario novoUsuario = new Usuario(
                null,
                dto.getEmail(),
                senhaCriptografada,
                Role.ROLE_PACIENTE
        );

        paciente.setUsuario(novoUsuario);

        ViaCEPResponseDTO endereco = viaCEPCliente.buscarCEP(dto.getCep());
        paciente.setLogradouro(endereco.getLogradouro());
        paciente.setBairro(endereco.getBairro());
        paciente.setMunicipio(endereco.getLocalidade());
        paciente.setUf(endereco.getUf());
        paciente.setCep(endereco.getCep());

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

        String novoEmail = dto.getEmail();
        String emailAtual = pacienteExistente.getUsuario().getLogin();

        if (novoEmail != null && !novoEmail.equals(emailAtual)) {
            usuarioRepository.findByLogin(novoEmail).ifPresent(u -> {
                throw new OperacaoInvalidaException("Email já cadastrado para outro usuário.");
            });
            pacienteExistente.getUsuario().setLogin(novoEmail); // Atualiza o login
        }

        pacienteMapper.updateEntityFromDTO(dto, pacienteExistente);

        Long idUbsDto = dto.getUnidadeSaudeId();
        Long idUbsExistente = pacienteExistente.getUnidadeSaudeVinculada().getId();

        if (idUbsDto != null && !idUbsDto.equals(idUbsExistente)) {
            UnidadeSaude novaUbs = buscarUbsPorId(idUbsDto);
            pacienteExistente.setUnidadeSaudeVinculada(novaUbs);
        }


        ViaCEPResponseDTO endereco = viaCEPCliente.buscarCEP(dto.getCep());
        pacienteExistente.setLogradouro(endereco.getLogradouro());
        pacienteExistente.setBairro(endereco.getBairro());
        pacienteExistente.setMunicipio(endereco.getLocalidade());
        pacienteExistente.setUf(endereco.getUf());
        pacienteExistente.setCep(endereco.getCep());

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

    @Transactional(readOnly = true)
    public List<PacienteResponseDTO> buscarPorTipo(String tipo, String termo) {
        List<Paciente> pacientes;

        if (termo == null || termo.isBlank()) {
            return listarTodos();
        }

        switch (tipo.toUpperCase()) {
            case "NOME":
                pacientes = pacienteRepository.findByNomeCompletoContainingIgnoreCase(termo);
                break;
            case "CPF":
                // Remove pontuação se vier do front
                String cpfLimpo = termo.replaceAll("\\D", "");
                pacientes = pacienteRepository.findByCpfContaining(cpfLimpo);
                break;
            case "CNS":
                // Remove pontuação se vier do front
                String cnsLimpo = termo.replaceAll("\\D", "");
                pacientes = pacienteRepository.findByCnsContaining(cnsLimpo);
                break;
            default:
                return List.of(); // Retorna vazio se o tipo for inválido
        }

        return pacientes.stream()
                .map(pacienteMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    private void validarCpfECns(String cpf, String cns, Long pacienteId) {
        pacienteRepository.findByCpf(cpf).ifPresent(paciente -> {
            if (pacienteId == null || !paciente.getId().equals(pacienteId)) {
                throw new OperacaoInvalidaException("CPF já cadastrado para outro paciente.");
            }
        });

        pacienteRepository.findByCns(cns).ifPresent(paciente -> {
            if (pacienteId == null || !paciente.getId().equals(pacienteId)) {
                throw new OperacaoInvalidaException("CNS já cadastrado para outro paciente.");
            }
        });
    }
}