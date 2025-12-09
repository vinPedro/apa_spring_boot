package apa.ifpb.edu.br.APA.dto;

import lombok.Data;
import java.util.List;

@Data
public class PainelResponseDTO {
    private String senhaAtual;
    private String guiche;      // Nome da UBS
    private List<String> historico; // As últimas 3 senhas chamadas
}