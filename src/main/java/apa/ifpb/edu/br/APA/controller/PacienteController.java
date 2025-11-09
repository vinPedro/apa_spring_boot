package apa.ifpb.edu.br.APA.controller;

import apa.ifpb.edu.br.APA.dto.PacienteRequestDTO;
import apa.ifpb.edu.br.APA.dto.PacienteResponseDTO;
import apa.ifpb.edu.br.APA.service.PacienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pacientes")
public class PacienteController {

    @Autowired
    private PacienteService pacienteService;

    @PostMapping
    public ResponseEntity<PacienteResponseDTO> criarPaciente(@RequestBody PacienteRequestDTO dto) {
        PacienteResponseDTO novoPaciente = pacienteService.criarPaciente(dto);
        return new ResponseEntity<>(novoPaciente, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PacienteResponseDTO> buscarPacientePorId(@PathVariable Long id) {
        PacienteResponseDTO paciente = pacienteService.buscarPorId(id);
        return ResponseEntity.ok(paciente);
    }


    @GetMapping
    public ResponseEntity<List<PacienteResponseDTO>> listarTodosPacientes() {
        List<PacienteResponseDTO> pacientes = pacienteService.listarTodos();
        return ResponseEntity.ok(pacientes);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PacienteResponseDTO> atualizarPaciente(
            @PathVariable Long id,
            @RequestBody PacienteRequestDTO dto) {

        PacienteResponseDTO pacienteAtualizado = pacienteService.atualizarPaciente(id, dto);
        return ResponseEntity.ok(pacienteAtualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarPaciente(@PathVariable Long id) {
        pacienteService.deletarPaciente(id);
        return ResponseEntity.noContent().build();
    }
}