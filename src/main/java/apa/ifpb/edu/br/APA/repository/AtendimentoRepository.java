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
}