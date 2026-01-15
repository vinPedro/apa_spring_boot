package apa.ifpb.edu.br.APA.service;

import apa.ifpb.edu.br.APA.cliente.ViaCEPCliente;
import apa.ifpb.edu.br.APA.dto.PacienteRequestDTO;
import apa.ifpb.edu.br.APA.dto.PacienteResponseDTO;
import apa.ifpb.edu.br.APA.dto.ViaCEPResponseDTO;
import apa.ifpb.edu.br.APA.exception.OperacaoInvalidaException;
import apa.ifpb.edu.br.APA.mapper.PacienteMapper;
import apa.ifpb.edu.br.APA.model.Paciente;
import apa.ifpb.edu.br.APA.model.UnidadeSaude;
import apa.ifpb.edu.br.APA.model.Usuario;
import apa.ifpb.edu.br.APA.repository.PacienteRepository;
import apa.ifpb.edu.br.APA.repository.UnidadeSaudeRepository;
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
class PacienteServiceTest {

    @Mock private PacienteRepository pacienteRepository;
    @Mock private UnidadeSaudeRepository unidadeSaudeRepository;
    @Mock private PacienteMapper pacienteMapper;
    @Mock private UsuarioRepository usuarioRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private ViaCEPCliente viaCEPCliente;

    @InjectMocks
    private PacienteService pacienteService;


    @Test
    @DisplayName("Deve criar paciente com sucesso (Cenário Completo - Login CPF)")
    void criarPaciente_Sucesso() {
        PacienteRequestDTO dto = criarDtoValido();
        String cpfLimpo = "12345678900"; // dto.getCpf() sem formatação

        // Verifica duplicidade no Paciente
        when(pacienteRepository.findByCpf(dto.getCpf())).thenReturn(Optional.empty());
        when(pacienteRepository.findByCns(dto.getCns())).thenReturn(Optional.empty());

        // Verifica duplicidade no Usuário (Login agora é CPF)
        when(usuarioRepository.findByLogin(cpfLimpo)).thenReturn(Optional.empty());

        UnidadeSaude ubs = new UnidadeSaude();
        ubs.setId(10L);
        when(unidadeSaudeRepository.findById(10L)).thenReturn(Optional.of(ubs));

        when(passwordEncoder.encode(dto.getSenha())).thenReturn("senhaHash");
        when(viaCEPCliente.buscarCEP(dto.getCep())).thenReturn(criarEnderecoFake());

        Paciente pacienteEntity = new Paciente();
        when(pacienteMapper.toEntity(dto)).thenReturn(pacienteEntity);

        when(pacienteRepository.save(any(Paciente.class))).thenAnswer(i -> {
            Paciente p = i.getArgument(0);
            p.setId(1L);
            return p;
        });

        when(pacienteMapper.toResponseDTO(any())).thenReturn(new PacienteResponseDTO());

        PacienteResponseDTO resultado = pacienteService.criarPaciente(dto);

        assertNotNull(resultado);
        verify(pacienteRepository).save(any(Paciente.class));

        assertEquals("Rua Fake", pacienteEntity.getLogradouro());
        assertNotNull(pacienteEntity.getUsuario());

        // CORREÇÃO: Login deve ser o CPF
        assertEquals(cpfLimpo, pacienteEntity.getUsuario().getLogin());
    }

