package apa.ifpb.edu.br.APA.controller;

import apa.ifpb.edu.br.APA.dto.ProntuarioRequestDTO;
import apa.ifpb.edu.br.APA.dto.ProntuarioResponseDTO;
import apa.ifpb.edu.br.APA.service.ProntuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/prontuarios")
@RequiredArgsConstructor
public class ProntuarioController {

    private final ProntuarioService prontuarioService;

    @PostMapping
    public ResponseEntity<ProntuarioResponseDTO> finalizarAtendimento(@RequestBody @Valid ProntuarioRequestDTO dto) {
        ProntuarioResponseDTO response = prontuarioService.salvarProntuario(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // GET /api/prontuarios?pacienteId=1&medicoId=2...
    @GetMapping
    public ResponseEntity<List<ProntuarioResponseDTO>> pesquisar(
            @RequestParam(required = false) String cpfPaciente,
            @RequestParam(required = false) Long medicoId,
            @RequestParam(required = false) LocalDate dataInicio,
            @RequestParam(required = false) LocalDate dataFim) {

        List<ProntuarioResponseDTO> historico = prontuarioService.buscarHistorico(cpfPaciente, medicoId, dataInicio, dataFim);
        return ResponseEntity.ok(historico);
    }

}