// Caminho: src/main/java/apa/ifpb/edu/br/APA/controller/AtendimentoController.java
package apa.ifpb.edu.br.APA.controller;

import apa.ifpb.edu.br.APA.dto.AtendimentoRequestDTO;
import apa.ifpb.edu.br.APA.dto.AtendimentoResponseDTO;
import apa.ifpb.edu.br.APA.dto.ChamarPacienteDTO;
import apa.ifpb.edu.br.APA.dto.DadosConsultaDTO;
import apa.ifpb.edu.br.APA.service.AtendimentoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.util.List;

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

    // POST /api/atendimentos/chamar
    @PostMapping("/chamar")
    public ResponseEntity<AtendimentoResponseDTO> chamarProximo(@RequestBody @Valid ChamarPacienteDTO dto) {
        AtendimentoResponseDTO atendimento = atendimentoService.chamarProximoPaciente(dto);
        return ResponseEntity.ok(atendimento);
    }

    @GetMapping("/{id}/dados-consulta")
    public ResponseEntity<DadosConsultaDTO> getDadosConsulta(@PathVariable Long id) {
        DadosConsultaDTO dados = atendimentoService.buscarDadosParaConsulta(id);
        return ResponseEntity.ok(dados);
    }

    @GetMapping("/fila/{unidadeId}")
    public ResponseEntity<List<AtendimentoResponseDTO>> listarFila(@PathVariable Long unidadeId) {
        List<AtendimentoResponseDTO> fila = atendimentoService.listarFilaPorUnidade(unidadeId);
        return ResponseEntity.ok(fila);
    }

    // GET /api/atendimentos?unidadeSaudeId=1&status=AGUARDANDO
    @GetMapping
    public ResponseEntity<List<AtendimentoResponseDTO>> listarFila(
            @RequestParam Long unidadeSaudeId,
            @RequestParam(required = false) String status) {

        List<AtendimentoResponseDTO> fila = atendimentoService.listarFila(unidadeSaudeId, status);
        return ResponseEntity.ok(fila);
    }

    // PATCH /api/atendimentos/{id}/cancelar
    @PatchMapping("/{id}/cancelar")
    public ResponseEntity<Void> cancelarAtendimento(
            @PathVariable Long id,
            @RequestParam(required = false) String motivo) {

        atendimentoService.cancelarAtendimento(id, motivo);
        return ResponseEntity.noContent().build();
    }
}