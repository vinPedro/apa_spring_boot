package apa.ifpb.edu.br.APA.repository;

import apa.ifpb.edu.br.APA.model.Paciente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PacienteRepository extends JpaRepository<Paciente, Long> {

    Optional<Paciente> findByCpf(String cpf);

    Optional<Paciente> findByEmail(String email);

    Optional<Paciente> findByCns(String cns);

    boolean existsByUnidadeSaudeVinculadaId(Long unidadeSaudeId);
}