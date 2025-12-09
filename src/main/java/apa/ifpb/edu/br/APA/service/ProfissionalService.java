package apa.ifpb.edu.br.APA.service;

import apa.ifpb.edu.br.APA.dto.DisponibilidadeDTO;
import apa.ifpb.edu.br.APA.dto.ProfissionalRequestDTO;
import apa.ifpb.edu.br.APA.dto.ProfissionalResponseDTO;
import apa.ifpb.edu.br.APA.exception.OperacaoInvalidaException;
import apa.ifpb.edu.br.APA.exception.RecursoNaoEncontradoException;
import apa.ifpb.edu.br.APA.mapper.ProfissionalMapper;
import apa.ifpb.edu.br.APA.model.Profissional;
import apa.ifpb.edu.br.APA.model.Role;
import apa.ifpb.edu.br.APA.model.UnidadeSaude;
import apa.ifpb.edu.br.APA.model.Usuario;
import apa.ifpb.edu.br.APA.repository.ProfissionalRepository;
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
public class ProfissionalService {

    private final ProfissionalRepository profissionalRepository;
    private final UnidadeSaudeRepository unidadeSaudeRepository;
    private final ProfissionalMapper profissionalMapper;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;


    @Transactional
    public ProfissionalResponseDTO criarProfissional(ProfissionalRequestDTO dto) {

        validarCpfCnsEmail(dto.getCpf(), dto.getCns(), dto.getEmailInstitucional(), null);

        usuarioRepository.findByLogin(dto.getCpf()).ifPresent(u -> {
            throw new OperacaoInvalidaException("CPF já cadastrado como login para outro usuário.");
        });

        Profissional profissional = profissionalMapper.toEntity(dto);

        UnidadeSaude ubs = buscarUbsPorId(dto.getUbsVinculadaId());
        profissional.setUbsVinculada(ubs);

        String senhaCriptografada = passwordEncoder.encode(dto.getSenha());

        Usuario novoUsuario = new Usuario(
                null,
                dto.getCpf(),
                senhaCriptografada,
                Role.ROLE_PROFISSIONAL
        );

        profissional.setUsuario(novoUsuario);

        Profissional salvo = profissionalRepository.save(profissional);
        return profissionalMapper.toResponseDTO(salvo);
    }

    @Transactional(readOnly = true)
    public ProfissionalResponseDTO buscarPorId(Long id) {
        Profissional profissional = buscarProfissionalExistente(id);
        return profissionalMapper.toResponseDTO(profissional);
    }

    @Transactional(readOnly = true)
    public List<ProfissionalResponseDTO> listarTodos() {
        return profissionalRepository.findAll()
                .stream()
                .map(profissionalMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public ProfissionalResponseDTO atualizarProfissional(Long id, ProfissionalRequestDTO dto) {

        Profissional profissionalExistente = buscarProfissionalExistente(id);

        validarCpfCnsEmail(dto.getCpf(), dto.getCns(), dto.getEmailInstitucional(), id);

        String novoCpf = dto.getCpf();
        String cpfAtual = profissionalExistente.getUsuario().getLogin();

        if (novoCpf != null && !novoCpf.equals(cpfAtual)) {
            usuarioRepository.findByLogin(novoCpf).ifPresent(u -> {
                throw new OperacaoInvalidaException("CPF já cadastrado como login para outro usuário.");
            });
            profissionalExistente.getUsuario().setLogin(novoCpf);
        }

        profissionalMapper.updateEntityFromDTO(dto, profissionalExistente);

        if (dto.getUbsVinculadaId() != null && !dto.getUbsVinculadaId().equals(profissionalExistente.getUbsVinculada().getId())) {
            UnidadeSaude novaUbs = buscarUbsPorId(dto.getUbsVinculadaId());
            profissionalExistente.setUbsVinculada(novaUbs);
        }

        Profissional atualizado = profissionalRepository.save(profissionalExistente);
        return profissionalMapper.toResponseDTO(atualizado);
    }

    @Transactional
    public void deletarProfissional(Long id) {
        Profissional profissional = buscarProfissionalExistente(id);
        profissionalRepository.delete(profissional);
    }


    private Profissional buscarProfissionalExistente(Long id) {
        return profissionalRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Profissional não encontrado com ID: " + id));
    }

    private UnidadeSaude buscarUbsPorId(Long id) {
        return unidadeSaudeRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Unidade de Saúde não encontrada com ID: " + id));
    }

    @Transactional(readOnly = true)
    public List<ProfissionalResponseDTO> buscarPorTipo(String tipo, String termo) {
        List<Profissional> profissionais;

        if (termo == null || termo.isBlank()) {
            return listarTodos();
        }

        switch (tipo.toUpperCase()) {
            case "NOME":
                profissionais = profissionalRepository.findByNomeCompletoContainingIgnoreCase(termo);
                break;
            case "CPF":
                // Remove pontos e traços se o front mandar formatado
                String cpfLimpo = termo.replaceAll("\\D", "");
                profissionais = profissionalRepository.findByCpfContaining(cpfLimpo);
                break;
            case "CNS":
                // Remove qualquer caractere não numérico
                String cnsLimpo = termo.replaceAll("\\D", "");
                profissionais = profissionalRepository.findByCnsContaining(cnsLimpo);
                break;
            default:
                // Se mandar um tipo inválido, retorna lista vazia ou lança erro
                return List.of();
        }

        return profissionais.stream()
                .map(profissionalMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    private void validarCpfCnsEmail(String cpf, String cns, String email, Long profissionalId) {
        profissionalRepository.findByCpf(cpf).ifPresent(p -> {
            if (profissionalId == null || !p.getId().equals(profissionalId)) {
                throw new OperacaoInvalidaException("CPF já cadastrado para outro profissional.");
            }
        });

        profissionalRepository.findByCns(cns).ifPresent(p -> {
            if (profissionalId == null || !p.getId().equals(profissionalId)) {
                throw new OperacaoInvalidaException("CNS já cadastrado para outro profissional.");
            }
        });

        profissionalRepository.findByEmailInstitucional(email).ifPresent(p -> {
            if (profissionalId == null || !p.getId().equals(profissionalId)) {
                throw new OperacaoInvalidaException("Email institucional já cadastrado para outro profissional.");
            }
        });
    }

    @Transactional
    public ProfissionalResponseDTO atualizarDisponibilidade(Long id, DisponibilidadeDTO dto) {
        Profissional profissional = buscarProfissionalExistente(id);

        if (dto.getDiasTrabalho() != null) {
            profissional.setDiasTrabalho(dto.getDiasTrabalho());
        }
        if (dto.getHoraInicio() != null) {
            profissional.setHoraInicio(dto.getHoraInicio());
        }
        if (dto.getHoraFim() != null) {
            profissional.setHoraFim(dto.getHoraFim());
        }
        if (dto.getDisponivelAtualmente() != null) {
            profissional.setDisponivelAtualmente(dto.getDisponivelAtualmente());
        }

        Profissional atualizado = profissionalRepository.save(profissional);
        return profissionalMapper.toResponseDTO(atualizado);
    }
}