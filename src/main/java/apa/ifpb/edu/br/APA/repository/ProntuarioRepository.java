package apa.ifpb.edu.br.APA.repository;

import apa.ifpb.edu.br.APA.model.Prontuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProntuarioRepository extends JpaRepository<Prontuario, Long> {
    Optional<Prontuario> findByAtendimentoId(Long atendimentoId);
}