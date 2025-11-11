package apa.ifpb.edu.br.APA.repository;

import apa.ifpb.edu.br.APA.model.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AdminRepository extends JpaRepository<Admin, Long> {
    @Query("SELECT a FROM Admin a WHERE a.usuario.login = :login")
    Optional<Admin> findByUsuarioLogin(String login);
}
