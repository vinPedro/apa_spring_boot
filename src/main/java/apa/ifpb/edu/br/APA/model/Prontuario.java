package apa.ifpb.edu.br.APA.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "tb_prontuarios")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Prontuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "atendimento_id", nullable = false, unique = true)
    private Atendimento atendimento;

    @Column(columnDefinition = "TEXT")
    private String queixaPrincipal;

    @Column(columnDefinition = "TEXT")
    private String historicoDoenca;

    @Column(columnDefinition = "TEXT")
    private String exameFisico;

    @Column(columnDefinition = "TEXT")
    private String diagnostico;

    @Column(columnDefinition = "TEXT")
    private String prescricaoMedica;

    @Column(columnDefinition = "TEXT")
    private String examesSolicitados;

    private Integer atestadoDias;

    private LocalDateTime dataHoraFinalizacao;
}