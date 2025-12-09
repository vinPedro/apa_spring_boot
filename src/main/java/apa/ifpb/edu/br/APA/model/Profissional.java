package apa.ifpb.edu.br.APA.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.br.CPF;
import jakarta.validation.constraints.Email;

import java.time.LocalTime;
import java.util.Set;

@Entity
@Table(name = "profissional", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"cpf"}),
        @UniqueConstraint(columnNames = {"cns"}),
        @UniqueConstraint(columnNames = {"emailInstitucional"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Profissional {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nomeCompleto;

    @CPF(message = "CPF inválido")
    @Column(nullable = false, unique = true)
    private String cpf;

    @Column(nullable = false, unique = true, length = 15)
    private String cns;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ConselhoProfissional conselhoProfissional;

    @Column(nullable = false)
    private String registroConselho;

    @Column(nullable = false, length = 2)
    private String ufConselho;

    @ManyToOne
    @JoinColumn(name = "ubs_id", nullable = false)
    private UnidadeSaude ubsVinculada;

    @Email(message = "Formato de email inválido.")
    @Column(nullable = false, unique = true)
    private String emailInstitucional;

    private String telefoneContato;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "usuario_id", referencedColumnName = "id")
    private Usuario usuario;

    @ElementCollection(targetClass = DiaSemana.class, fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "profissional_dias_trabalho", joinColumns = @JoinColumn(name = "profissional_id"))
    @Column(name = "dia_semana")
    private Set<DiaSemana> diasTrabalho;

    private LocalTime horaInicio;

    private LocalTime horaFim;

    @Column(nullable = false)
    private boolean disponivelAtualmente = false;
}