package apa.ifpb.edu.br.APA.controller;

import apa.ifpb.edu.br.APA.dto.UnidadeSaudeDTO;
import apa.ifpb.edu.br.APA.repository.UsuarioRepository;
import apa.ifpb.edu.br.APA.security.SecurityConfig;
import apa.ifpb.edu.br.APA.security.SecurityFilter;
import apa.ifpb.edu.br.APA.security.TokenService;
import apa.ifpb.edu.br.APA.service.UnidadeSaudeService;
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

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UnidadeSaudeController.class)
@Import({SecurityConfig.class, SecurityFilter.class})
class UnidadeSaudeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UnidadeSaudeService unidadeSaudeService;

    @MockitoBean
    private TokenService tokenService;

    @MockitoBean
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private UnidadeSaudeDTO dto;

    @BeforeEach
    void setUp() {
        dto = new UnidadeSaudeDTO();
        dto.setId(1L);
        dto.setCodigoCnes("1234567");
        dto.setCnpj("00.000.000/0001-91");
        dto.setNome("UBS Integracao");
        dto.setCep("58000000");
        dto.setLogradouro("Rua Teste");
        dto.setBairro("Centro");
        dto.setMunicipio("Joao Pessoa");
        dto.setUf("PB");
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deveListarTodasUnidades() throws Exception {
        when(unidadeSaudeService.listarTodas()).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/unidades"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].nome").value("UBS Integracao"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deveBuscarPorId() throws Exception {
        when(unidadeSaudeService.buscarPorId(1L)).thenReturn(dto);

        mockMvc.perform(get("/api/unidades/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.codigoCnes").value("1234567"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deveCriarUnidade() throws Exception {
        when(unidadeSaudeService.criarUnidade(any(UnidadeSaudeDTO.class))).thenReturn(dto);

        mockMvc.perform(post("/api/unidades")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.nome").value("UBS Integracao"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deveAtualizarUnidade() throws Exception {
        when(unidadeSaudeService.atualizarUnidade(eq(1L), any(UnidadeSaudeDTO.class))).thenReturn(dto);

        mockMvc.perform(put("/api/unidades/{id}", 1L)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.nome").value("UBS Integracao"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deveDeletarUnidade() throws Exception {
        doNothing().when(unidadeSaudeService).deletarUnidade(1L);

        mockMvc.perform(delete("/api/unidades/{id}", 1L)
                        .with(csrf()))
                .andExpect(status().isNoContent());
    }


    @Test
    @WithMockUser(roles = "PACIENTE")
    void deveRetornar403QuandoSemPermissao() throws Exception {
        mockMvc.perform(get("/api/unidades"))
                .andExpect(status().isForbidden());
    }
}