// Caminho: src/main/java/apa/ifpb/edu/br/APA/controller/UnidadeSaudeController.java
package apa.ifpb.edu.br.APA.controller;

import apa.ifpb.edu.br.APA.dto.UnidadeSaudeDTO;
import apa.ifpb.edu.br.APA.service.UnidadeSaudeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/unidades")
@RequiredArgsConstructor
public class UnidadeSaudeController {

    private final UnidadeSaudeService unidadeSaudeService;

    // GET /api/unidades
    // Endpoint para listar todas as unidades de saúde cadastradas.
    @GetMapping
    public ResponseEntity<List<UnidadeSaudeDTO>> listarTodas() {
        return ResponseEntity.ok(unidadeSaudeService.listarTodas());
    }

    // GET /api/unidades/{id}
    // Endpoint para buscar uma unidade de saúde específica pelo seu ID.
    @GetMapping("/{id}")
    public ResponseEntity<UnidadeSaudeDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(unidadeSaudeService.buscarPorId(id));
    }

    // POST /api/unidades
    // Endpoint para cadastrar uma nova unidade de saúde no sistema.
    @PostMapping
    public ResponseEntity<UnidadeSaudeDTO> criarUnidade(@RequestBody UnidadeSaudeDTO dto) {
        UnidadeSaudeDTO novaUnidade = unidadeSaudeService.criarUnidade(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(novaUnidade);
    }

    // PUT /api/unidades/{id}
    // Endpoint para atualizar os dados de uma unidade de saúde existente.
    @PutMapping("/{id}")
    public ResponseEntity<UnidadeSaudeDTO> atualizarUnidade(@PathVariable Long id, @RequestBody UnidadeSaudeDTO dto) {
        UnidadeSaudeDTO unidadeAtualizada = unidadeSaudeService.atualizarUnidade(id, dto);
        return ResponseEntity.ok(unidadeAtualizada);
    }

    // DELETE /api/unidades/{id}
    // Endpoint para deletar uma unidade de saúde do sistema pelo seu ID.
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarUnidade(@PathVariable Long id) {
        unidadeSaudeService.deletarUnidade(id);
        return ResponseEntity.noContent().build();
    }
}