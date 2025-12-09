package apa.ifpb.edu.br.APA.controller;

import apa.ifpb.edu.br.APA.dto.EspecialidadeDTO;
import apa.ifpb.edu.br.APA.service.EspecialidadeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/especialidades")
@RequiredArgsConstructor
public class EspecialidadeController {

    private final EspecialidadeService especialidadeService;

   //endpoint que lista todas as especialidades cadastradas
    @GetMapping
    public ResponseEntity<List<EspecialidadeDTO>> listarTodas() {
        return ResponseEntity.ok(especialidadeService.listarTodas());
    }

  //endpoint que retorna especialidade com id especifico
    @GetMapping("/{id}")
    public ResponseEntity<EspecialidadeDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(especialidadeService.buscarPorId(id));
    }

    //endpoint que retorna especialidade com nome especifico
    @GetMapping("/nome/{nome}")
    public ResponseEntity<EspecialidadeDTO> buscarPorNome(@PathVariable String nome) {
        return ResponseEntity.ok(especialidadeService.buscarPorNome(nome));
    }

    //endpoint que cria especialidade
    @PostMapping
    public ResponseEntity<EspecialidadeDTO> criarEspecialidade(@RequestBody EspecialidadeDTO dto) {
        EspecialidadeDTO novaEspecialidade = especialidadeService.criarEspecialidade(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(novaEspecialidade);
    }

    //endpoint que atualiza especialidade
    @PutMapping("/{id}")
    public ResponseEntity<EspecialidadeDTO> atualizarEspecialidade(@PathVariable Long id, @RequestBody EspecialidadeDTO dto) {
        EspecialidadeDTO especialidadeAtualizada = especialidadeService.atualizarEspecialidade(id, dto);
        return ResponseEntity.ok(especialidadeAtualizada);
    }

    //endpoint que deleta especialidade
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarEspecialidade(@PathVariable Long id) {
        especialidadeService.deletarEspecialidade(id);
        return ResponseEntity.noContent().build();
    }
}