    @Test
    @DisplayName("Deve falhar ao criar se CPF (Login) já existe na tabela de usuários")
    void criarPaciente_CpfLoginDuplicado() {
        PacienteRequestDTO dto = criarDtoValido();
        String cpfLimpo = "12345678900";

        when(pacienteRepository.findByCpf(dto.getCpf())).thenReturn(Optional.empty());
        when(pacienteRepository.findByCns(dto.getCns())).thenReturn(Optional.empty());

        // Simula que já existe um USUÁRIO com esse CPF
        when(usuarioRepository.findByLogin(cpfLimpo)).thenReturn(Optional.of(new Usuario()));

        assertThrows(OperacaoInvalidaException.class, () -> pacienteService.criarPaciente(dto));
        verify(pacienteRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve atualizar paciente com sucesso (Mudando CPF e Login)")
    void atualizarPaciente_Sucesso() {
        Long id = 1L;
        PacienteRequestDTO dto = criarDtoValido();
        dto.setCpf("99988877700"); // Novo CPF
        dto.setUnidadeSaudeId(20L);

        Paciente pacienteExistente = new Paciente();
        pacienteExistente.setId(id);
        pacienteExistente.setUnidadeSaudeVinculada(new UnidadeSaude(10L, null, null, null, null, null, null, null, null));

        Usuario usuarioExistente = new Usuario();
        usuarioExistente.setLogin("12345678900"); // CPF Antigo
        pacienteExistente.setUsuario(usuarioExistente);

        when(pacienteRepository.findById(id)).thenReturn(Optional.of(pacienteExistente));

        // Validações de duplicidade
        when(pacienteRepository.findByCpf(any())).thenReturn(Optional.empty());
        when(pacienteRepository.findByCns(any())).thenReturn(Optional.empty());

        // Verifica se o novo CPF já é login de outro
        when(usuarioRepository.findByLogin("99988877700")).thenReturn(Optional.empty());

        when(unidadeSaudeRepository.findById(20L)).thenReturn(Optional.of(new UnidadeSaude()));
        when(viaCEPCliente.buscarCEP(any())).thenReturn(criarEnderecoFake());

        when(pacienteRepository.save(any())).thenReturn(pacienteExistente);
        when(pacienteMapper.toResponseDTO(any())).thenReturn(new PacienteResponseDTO());

        pacienteService.atualizarPaciente(id, dto);

        verify(pacienteRepository).save(pacienteExistente);
        // CORREÇÃO: Login deve ter atualizado para o novo CPF
        assertEquals("99988877700", pacienteExistente.getUsuario().getLogin());
        verify(unidadeSaudeRepository).findById(20L);
    }

    @Test
    @DisplayName("Deve falhar ao atualizar se novo CPF já é login de outro usuário")
    void atualizarPaciente_CpfLoginEmUso() {
        Long id = 1L;
        PacienteRequestDTO dto = criarDtoValido();
        dto.setCpf("99988877700"); // Novo CPF

        Paciente pacienteExistente = new Paciente();
        pacienteExistente.setId(id);
        pacienteExistente.setUsuario(new Usuario(1L, "12345678900", "senha", null)); // CPF Antigo

        when(pacienteRepository.findById(id)).thenReturn(Optional.of(pacienteExistente));

        // Simula que o novo CPF já tem usuário
        when(usuarioRepository.findByLogin("99988877700")).thenReturn(Optional.of(new Usuario()));

        assertThrows(OperacaoInvalidaException.class, () -> pacienteService.atualizarPaciente(id, dto));
    }

    @Test
    @DisplayName("Deve buscar por ID existente")
    void buscarPorId_Sucesso() {
        when(pacienteRepository.findById(1L)).thenReturn(Optional.of(new Paciente()));
        when(pacienteMapper.toResponseDTO(any())).thenReturn(new PacienteResponseDTO());

        assertNotNull(pacienteService.buscarPorId(1L));
    }

    @Test
    @DisplayName("Deve listar todos")
    void listarTodos_Sucesso() {
        when(pacienteRepository.findAll()).thenReturn(List.of(new Paciente()));
        when(pacienteMapper.toResponseDTO(any())).thenReturn(new PacienteResponseDTO());

        List<PacienteResponseDTO> lista = pacienteService.listarTodos();
        assertFalse(lista.isEmpty());
    }

    @Test
    @DisplayName("Deve buscar por TIPO: NOME")
    void buscarPorTipo_Nome() {
        when(pacienteRepository.findByNomeCompletoContainingIgnoreCase("Maria"))
                .thenReturn(List.of(new Paciente()));

        List<PacienteResponseDTO> res = pacienteService.buscarPorTipo("NOME", "Maria");
        assertFalse(res.isEmpty());
    }

    @Test
    @DisplayName("Deve buscar por TIPO: CPF (Removendo pontuação)")
    void buscarPorTipo_CPF() {
        when(pacienteRepository.findByCpfContaining("12345678900"))
                .thenReturn(List.of(new Paciente()));

        List<PacienteResponseDTO> res = pacienteService.buscarPorTipo("CPF", "123.456.789-00");
        assertFalse(res.isEmpty());
        verify(pacienteRepository).findByCpfContaining("12345678900");
    }

    @Test
    @DisplayName("Deve retornar vazio se termo for vazio")
    void buscarPorTipo_TermoVazio() {
        // Se termo for vazio, chama listarTodos (findAll)
        when(pacienteRepository.findAll()).thenReturn(List.of(new Paciente()));
        pacienteService.buscarPorTipo("NOME", "");
        verify(pacienteRepository).findAll();
    }

    @Test
    @DisplayName("Deve retornar vazio para tipo inválido")
    void buscarPorTipo_Invalido() {
        List<PacienteResponseDTO> res = pacienteService.buscarPorTipo("XPTO", "valor");
        assertTrue(res.isEmpty());
    }

    @Test
    @DisplayName("Deve deletar paciente existente")
    void deletarPaciente_Sucesso() {
        Paciente p = new Paciente();
        when(pacienteRepository.findById(1L)).thenReturn(Optional.of(p));

        pacienteService.deletarPaciente(1L);
        verify(pacienteRepository).delete(p);
    }

    @Test
    @DisplayName("Atualizar: Não deve mudar Login/UBS se CPF e UBS forem iguais")
    void atualizarPaciente_SemMudancas() {
        Long id = 1L;
        PacienteRequestDTO dto = new PacienteRequestDTO();
        dto.setCpf("12345678900"); // Mesmo CPF
        dto.setCns("100000000000000");
        dto.setCep("58000000");
        dto.setUnidadeSaudeId(10L); // Mesma UBS

        Paciente pacienteExistente = new Paciente();
        pacienteExistente.setId(id);
        pacienteExistente.setUnidadeSaudeVinculada(new UnidadeSaude(10L, null, null, null, null, null, null, null, null));
        pacienteExistente.setUsuario(new Usuario(1L, "12345678900", "senha", null));

        when(pacienteRepository.findById(id)).thenReturn(Optional.of(pacienteExistente));
        when(pacienteRepository.findByCpf(any())).thenReturn(Optional.empty());
        when(pacienteRepository.findByCns(any())).thenReturn(Optional.empty());

        when(viaCEPCliente.buscarCEP(any())).thenReturn(criarEnderecoFake());

        when(pacienteRepository.save(any())).thenReturn(pacienteExistente);
        when(pacienteMapper.toResponseDTO(any())).thenReturn(new PacienteResponseDTO());

        pacienteService.atualizarPaciente(id, dto);

        // Verifica que não tentou buscar outro usuário
        assertEquals("12345678900", pacienteExistente.getUsuario().getLogin());
        verify(usuarioRepository, never()).findByLogin(anyString());
    }

    @Test
    @DisplayName("Validação: Deve falhar no Update se CPF pertence a OUTRO paciente (Tabela Paciente)")
    void validarUpdate_OutroCpf_DeveFalhar() {
        Long id = 1L;
        PacienteRequestDTO dto = criarDtoValido();
        // Tenta usar um CPF que já existe em OUTRO paciente
        Paciente outroPaciente = new Paciente();
        outroPaciente.setId(2L);

        when(pacienteRepository.findById(id)).thenReturn(Optional.of(new Paciente()));
        when(pacienteRepository.findByCpf(dto.getCpf())).thenReturn(Optional.of(outroPaciente));

        assertThrows(OperacaoInvalidaException.class, () -> pacienteService.atualizarPaciente(id, dto));
    }

    private PacienteRequestDTO criarDtoValido() {
        PacienteRequestDTO dto = new PacienteRequestDTO();
        dto.setCpf("12345678900");
        dto.setCns("123456789012345");
        dto.setEmail("test@email.com"); // Email informativo apenas
        dto.setSenha("123456");
        dto.setCep("58000000");
        dto.setUnidadeSaudeId(10L);
        return dto;
    }

    private ViaCEPResponseDTO criarEnderecoFake() {
        ViaCEPResponseDTO endereco = new ViaCEPResponseDTO();
        endereco.setLogradouro("Rua Fake");
        endereco.setBairro("Centro");
        endereco.setLocalidade("Cidade");
        endereco.setUf("PB");
        endereco.setCep("58000000");
        return endereco;
    }
}