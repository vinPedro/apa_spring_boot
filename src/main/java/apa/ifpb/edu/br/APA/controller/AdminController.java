package apa.ifpb.edu.br.APA.controller;

import apa.ifpb.edu.br.APA.dto.AdminRequestDTO;
import apa.ifpb.edu.br.APA.dto.AdminResponseDTO;
import apa.ifpb.edu.br.APA.service.AdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController()
@RequestMapping("/api/admins")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    //Endpoint responsavel por criar um novo adm
    @PostMapping
    public ResponseEntity<AdminResponseDTO> criarNovoAdmin(@RequestBody @Valid AdminRequestDTO dto) {
        AdminResponseDTO novoAdmin = adminService.criarAdmin(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(novoAdmin);
    }

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
    public ResponseEntity<AdminResponseDTO> buscarAdminPorLogin(@PathVariable String login) {
        AdminResponseDTO admin = adminService.buscarPorLogin(login);
        return ResponseEntity.ok(admin);
    }

}
