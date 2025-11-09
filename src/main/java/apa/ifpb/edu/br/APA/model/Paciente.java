package apa.ifpb.edu.br.APA.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.br.CPF;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "paciente", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"cns"}),
        @UniqueConstraint(columnNames = {"cpf"})
})
public class Paciente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Cartão Nacional de Saúde
    @Column(length = 15, nullable = false, unique = true)
    private String cns;

    @CPF(message = "CPF inválido")
    private String cpf;

    @Column(nullable = false)
    private String nomeCompleto;

    @Column(nullable = false)
    private LocalDate dataNascimento;

    @Enumerated(EnumType.STRING)
    private Sexo sexo;

    @Enumerated(EnumType.STRING)
    private RacaCor racaCor;

    private int telefone;

    @Email(message ="Formato de email inválido.")
    private String email;

    @ManyToOne
    @JoinColumn(name = "ubs_id", nullable = false)
    private UnidadeSaude unidadeSaudeVinculada;

    @Column(nullable = false)
    private String senha;

    @Column(nullable = false)
    private String logradouro;

    @Column(nullable = false)
    private String bairro;

    @Column(nullable = false)
    private String municipio;

    @Column(nullable = false, length = 2)
    private String uf;

}
