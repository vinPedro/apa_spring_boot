package apa.ifpb.edu.br.APA.dto;

import apa.ifpb.edu.br.APA.model.Admin;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminResponseDTO {
    private Long id;
    private int login;

    public AdminResponseDTO(Admin admin) {
        this.id = admin.getId();
        this.login = admin.getLogin();
    }
}
