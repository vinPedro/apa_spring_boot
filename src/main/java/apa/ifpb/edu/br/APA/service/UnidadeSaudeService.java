package apa.ifpb.edu.br.APA.service;

import apa.ifpb.edu.br.APA.dto.UnidadeSaudeDTO;
import apa.ifpb.edu.br.APA.exception.OperacaoInvalidaException;
import apa.ifpb.edu.br.APA.exception.RecursoJaExisteException;
import apa.ifpb.edu.br.APA.exception.RecursoNaoEncontradoException;
import apa.ifpb.edu.br.APA.mapper.UnidadeSaudeMapper;
import apa.ifpb.edu.br.APA.model.UnidadeSaude;
import apa.ifpb.edu.br.APA.repository.PacienteRepository;
import apa.ifpb.edu.br.APA.repository.ProfissionalRepository;
import apa.ifpb.edu.br.APA.repository.UnidadeSaudeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UnidadeSaudeService {

    private final UnidadeSaudeRepository unidadeSaudeRepository;
    private final UnidadeSaudeMapper unidadeSaudeMapper;
    @Autowired
    private ProfissionalRepository profissionalRepository;
    @Autowired
    private PacienteRepository pacienteRepository;

    // Busca todas as unidades no banco e as converte para DTO.
    @Transactional(readOnly = true)
    public List<UnidadeSaudeDTO> listarTodas() {
        return unidadeSaudeRepository.findAll()
                .stream()
                .map(unidadeSaudeMapper::toDTO)
                .collect(Collectors.toList());
    }

    // Busca uma unidade específica pelo ID. Lança exceção se não encontrar.
    @Transactional(readOnly = true)
    public UnidadeSaudeDTO buscarPorId(Long id) {
        UnidadeSaude unidade = unidadeSaudeRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Unidade de Saúde não encontrada com id: " + id));
        return unidadeSaudeMapper.toDTO(unidade);
    }

    // Cria uma nova unidade. Antes de salvar, valida se já existe CNEs ou CNPJ igual.
    @Transactional
    public UnidadeSaudeDTO criarUnidade(UnidadeSaudeDTO dto) {
        // Validação (seguindo o padrão do EspecialidadeService)
        unidadeSaudeRepository.findByCodigoCnes(dto.getCodigoCnes()).ifPresent(u -> {
            throw new RecursoJaExisteException("Já existe uma unidade com o CNES: " + dto.getCodigoCnes());
        });
        unidadeSaudeRepository.findByCnpj(dto.getCnpj()).ifPresent(u -> {
            throw new RecursoJaExisteException("Já existe uma unidade com o CNPJ: " + dto.getCnpj());
        });

        UnidadeSaude unidade = unidadeSaudeMapper.toUnidadeSaude(dto);
        UnidadeSaude salva = unidadeSaudeRepository.save(unidade);
        return unidadeSaudeMapper.toDTO(salva);
    }

    // Atualiza uma unidade existente. Busca pelo ID e aplica as mudanças do DTO.
    @Transactional
    public UnidadeSaudeDTO atualizarUnidade(Long id, UnidadeSaudeDTO dto) {
        UnidadeSaude unidade = unidadeSaudeRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Unidade de Saúde não encontrada com id: " + id));

        if (dto.getCodigoCnes() != null && !dto.getCodigoCnes().equals(unidade.getCodigoCnes())) {
            unidadeSaudeRepository.findByCodigoCnes(dto.getCodigoCnes()).ifPresent(u -> {
                throw new RecursoJaExisteException("Já existe outra unidade com o CNES: " + dto.getCodigoCnes());
            });
        }
        if (dto.getCnpj() != null && !dto.getCnpj().equals(unidade.getCnpj())) {
            unidadeSaudeRepository.findByCnpj(dto.getCnpj()).ifPresent(u -> {
                throw new RecursoJaExisteException("Já existe outra unidade com o CNPJ: " + dto.getCnpj());
            });
        }

        unidadeSaudeMapper.updateFromDto(dto, unidade);
        UnidadeSaude atualizada = unidadeSaudeRepository.save(unidade);
        return unidadeSaudeMapper.toDTO(atualizada);
    }
    //deleta unidade de saude
    @Transactional
    public void deletarUnidade(Long id) {
        UnidadeSaude unidade = unidadeSaudeRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Unidade de Saúde não encontrada com id: " + id));

        if (pacienteRepository.existsByUnidadeSaudeVinculadaId(id)) {
            throw new OperacaoInvalidaException("Não é possível excluir a Unidade de Saúde pois existem pacientes vinculados a ela.");
        }
        if (profissionalRepository.existsByUbsVinculadaId(id)) {
            throw new OperacaoInvalidaException("Não é possível excluir a Unidade de Saúde pois existem profissionais vinculados a ela.");
        }
        unidadeSaudeRepository.delete(unidade);
    }
}