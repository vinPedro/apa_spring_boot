package apa.ifpb.edu.br.APA.controller;

import apa.ifpb.edu.br.APA.dto.ProfissionalRequestDTO;
import apa.ifpb.edu.br.APA.dto.ProfissionalResponseDTO;
import apa.ifpb.edu.br.APA.service.ProfissionalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/profissionais")
public class ProfissionalController {

    @Autowired
    private ProfissionalService profissionalService;

    @PostMapping
    public ResponseEntity<ProfissionalResponseDTO> criarProfissional(@RequestBody ProfissionalRequestDTO dto) {
        ProfissionalResponseDTO novoProfissional = profissionalService.criarProfissional(dto);
        return new ResponseEntity<>(novoProfissional, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProfissionalResponseDTO> buscarProfissionalPorId(@PathVariable Long id) {
        ProfissionalResponseDTO profissional = profissionalService.buscarPorId(id);
        return ResponseEntity.ok(profissional);
    }

    // Endpoint: GET /api/profissionais/buscar?tipo=NOME&termo=João
    @GetMapping("/buscar")
    public ResponseEntity<List<ProfissionalResponseDTO>> buscar(
            @RequestParam String tipo,
            @RequestParam String termo) {

        List<ProfissionalResponseDTO> resultados = profissionalService.buscarPorTipo(tipo, termo);
        return ResponseEntity.ok(resultados);
    }

    @GetMapping
    public ResponseEntity<List<ProfissionalResponseDTO>> listarTodosProfissionais() {
        List<ProfissionalResponseDTO> profissionais = profissionalService.listarTodos();
        return ResponseEntity.ok(profissionais);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProfissionalResponseDTO> atualizarProfissional(
            @PathVariable Long id,
            @RequestBody ProfissionalRequestDTO dto) {

        ProfissionalResponseDTO atualizado = profissionalService.atualizarProfissional(id, dto);
        return ResponseEntity.ok(atualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarProfissional(@PathVariable Long id) {
        profissionalService.deletarProfissional(id);
        return ResponseEntity.noContent().build();
    }
}