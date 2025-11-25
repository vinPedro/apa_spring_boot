package apa.ifpb.edu.br.APA.controller;

import apa.ifpb.edu.br.APA.dto.PacienteRequestDTO;
import apa.ifpb.edu.br.APA.dto.PacienteResponseDTO;
import apa.ifpb.edu.br.APA.model.RacaCor;
import apa.ifpb.edu.br.APA.model.Sexo;
import apa.ifpb.edu.br.APA.repository.UsuarioRepository;
import apa.ifpb.edu.br.APA.security.SecurityConfig;
import apa.ifpb.edu.br.APA.security.SecurityFilter;
import apa.ifpb.edu.br.APA.security.TokenService;
import apa.ifpb.edu.br.APA.service.PacienteService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PacienteController.class)
@Import({SecurityConfig.class, SecurityFilter.class})
class PacienteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PacienteService pacienteService;

    @MockitoBean
    private TokenService tokenService;

    @MockitoBean
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private PacienteRequestDTO requestDTO;
    private PacienteResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        requestDTO = new PacienteRequestDTO();
        requestDTO.setNomeCompleto("Paciente Teste");
        requestDTO.setCpf("123.456.789-00");
        requestDTO.setCns("123456789012345");
        requestDTO.setDataNascimento(LocalDate.of(1990, 1, 1));
        requestDTO.setSexo(Sexo.MASCULINO);
        requestDTO.setRacacor(RacaCor.PARDA);
        requestDTO.setEmail("paciente@teste.com");
        requestDTO.setTelefone("83999998888");
        requestDTO.setCep("58000000");
        requestDTO.setLogradouro("Rua Teste");
        requestDTO.setBairro("Bairro Teste");
        requestDTO.setMunicipio("Cidade Teste");
        requestDTO.setUf("PB");
        requestDTO.setSenha("senha123");
        requestDTO.setUnidadeSaudeId(1L);

        responseDTO = new PacienteResponseDTO();
        responseDTO.setId(1L);
        responseDTO.setNomeCompleto("Paciente Teste");
        responseDTO.setCpf("123.456.789-00");
        responseDTO.setCns("123456789012345");
        responseDTO.setDataNascimento(LocalDate.of(1990, 1, 1));
        responseDTO.setSexo(Sexo.MASCULINO);
        responseDTO.setRacacor(RacaCor.PARDA);
        responseDTO.setEmail("paciente@teste.com");
        responseDTO.setUnidadeSaudeId(1L);
    }

    @Test
    void deveCriarPacientePublicamente() throws Exception {
        when(pacienteService.criarPaciente(any(PacienteRequestDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(post("/api/pacientes")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.nomeCompleto").value("Paciente Teste"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deveBuscarPacientePorId() throws Exception {
        when(pacienteService.buscarPorId(1L)).thenReturn(responseDTO);

        mockMvc.perform(get("/api/pacientes/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.nomeCompleto").value("Paciente Teste"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deveBuscarPacientesPorTipoETermo() throws Exception {
        when(pacienteService.buscarPorTipo("NOME", "Teste")).thenReturn(List.of(responseDTO));

        mockMvc.perform(get("/api/pacientes/buscar")
                        .param("tipo", "NOME")
                        .param("termo", "Teste"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].nomeCompleto").value("Paciente Teste"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deveListarTodosPacientes() throws Exception {
        when(pacienteService.listarTodos()).thenReturn(List.of(responseDTO));

        mockMvc.perform(get("/api/pacientes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deveAtualizarPaciente() throws Exception {
        when(pacienteService.atualizarPaciente(eq(1L), any(PacienteRequestDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(put("/api/pacientes/{id}", 1L)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.nomeCompleto").value("Paciente Teste"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deveDeletarPaciente() throws Exception {
        doNothing().when(pacienteService).deletarPaciente(1L);

        mockMvc.perform(delete("/api/pacientes/{id}", 1L)
                        .with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(roles = "PROFISSIONAL")
    void deveRetornar403QuandoSemPermissao() throws Exception {
        mockMvc.perform(get("/api/pacientes"))
                .andExpect(status().isForbidden());
    }
}