package apa.ifpb.edu.br.APA.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "tb_fichamentos")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Fichamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "atendimento_id", nullable = false, unique = true)
    private Atendimento atendimento;

    private Double peso; // em kg
    private Double altura; // em metros
    private String pressaoArterial; // ex: 12/8
    private Double temperatura; // em graus celsius

    @Column(columnDefinition = "TEXT")
    private String sintomas;

    @Column(columnDefinition = "TEXT")
    private String observacao;

    private LocalDateTime dataHora;
}