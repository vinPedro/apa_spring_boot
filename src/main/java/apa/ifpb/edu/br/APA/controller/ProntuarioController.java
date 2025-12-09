package apa.ifpb.edu.br.APA.controller;

import apa.ifpb.edu.br.APA.dto.ProntuarioRequestDTO;
import apa.ifpb.edu.br.APA.dto.ProntuarioResponseDTO;
import apa.ifpb.edu.br.APA.service.ProntuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
}