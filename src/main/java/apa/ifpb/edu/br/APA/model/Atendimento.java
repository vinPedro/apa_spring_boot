package apa.ifpb.edu.br.APA.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "tb_atendimentos")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Atendimento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //(Ex: P1, PR5)
    @Column(nullable = false)
    private String senha;

    @Column(nullable = false)
    private LocalDateTime dataHoraChegada;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusAtendimento status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoPrioridade prioridade;

    @ManyToOne
    @JoinColumn(name = "paciente_id", nullable = false)
    private Paciente paciente;

    @ManyToOne
    @JoinColumn(name = "unidade_saude_id", nullable = false)
    private UnidadeSaude unidadeSaude;

    @ManyToOne
    @JoinColumn(name = "medico_id")
    private Profissional medico;

    private LocalDateTime dataHoraChamada;
}