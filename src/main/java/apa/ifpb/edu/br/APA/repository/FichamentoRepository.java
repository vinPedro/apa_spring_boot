package apa.ifpb.edu.br.APA.repository;

import apa.ifpb.edu.br.APA.model.Fichamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FichamentoRepository extends JpaRepository<Fichamento, Long> {
    Optional<Fichamento> findByAtendimentoId(Long atendimentoId);
}