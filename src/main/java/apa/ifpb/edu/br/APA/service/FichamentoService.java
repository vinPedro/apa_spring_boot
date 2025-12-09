package apa.ifpb.edu.br.APA.service;

import apa.ifpb.edu.br.APA.dto.FichamentoDTO;
import apa.ifpb.edu.br.APA.exception.OperacaoInvalidaException;
import apa.ifpb.edu.br.APA.exception.RecursoNaoEncontradoException;
import apa.ifpb.edu.br.APA.mapper.FichamentoMapper;
import apa.ifpb.edu.br.APA.model.Atendimento;
import apa.ifpb.edu.br.APA.model.Fichamento;
import apa.ifpb.edu.br.APA.model.StatusAtendimento;
import apa.ifpb.edu.br.APA.repository.AtendimentoRepository;
import apa.ifpb.edu.br.APA.repository.FichamentoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class FichamentoService {

    private final FichamentoRepository fichamentoRepository;
    private final AtendimentoRepository atendimentoRepository;
    private final FichamentoMapper fichamentoMapper;

    @Transactional
    public FichamentoDTO realizarFichamento(FichamentoDTO dto) {

        // 1. Busca o atendimento na fila
        Atendimento atendimento = atendimentoRepository.findById(dto.getAtendimentoId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Atendimento não encontrado"));

        // 2. Valida se o paciente está no status correto (AGUARDANDO)
        if (atendimento.getStatus() != StatusAtendimento.AGUARDANDO) {
            throw new OperacaoInvalidaException("Este atendimento não está aguardando triagem. Status atual: " + atendimento.getStatus());
        }

        // 3. Verifica se já existe fichamento (evita duplicidade)
        if (fichamentoRepository.findByAtendimentoId(atendimento.getId()).isPresent()) {
            throw new OperacaoInvalidaException("Já existe um fichamento para este atendimento.");
        }

        // 4. Cria e Salva o Fichamento
        Fichamento fichamento = fichamentoMapper.toEntity(dto);
        fichamento.setAtendimento(atendimento);
        fichamento.setDataHora(LocalDateTime.now());

        Fichamento salvo = fichamentoRepository.save(fichamento);

        // 5. ATUALIZA A FILA: Move o paciente para a lista do Médico
        atendimento.setStatus(StatusAtendimento.PRONTO_PARA_CONSULTA);
        atendimentoRepository.save(atendimento);

        return fichamentoMapper.toDTO(salvo);
    }
}