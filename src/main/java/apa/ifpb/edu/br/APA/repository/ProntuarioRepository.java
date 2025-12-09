package apa.ifpb.edu.br.APA.repository;

import apa.ifpb.edu.br.APA.model.Prontuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProntuarioRepository extends JpaRepository<Prontuario, Long> {

    Optional<Prontuario> findByAtendimentoId(Long atendimentoId);

    @Query("SELECT p FROM Prontuario p WHERE " +
            "(:cpfPaciente IS NULL OR p.atendimento.paciente.cpf = :cpfPaciente) AND " +
            "(:medicoId IS NULL OR p.atendimento.medico.id = :medicoId) AND " +
            "(cast(:dataInicio as timestamp) IS NULL OR p.dataHoraFinalizacao >= :dataInicio) AND " +
            "(cast(:dataFim as timestamp) IS NULL OR p.dataHoraFinalizacao <= :dataFim) " +
            "ORDER BY p.dataHoraFinalizacao DESC")
    List<Prontuario> buscarHistorico(String cpfPaciente, Long medicoId, LocalDateTime dataInicio, LocalDateTime dataFim);

}