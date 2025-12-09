package apa.ifpb.edu.br.APA.repository;

import apa.ifpb.edu.br.APA.model.Atendimento;
import apa.ifpb.edu.br.APA.model.StatusAtendimento; // <-- Importante
import apa.ifpb.edu.br.APA.model.TipoPrioridade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AtendimentoRepository extends JpaRepository<Atendimento, Long> {

    // Conta quantos atendimentos de um tipo (P ou PR) existem numa unidade a partir de uma data (hoje)
    @Query("SELECT COUNT(a) FROM Atendimento a WHERE a.unidadeSaude.id = :unidadeId AND a.prioridade = :prioridade AND a.dataHoraChegada >= :inicioDia")
    Long contarAtendimentosDoDia(Long unidadeId, TipoPrioridade prioridade, LocalDateTime inicioDia);

    // CORREÇÃO: Agora aceita StatusAtendimento (Enum) em vez de String
    List<Atendimento> findByUnidadeSaudeIdAndStatus(Long unidadeId, StatusAtendimento status);

    // 1. Busca todos do dia (sem filtro de status)
    // Ordenado por chegada (quem chegou primeiro aparece primeiro)
    List<Atendimento> findByUnidadeSaudeIdAndDataHoraChegadaBetweenOrderByDataHoraChegadaAsc(
            Long unidadeId, LocalDateTime inicio, LocalDateTime fim);

    // 2. Busca do dia filtrando por status (ex: só quem está AGUARDANDO)
    // Ordenado por Prioridade (PRIORIDADE vem antes de NORMAL) e depois por Chegada
    // Isso já deixa a lista pronta visualmente para o médico/recepção
    @Query("SELECT a FROM Atendimento a " +
            "WHERE a.unidadeSaude.id = :unidadeId " +
            "AND a.status = :status " +
            "AND a.dataHoraChegada BETWEEN :inicio AND :fim " +
            "ORDER BY a.prioridade DESC, a.dataHoraChegada ASC")
    List<Atendimento> buscarFilaPorStatus(
            Long unidadeId, StatusAtendimento status, LocalDateTime inicio, LocalDateTime fim);
}