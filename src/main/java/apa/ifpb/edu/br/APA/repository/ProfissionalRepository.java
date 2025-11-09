package apa.ifpb.edu.br.APA.repository;

import apa.ifpb.edu.br.APA.model.Profissional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProfissionalRepository extends JpaRepository<Profissional, Long> {

    Optional<Profissional> findByCpf(String cpf);
    Optional<Profissional> findByCns(String cns);
    Optional<Profissional> findByEmailInstitucional(String email);
    boolean existsByUbsVinculadaId(Long ubsVinculadaId);
}