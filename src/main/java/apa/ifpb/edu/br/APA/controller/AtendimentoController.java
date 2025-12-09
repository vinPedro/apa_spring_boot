// Caminho: src/main/java/apa/ifpb/edu/br/APA/controller/AtendimentoController.java
package apa.ifpb.edu.br.APA.controller;

import apa.ifpb.edu.br.APA.dto.AtendimentoRequestDTO;
import apa.ifpb.edu.br.APA.dto.AtendimentoResponseDTO;
import apa.ifpb.edu.br.APA.service.AtendimentoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/atendimentos")
@RequiredArgsConstructor
public class AtendimentoController {

    private final AtendimentoService atendimentoService;

    // POST /api/atendimentos
    // Endpoint para colocar um paciente na fila de espera
    @PostMapping
    public ResponseEntity<AtendimentoResponseDTO> entrarNaFila(@RequestBody @Valid AtendimentoRequestDTO dto) {
        AtendimentoResponseDTO novoAtendimento = atendimentoService.entrarNaFila(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(novoAtendimento);
    }
}