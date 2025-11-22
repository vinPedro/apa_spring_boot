package apa.ifpb.edu.br.APA.repository;

import apa.ifpb.edu.br.APA.model.Profissional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProfissionalRepository extends JpaRepository<Profissional, Long> {

    Optional<Profissional> findByCpf(String cpf);
    Optional<Profissional> findByCns(String cns);
    Optional<Profissional> findByEmailInstitucional(String email);
    boolean existsByUbsVinculadaId(Long ubsVinculadaId);

    //Buscar por parte do nome
    List<Profissional> findByNomeCompletoContainingIgnoreCase(String nome);

    //Buscar por parte do CPF
    List<Profissional> findByCpfContaining(String cpf);

    //Buscar por parte do CNS
    List<Profissional> findByCnsContaining(String cns);
}