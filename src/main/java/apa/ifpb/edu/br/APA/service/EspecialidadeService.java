package apa.ifpb.edu.br.APA.service;

import apa.ifpb.edu.br.APA.dto.EspecialidadeDTO;
import apa.ifpb.edu.br.APA.exception.OperacaoInvalidaException;
import apa.ifpb.edu.br.APA.exception.RecursoJaExisteException;
import apa.ifpb.edu.br.APA.exception.RecursoNaoEncontradoException;
import apa.ifpb.edu.br.APA.mapper.EspecialidadeMapper;
import apa.ifpb.edu.br.APA.model.Especialidade;
import apa.ifpb.edu.br.APA.repository.EspecialidadeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EspecialidadeService {

    private final EspecialidadeRepository especialidadeRepository;
    private final EspecialidadeMapper especialidadeMapper;


    //lista todas especialidades cadastradas
    @Transactional(readOnly = true)
    public List<EspecialidadeDTO> listarTodas() {
        return especialidadeRepository.findAll()
                .stream()
                .map(especialidadeMapper::toDTO)
                .collect(Collectors.toList());
    }

    //busca a especialidade por id
    @Transactional(readOnly = true)
    public EspecialidadeDTO buscarPorId(Long id) {
        Especialidade especialidade = especialidadeRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Especialidade não encontrada com id: " + id));
        return especialidadeMapper.toDTO(especialidade);
    }

    //busca a especialidade pelo nome
    @Transactional(readOnly = true)
    public EspecialidadeDTO buscarPorNome(String nome) {
        Especialidade especialidade = especialidadeRepository.findByNome(nome)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Especialidade não encontrada com nome: " + nome));
        return especialidadeMapper.toDTO(especialidade);
    }


    //cria a especialidade
    @Transactional
    public EspecialidadeDTO criarEspecialidade(EspecialidadeDTO dto) {
        especialidadeRepository.findByNome(dto.getNome()).ifPresent(e -> {
            throw new RecursoJaExisteException("Especialidade com nome '" + dto.getNome() + "' já existe.");
        });

        Especialidade especialidade = especialidadeMapper.toEspecialidade(dto);
        Especialidade salva = especialidadeRepository.save(especialidade);
        return especialidadeMapper.toDTO(salva);
    }

    //atualiza a especialidade
    @Transactional
    public EspecialidadeDTO atualizarEspecialidade(Long id, EspecialidadeDTO dto) {
        Especialidade especialidade = especialidadeRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Especialidade não encontrada com id: " + id));

        especialidadeMapper.updateFromDto(dto, especialidade);

        Especialidade atualizada = especialidadeRepository.save(especialidade);
        return especialidadeMapper.toDTO(atualizada);
    }

    //deleta a especialidade
    @Transactional
    public void deletarEspecialidade(Long id) {
        Especialidade especialidade = especialidadeRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Especialidade não encontrada com id: " + id));

        if ("OUTRO".equalsIgnoreCase(especialidade.getNome())) {
            throw new OperacaoInvalidaException("Não é permitido deletar a especialidade padrão 'OUTRO'.");
        }

        especialidadeRepository.delete(especialidade);
    }
}
