package apa.ifpb.edu.br.APA.controller;

import apa.ifpb.edu.br.APA.dto.AdminRequestDTO;
import apa.ifpb.edu.br.APA.repository.UsuarioRepository;
import apa.ifpb.edu.br.APA.service.SetupService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;


@RestController
@RequestMapping("/api/setup")
@RequiredArgsConstructor

public class SetupController {

    private final SetupService setupService;

    @Autowired
    private UsuarioRepository usuarioRepository;



    @GetMapping("/status")
    public ResponseEntity<Map<String, Boolean>> verificarStatus() {
        long count = usuarioRepository.count();

        boolean adminExists = count > 0;

        Map<String, Boolean> response = new HashMap<>();
        response.put("adminExists", adminExists);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/criar-admin")
    public ResponseEntity<Void> criarPrimeiroAdmin(@RequestBody @Valid AdminRequestDTO dto) {
        setupService.criarPrimeiroAdmin(dto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

}