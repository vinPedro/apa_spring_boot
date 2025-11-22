package apa.ifpb.edu.br.APA.controller;

import apa.ifpb.edu.br.APA.dto.AdminRequestDTO;
import apa.ifpb.edu.br.APA.service.SetupService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Map;

@RestController
@RequestMapping("/api/setup")
@RequiredArgsConstructor
public class SetupController {

    private final SetupService setupService;

    @GetMapping("/status")
    public ResponseEntity<Map<String, Boolean>> verificarStatus() {
        boolean inicializado = setupService.isSistemaInicializado();
        return ResponseEntity.ok(Collections.singletonMap("inicializado", inicializado));
    }

    @PostMapping("/criar-admin")
    public ResponseEntity<Void> criarPrimeiroAdmin(@RequestBody @Valid AdminRequestDTO dto) {
        setupService.criarPrimeiroAdmin(dto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}