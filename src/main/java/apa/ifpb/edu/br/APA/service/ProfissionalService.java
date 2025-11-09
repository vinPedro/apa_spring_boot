package apa.ifpb.edu.br.APA.service;

import apa.ifpb.edu.br.APA.dto.ProfissionalRequestDTO;
import apa.ifpb.edu.br.APA.dto.ProfissionalResponseDTO;
import apa.ifpb.edu.br.APA.exception.OperacaoInvalidaException;
import apa.ifpb.edu.br.APA.exception.RecursoNaoEncontradoException;
import apa.ifpb.edu.br.APA.mapper.ProfissionalMapper;
import apa.ifpb.edu.br.APA.model.Profissional;
import apa.ifpb.edu.br.APA.model.UnidadeSaude;
import apa.ifpb.edu.br.APA.repository.ProfissionalRepository;
import apa.ifpb.edu.br.APA.repository.UnidadeSaudeRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProfissionalService {

    @Autowired
    private ProfissionalRepository profissionalRepository;

    @Autowired
    private UnidadeSaudeRepository unidadeSaudeRepository;

    @Autowired
    private ProfissionalMapper profissionalMapper;

    @Transactional
    public ProfissionalResponseDTO criarProfissional(ProfissionalRequestDTO dto) {

        validarCpfCnsEmail(dto.getCpf(), dto.getCns(), dto.getEmailInstitucional(), null);

        Profissional profissional = profissionalMapper.toEntity(dto);

        UnidadeSaude ubs = buscarUbsPorId(dto.getUbsVinculadaId());
        profissional.setUbsVinculada(ubs);

        // ADD CRIPTOGRAFIA
        profissional.setSenha(dto.getSenha());

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

        profissionalMapper.updateEntityFromDTO(dto, profissionalExistente);

        if (!dto.getUbsVinculadaId().equals(profissionalExistente.getUbsVinculada().getId())) {
            UnidadeSaude novaUbs = buscarUbsPorId(dto.getUbsVinculadaId());
            profissionalExistente.setUbsVinculada(novaUbs);
        }

        Profissional atualizado = profissionalRepository.save(profissionalExistente);
        return profissionalMapper.toResponseDTO(atualizado);
    }

    //falta add verificacao
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
}