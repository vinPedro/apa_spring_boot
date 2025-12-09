// Caminho: src/main/java/apa/ifpb/edu/br/APA/controller/PainelController.java
package apa.ifpb.edu.br.APA.controller;

import apa.ifpb.edu.br.APA.dto.PainelResponseDTO;
import apa.ifpb.edu.br.APA.service.AtendimentoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/painel")
@RequiredArgsConstructor
public class PainelController {

    private final AtendimentoService atendimentoService;

    // GET /api/painel/atual?unidadeId=1
    @GetMapping("/atual")
    public ResponseEntity<PainelResponseDTO> getPainelAtual(@RequestParam Long unidadeId) {
        PainelResponseDTO dados = atendimentoService.getDadosPainel(unidadeId);
        return ResponseEntity.ok(dados);
    }

    @PostMapping("/proxima")
    public ResponseEntity<PainelResponseDTO> chamarProximaSenha(@RequestParam Long unidadeId) {
        atendimentoService.chamarProxima(unidadeId);
        return ResponseEntity.ok().build();
    }
}