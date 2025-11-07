package apa.ifpb.edu.br.APA.repository;

import apa.ifpb.edu.br.APA.model.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AdminRepository extends JpaRepository<Admin, Long> {

    Optional<Admin> findByLogin(int login);
}
