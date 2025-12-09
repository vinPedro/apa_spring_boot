package apa.ifpb.edu.br.APA.repository;

import apa.ifpb.edu.br.APA.model.Paciente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PacienteRepository extends JpaRepository<Paciente, Long> {

    Optional<Paciente> findByCpf(String cpf);
    Optional<Paciente> findByEmail(String email);
    Optional<Paciente> findByCns(String cns);
    boolean existsByUnidadeSaudeVinculadaId(Long unidadeSaudeId);

    // Busca por parte do nome
    List<Paciente> findByNomeCompletoContainingIgnoreCase(String nome);

    // Busca por CPF por parte
    List<Paciente> findByCpfContaining(String cpf);

    // Busca por CNS por parte
    List<Paciente> findByCnsContaining(String cns);
}