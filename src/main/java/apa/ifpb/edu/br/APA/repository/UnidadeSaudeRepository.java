package apa.ifpb.edu.br.APA.repository;

import apa.ifpb.edu.br.APA.model.UnidadeSaude;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UnidadeSaudeRepository extends JpaRepository<UnidadeSaude, Long> {

    // Métodos para verificar duplicatas antes de salvar
    Optional<UnidadeSaude> findByCodigoCnes(String codigoCnes);
    Optional<UnidadeSaude> findByCnpj(String cnpj);
}