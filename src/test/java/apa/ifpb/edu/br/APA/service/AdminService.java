package apa.ifpb.edu.br.APA.service;

import apa.ifpb.edu.br.APA.dto.AdminRequestDTO;
import apa.ifpb.edu.br.APA.dto.AdminResponseDTO;
import apa.ifpb.edu.br.APA.exception.OperacaoInvalidaException;
import apa.ifpb.edu.br.APA.exception.RecursoNaoEncontradoException;
import apa.ifpb.edu.br.APA.mapper.AdminMapper;
import apa.ifpb.edu.br.APA.model.Admin;
import apa.ifpb.edu.br.APA.model.Role;
import apa.ifpb.edu.br.APA.model.Usuario;
import apa.ifpb.edu.br.APA.repository.AdminRepository;
import apa.ifpb.edu.br.APA.repository.UsuarioRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminServiceTest {

    @Mock
    private AdminRepository adminRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private AdminMapper adminMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AdminService adminService;

    //TESTES ADMIN

    @Test
    @DisplayName("Deve criar admin com sucesso quando login não existe")
    void criarAdmin_Sucesso() {
        AdminRequestDTO dto = new AdminRequestDTO("novoAdmin", "senha123");

        when(usuarioRepository.findByLogin(dto.getLogin())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(dto.getSenha())).thenReturn("senhaCriptografada");

        Admin adminSalvo = new Admin();
        adminSalvo.setId(1L);
        Usuario usuario = new Usuario(1L, "novoAdmin", "senhaCriptografada", Role.ROLE_ADMIN);
        adminSalvo.setUsuario(usuario);

        when(adminRepository.save(any(Admin.class))).thenReturn(adminSalvo);

        AdminResponseDTO responseDTO = new AdminResponseDTO(1L, "novoAdmin");
        when(adminMapper.toResponseDTO(adminSalvo)).thenReturn(responseDTO);

        AdminResponseDTO resultado = adminService.criarAdmin(dto);

        assertNotNull(resultado);
        assertEquals("novoAdmin", resultado.getLogin());

        verify(passwordEncoder).encode("senha123");
        verify(adminRepository).save(any(Admin.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar criar admin com login já existente")
    void criarAdmin_LoginDuplicado_DeveFalhar() {
        AdminRequestDTO dto = new AdminRequestDTO("adminExistente", "senha123");

        when(usuarioRepository.findByLogin(dto.getLogin())).thenReturn(Optional.of(new Usuario()));

        assertThrows(OperacaoInvalidaException.class, () -> adminService.criarAdmin(dto));

        verify(adminRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve retornar lista de admins")
    void listarTodos_Sucesso() {
        when(adminRepository.findAll()).thenReturn(List.of(new Admin(), new Admin()));
        when(adminMapper.toResponseDTO(any())).thenReturn(new AdminResponseDTO());

        List<AdminResponseDTO> lista = adminService.listarTodos();

        assertEquals(2, lista.size());
        verify(adminRepository).findAll();
    }


    @Test
    @DisplayName("Deve buscar admin por ID existente")
    void buscarPorId_Sucesso() {
        Long id = 1L;
        Admin admin = new Admin();
        admin.setId(id);

        when(adminRepository.findById(id)).thenReturn(Optional.of(admin));
        when(adminMapper.toResponseDTO(admin)).thenReturn(new AdminResponseDTO(id, "login"));

        AdminResponseDTO resultado = adminService.buscarPorId(id);

        assertNotNull(resultado);
        assertEquals(id, resultado.getId());
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar admin por ID inexistente")
    void buscarPorId_NaoEncontrado() {
        Long id = 99L;
        when(adminRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(RecursoNaoEncontradoException.class, () -> adminService.buscarPorId(id));
    }

    @Test
    @DisplayName("Deve buscar admin por Login existente")
    void buscarPorLogin_Sucesso() {
        String login = "adminMaster";
        Admin admin = new Admin();

        when(adminRepository.findByUsuarioLogin(login)).thenReturn(Optional.of(admin));
        when(adminMapper.toResponseDTO(admin)).thenReturn(new AdminResponseDTO(1L, login));

        AdminResponseDTO resultado = adminService.buscarPorLogin(login);

        assertNotNull(resultado);
        assertEquals(login, resultado.getLogin());
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar admin por Login inexistente")
    void buscarPorLogin_NaoEncontrado() {
        String login = "inexistente";
        when(adminRepository.findByUsuarioLogin(login)).thenReturn(Optional.empty());

        assertThrows(RecursoNaoEncontradoException.class, () -> adminService.buscarPorLogin(login));
    }
}
