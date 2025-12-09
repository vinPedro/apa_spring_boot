package apa.ifpb.edu.br.APA.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
//como nao tem dado sensivel, pode ser apenas 1 dto
public class EspecialidadeDTO {
    private Long id;
    private String nome;
    private String descricao; // Opcional
}
