package apa.ifpb.edu.br.APA.controller;

import apa.ifpb.edu.br.APA.dto.FichamentoDTO;
import apa.ifpb.edu.br.APA.service.FichamentoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/fichamentos")
@RequiredArgsConstructor
public class FichamentoController {

    private final FichamentoService fichamentoService;

    @PostMapping
    public ResponseEntity<FichamentoDTO> criarFichamento(@RequestBody @Valid FichamentoDTO dto) {
        FichamentoDTO novo = fichamentoService.realizarFichamento(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(novo);
    }
}