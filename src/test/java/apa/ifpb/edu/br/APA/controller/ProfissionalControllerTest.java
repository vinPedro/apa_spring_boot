package apa.ifpb.edu.br.APA.controller;

import apa.ifpb.edu.br.APA.dto.ProfissionalRequestDTO;
import apa.ifpb.edu.br.APA.dto.ProfissionalResponseDTO;
import apa.ifpb.edu.br.APA.model.ConselhoProfissional;
import apa.ifpb.edu.br.APA.service.ProfissionalService;
import apa.ifpb.edu.br.APA.security.TokenService;
import apa.ifpb.edu.br.APA.repository.UsuarioRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProfissionalController.class)
class ProfissionalControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProfissionalService profissionalService;

    @MockitoBean
    private TokenService tokenService;

    @MockitoBean
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private ProfissionalRequestDTO requestDTO;
    private ProfissionalResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        requestDTO = new ProfissionalRequestDTO();
        requestDTO.setNomeCompleto("Dr. Teste");
        requestDTO.setCpf("11122233344");
        requestDTO.setCns("123456789012345");
        requestDTO.setConselhoProfissional(ConselhoProfissional.CRM);
        requestDTO.setRegistroConselho("1234PB");
        requestDTO.setUfConselho("PB");
        requestDTO.setUbsVinculadaId(1L);
        requestDTO.setEmailInstitucional("teste@apa.gov.br");
        requestDTO.setTelefoneContato("83999990000");
        requestDTO.setSenha("senha123");

        responseDTO = new ProfissionalResponseDTO();
        responseDTO.setId(1L);
        responseDTO.setNomeCompleto("Dr. Teste");
        responseDTO.setCpf("11122233344");
        responseDTO.setCns("123456789012345");
        responseDTO.setConselhoProfissional(ConselhoProfissional.CRM);
        responseDTO.setRegistroConselho("1234PB");
        responseDTO.setUfConselho("PB");
        responseDTO.setUbsVinculadaId(1L);
        responseDTO.setEmailInstitucional("teste@apa.gov.br");
        responseDTO.setTelefoneContato("83999990000");
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deveCriarProfissionalComSucesso() throws Exception {
        when(profissionalService.criarProfissional(any(ProfissionalRequestDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(post("/api/profissionais")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.nomeCompleto").value("Dr. Teste"))
                .andExpect(jsonPath("$.cpf").value("11122233344"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deveBuscarProfissionalPorIdComSucesso() throws Exception {
        when(profissionalService.buscarPorId(1L)).thenReturn(responseDTO);

        mockMvc.perform(get("/api/profissionais/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.nomeCompleto").value("Dr. Teste"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deveBuscarProfissionaisPorTipoETermo() throws Exception {
        when(profissionalService.buscarPorTipo("NOME", "Teste")).thenReturn(List.of(responseDTO));

        mockMvc.perform(get("/api/profissionais/buscar")
                        .param("tipo", "NOME")
                        .param("termo", "Teste"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].nomeCompleto").value("Dr. Teste"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deveListarTodosOsProfissionais() throws Exception {
        when(profissionalService.listarTodos()).thenReturn(List.of(responseDTO));

        mockMvc.perform(get("/api/profissionais"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].nomeCompleto").value("Dr. Teste"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deveAtualizarProfissionalComSucesso() throws Exception {
        when(profissionalService.atualizarProfissional(eq(1L), any(ProfissionalRequestDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(put("/api/profissionais/{id}", 1L)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.nomeCompleto").value("Dr. Teste"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deveDeletarProfissionalComSucesso() throws Exception {
        doNothing().when(profissionalService).deletarProfissional(1L);

        mockMvc.perform(delete("/api/profissionais/{id}", 1L)
                        .with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    void deveRetornar401QuandoSemAutenticacao() throws Exception {
        mockMvc.perform(get("/api/profissionais"))
                .andExpect(status().isUnauthorized());
    }

}