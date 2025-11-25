package apa.ifpb.edu.br.APA.controller;

import apa.ifpb.edu.br.APA.dto.AtendimentoRequestDTO;
import apa.ifpb.edu.br.APA.model.*;
import apa.ifpb.edu.br.APA.repository.PacienteRepository;
import apa.ifpb.edu.br.APA.repository.UnidadeSaudeRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class AtendimentoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PacienteRepository pacienteRepository;

    @Autowired
    private UnidadeSaudeRepository unidadeSaudeRepository;

    private Long pacienteId;
    private Long unidadeId;

    @BeforeEach
    void setup() {
        UnidadeSaude ubs = new UnidadeSaude();
        ubs.setNome("UBS Teste Atendimento");
        ubs.setCodigoCnes("9998887");
        ubs.setCnpj("11222333000199");
        ubs.setCep("58000000");
        ubs.setMunicipio("Campina Grande");
        ubs.setUf("PB");
        ubs.setLogradouro("Rua Teste");

        unidadeId = unidadeSaudeRepository.save(ubs).getId();

        Paciente paciente = new Paciente();
        paciente.setNomeCompleto("Paciente da Silva");

        paciente.setCpf("98765432100");

        paciente.setCns("800000000000008");
        paciente.setDataNascimento(LocalDate.of(1990, 1, 1));
        paciente.setMunicipio("Campina Grande");
        paciente.setUf("PB");
        paciente.setCep("58000000");
        paciente.setLogradouro("Rua do Paciente");
        paciente.setUnidadeSaudeVinculada(ubs);

        pacienteId = pacienteRepository.save(paciente).getId();
    }

    @Test
    @DisplayName("POST /api/atendimentos - Deve criar atendimento com sucesso (ADMIN)")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void criarAtendimento_Sucesso() throws Exception {
        AtendimentoRequestDTO dto = new AtendimentoRequestDTO();
        dto.setPacienteId(pacienteId);
        dto.setUnidadeSaudeId(unidadeId);
        dto.setPrioridade(TipoPrioridade.NORMAL);

        mockMvc.perform(post("/api/atendimentos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.senha").value("P1"))
                .andExpect(jsonPath("$.status").value("AGUARDANDO"));
    }

    @Test
    @DisplayName("POST /api/atendimentos - Deve retornar 404 se Paciente não existe")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void criarAtendimento_PacienteNaoEncontrado() throws Exception {
        AtendimentoRequestDTO dto = new AtendimentoRequestDTO();
        dto.setPacienteId(999999L);
        dto.setUnidadeSaudeId(unidadeId);
        dto.setPrioridade(TipoPrioridade.NORMAL);

        mockMvc.perform(post("/api/atendimentos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("POST /api/atendimentos - Deve retornar 403 Forbidden se não for ADMIN")
    @WithMockUser(username = "recepcao", roles = {"PROFISSIONAL"})
    void criarAtendimento_SemPermissao() throws Exception {
        AtendimentoRequestDTO dto = new AtendimentoRequestDTO();
        dto.setPacienteId(pacienteId);
        dto.setUnidadeSaudeId(unidadeId);
        dto.setPrioridade(TipoPrioridade.NORMAL);

        mockMvc.perform(post("/api/atendimentos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("POST /api/atendimentos - Deve retornar 400 se campos obrigatórios faltarem")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void criarAtendimento_ValidacaoCampos() throws Exception {
        AtendimentoRequestDTO dto = new AtendimentoRequestDTO();

        mockMvc.perform(post("/api/atendimentos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }
}