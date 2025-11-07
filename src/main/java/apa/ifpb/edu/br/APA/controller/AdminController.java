package apa.ifpb.edu.br.APA.controller;

import apa.ifpb.edu.br.APA.dto.AdminResponseDTO;
import apa.ifpb.edu.br.APA.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController()
@RequestMapping("/api/admins")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;
    //Endpoint responsavel por listar todos os adm.

    @GetMapping
    public ResponseEntity<List<AdminResponseDTO>> listarTodosAdmins() {
        List<AdminResponseDTO> admins = adminService.listarTodos();
        return ResponseEntity.ok(admins);
    }

    //Endpoint para buscar um adm pelo Id.

    @GetMapping("/{id}")
    public ResponseEntity<AdminResponseDTO> buscarAdminPorId(@PathVariable Long id) {
        AdminResponseDTO admin = adminService.buscarPorId(id);
        return ResponseEntity.ok(admin);
    }

    //Endpoint para buscar um adm pelo login.
    @GetMapping("/login/{login}")
    public ResponseEntity<AdminResponseDTO> buscarAdminPorLogin(@PathVariable int login) {
        AdminResponseDTO admin = adminService.buscarPorLogin(login);
        return ResponseEntity.ok(admin);
    }

}
