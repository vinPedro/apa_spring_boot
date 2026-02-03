package apa.ifpb.edu.br.APA.controller;

import apa.ifpb.edu.br.APA.dto.ExameRequestDTO;
import apa.ifpb.edu.br.APA.dto.ExameResponseDTO;
import apa.ifpb.edu.br.APA.service.ExameService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/exames")
public class ExameController {

    private final ExameService exameService;

    public ExameController(ExameService exameService) {
        this.exameService = exameService;
    }

    @PostMapping
    public ResponseEntity<ExameResponseDTO> cadastrarExame(@RequestBody ExameRequestDTO dto) {
        ExameResponseDTO novoExame = exameService.criarExame(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(novoExame);
    }

    @GetMapping
    public ResponseEntity<List<ExameResponseDTO>> listarExames() {
        return ResponseEntity.ok(exameService.listarTodos());
    }
}