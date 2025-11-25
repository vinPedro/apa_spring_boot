package apa.ifpb.edu.br.APA.controller;

import apa.ifpb.edu.br.APA.dto.AdminRequestDTO;
import apa.ifpb.edu.br.APA.model.Admin;
import apa.ifpb.edu.br.APA.model.Role;
import apa.ifpb.edu.br.APA.model.Usuario;
import apa.ifpb.edu.br.APA.repository.AdminRepository;
import apa.ifpb.edu.br.APA.repository.UsuarioRepository;
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

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @BeforeEach
    void setup() {
    }

    @Test
    @DisplayName("POST /api/admins - Deve criar novo Admin (Logado como ADMIN)")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void criarAdmin_Integracao() throws Exception {
        AdminRequestDTO dto = new AdminRequestDTO("adminTesteIntegracao", "123456");

        mockMvc.perform(post("/api/admins")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.login", is("adminTesteIntegracao")));
    }

    @Test
    @DisplayName("GET /api/admins - Deve listar admins (Logado como ADMIN)")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void listarAdmins_Integracao() throws Exception {
        salvarAdminAuxiliar("adminLista", "123");

        mockMvc.perform(get("/api/admins"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(org.hamcrest.Matchers.greaterThanOrEqualTo(1))));
    }

    @Test
    @DisplayName("GET /api/admins/{id} - Deve retornar admin com sucesso")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void buscarPorId_Sucesso() throws Exception {
        Admin admin = salvarAdminAuxiliar("adminBuscaId", "123");

        mockMvc.perform(get("/api/admins/{id}", admin.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.login", is("adminBuscaId")));
    }

    @Test
    @DisplayName("GET /api/admins/{id} - Deve retornar 404 se não existir")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void buscarPorId_NaoEncontrado() throws Exception {
        mockMvc.perform(get("/api/admins/{id}", 9999L))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /api/admins/login/{login} - Deve retornar admin por login com sucesso")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void buscarPorLogin_Sucesso() throws Exception {
        salvarAdminAuxiliar("adminLoginBusca", "123");

        mockMvc.perform(get("/api/admins/login/{login}", "adminLoginBusca"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.login", is("adminLoginBusca")));
    }

    @Test
    @DisplayName("GET /api/admins - Deve retornar 403 Forbidden se não for ADMIN")
    @WithMockUser(username = "paciente", roles = {"PACIENTE"})
    void listarAdmins_SemPermissao() throws Exception {
        mockMvc.perform(get("/api/admins"))
                .andExpect(status().isForbidden());
    }

    private Admin salvarAdminAuxiliar(String login, String senha) {
        if (usuarioRepository.findByLogin(login).isPresent()) {
            return adminRepository.findByUsuarioLogin(login).get();
        }
        Usuario usuario = new Usuario(null, login, senha, Role.ROLE_ADMIN);
        Admin admin = new Admin(null, usuario);
        return adminRepository.save(admin);
    }
}