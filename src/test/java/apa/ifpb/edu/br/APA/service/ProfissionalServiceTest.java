package apa.ifpb.edu.br.APA.service;

import apa.ifpb.edu.br.APA.dto.ProfissionalRequestDTO;
import apa.ifpb.edu.br.APA.dto.ProfissionalResponseDTO;
import apa.ifpb.edu.br.APA.exception.OperacaoInvalidaException;
import apa.ifpb.edu.br.APA.exception.RecursoNaoEncontradoException;
import apa.ifpb.edu.br.APA.mapper.ProfissionalMapper;
import apa.ifpb.edu.br.APA.model.*;
import apa.ifpb.edu.br.APA.repository.ProfissionalRepository;
import apa.ifpb.edu.br.APA.repository.UnidadeSaudeRepository;
import apa.ifpb.edu.br.APA.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProfissionalServiceTest {

    @InjectMocks
    private ProfissionalService profissionalService;

    @Mock
    private ProfissionalRepository profissionalRepository;

    @Mock
    private UnidadeSaudeRepository unidadeSaudeRepository;

    @Mock
    private ProfissionalMapper profissionalMapper;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    private ProfissionalRequestDTO requestDTO;
    private Profissional profissionalEntity;
    private ProfissionalResponseDTO responseDTO;
    private UnidadeSaude unidadeSaude;
    private Usuario usuario;

    @BeforeEach
    void setUp() {
        unidadeSaude = new UnidadeSaude();
        unidadeSaude.setId(1L);
        unidadeSaude.setNome("UBS Teste");

        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setLogin("11122233344");
        usuario.setSenha("encodedPass");
        usuario.setRole(Role.ROLE_PROFISSIONAL);

        requestDTO = new ProfissionalRequestDTO();
        requestDTO.setNomeCompleto("Dr. Teste");
        requestDTO.setCpf("11122233344");
        requestDTO.setCns("123456789012345");
        requestDTO.setEmailInstitucional("teste@apa.gov.br");
        requestDTO.setUbsVinculadaId(1L);
        requestDTO.setSenha("senha123");
        requestDTO.setConselhoProfissional(ConselhoProfissional.CRM);
        requestDTO.setRegistroConselho("1234PB");
        requestDTO.setUfConselho("PB");

        profissionalEntity = new Profissional();
        profissionalEntity.setId(1L);
        profissionalEntity.setNomeCompleto("Dr. Teste");
        profissionalEntity.setCpf("11122233344");
        profissionalEntity.setCns("123456789012345");
        profissionalEntity.setEmailInstitucional("teste@apa.gov.br");
        profissionalEntity.setUbsVinculada(unidadeSaude);
        profissionalEntity.setUsuario(usuario);
        profissionalEntity.setConselhoProfissional(ConselhoProfissional.CRM);

        responseDTO = new ProfissionalResponseDTO();
        responseDTO.setId(1L);
        responseDTO.setNomeCompleto("Dr. Teste");
        responseDTO.setCpf("11122233344");
        responseDTO.setUbsVinculadaId(1L);
    }

    @Test
    void deveCriarProfissionalComSucesso() {
        when(profissionalRepository.findByCpf(anyString())).thenReturn(Optional.empty());
        when(profissionalRepository.findByCns(anyString())).thenReturn(Optional.empty());
        when(profissionalRepository.findByEmailInstitucional(anyString())).thenReturn(Optional.empty());
        when(usuarioRepository.findByLogin(anyString())).thenReturn(Optional.empty());
        when(unidadeSaudeRepository.findById(1L)).thenReturn(Optional.of(unidadeSaude));
        when(profissionalMapper.toEntity(requestDTO)).thenReturn(profissionalEntity);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPass");
        when(profissionalRepository.save(any(Profissional.class))).thenReturn(profissionalEntity);
        when(profissionalMapper.toResponseDTO(profissionalEntity)).thenReturn(responseDTO);

        ProfissionalResponseDTO result = profissionalService.criarProfissional(requestDTO);

        assertNotNull(result);
        assertEquals(responseDTO.getId(), result.getId());
        verify(profissionalRepository, times(1)).save(any(Profissional.class));
    }

    @Test
    void deveLancarExcecaoQuandoCpfJaCadastradoEmProfissional() {
        when(profissionalRepository.findByCpf(requestDTO.getCpf())).thenReturn(Optional.of(new Profissional()));

        assertThrows(OperacaoInvalidaException.class, () -> profissionalService.criarProfissional(requestDTO));
        verify(profissionalRepository, never()).save(any());
    }

    @Test
    void deveLancarExcecaoQuandoCnsJaCadastrado() {
        when(profissionalRepository.findByCpf(anyString())).thenReturn(Optional.empty());
        when(profissionalRepository.findByCns(requestDTO.getCns())).thenReturn(Optional.of(new Profissional()));

        assertThrows(OperacaoInvalidaException.class, () -> profissionalService.criarProfissional(requestDTO));
        verify(profissionalRepository, never()).save(any());
    }

    @Test
    void deveLancarExcecaoQuandoEmailJaCadastrado() {
        when(profissionalRepository.findByCpf(anyString())).thenReturn(Optional.empty());
        when(profissionalRepository.findByCns(anyString())).thenReturn(Optional.empty());
        when(profissionalRepository.findByEmailInstitucional(requestDTO.getEmailInstitucional())).thenReturn(Optional.of(new Profissional()));

        assertThrows(OperacaoInvalidaException.class, () -> profissionalService.criarProfissional(requestDTO));
        verify(profissionalRepository, never()).save(any());
    }

    @Test
    void deveLancarExcecaoQuandoUsuarioComLoginJaExiste() {
        when(profissionalRepository.findByCpf(anyString())).thenReturn(Optional.empty());
        when(profissionalRepository.findByCns(anyString())).thenReturn(Optional.empty());
        when(profissionalRepository.findByEmailInstitucional(anyString())).thenReturn(Optional.empty());
        when(usuarioRepository.findByLogin(requestDTO.getCpf())).thenReturn(Optional.of(new Usuario()));

        assertThrows(OperacaoInvalidaException.class, () -> profissionalService.criarProfissional(requestDTO));
        verify(profissionalRepository, never()).save(any());
    }

    @Test
    void deveLancarExcecaoQuandoUbsNaoEncontradaAoCriar() {
        when(profissionalRepository.findByCpf(anyString())).thenReturn(Optional.empty());
        when(profissionalRepository.findByCns(anyString())).thenReturn(Optional.empty());
        when(profissionalRepository.findByEmailInstitucional(anyString())).thenReturn(Optional.empty());
        when(usuarioRepository.findByLogin(anyString())).thenReturn(Optional.empty());
        when(profissionalMapper.toEntity(requestDTO)).thenReturn(profissionalEntity);
        when(unidadeSaudeRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RecursoNaoEncontradoException.class, () -> profissionalService.criarProfissional(requestDTO));
    }

    @Test
    void deveBuscarProfissionalPorIdComSucesso() {
        when(profissionalRepository.findById(1L)).thenReturn(Optional.of(profissionalEntity));
        when(profissionalMapper.toResponseDTO(profissionalEntity)).thenReturn(responseDTO);

        ProfissionalResponseDTO result = profissionalService.buscarPorId(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void deveLancarExcecaoQuandoBuscarPorIdInexistente() {
        when(profissionalRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RecursoNaoEncontradoException.class, () -> profissionalService.buscarPorId(1L));
    }

    @Test
    void deveListarTodosOsProfissionais() {
        when(profissionalRepository.findAll()).thenReturn(List.of(profissionalEntity));
        when(profissionalMapper.toResponseDTO(profissionalEntity)).thenReturn(responseDTO);

        List<ProfissionalResponseDTO> result = profissionalService.listarTodos();

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
    }

    @Test
    void deveAtualizarProfissionalComSucesso() {
        ProfissionalRequestDTO updateDTO = new ProfissionalRequestDTO();
        updateDTO.setCpf("11122233344");
        updateDTO.setCns("123456789012345");
        updateDTO.setEmailInstitucional("novo@apa.gov.br");
        updateDTO.setUbsVinculadaId(1L);

        when(profissionalRepository.findById(1L)).thenReturn(Optional.of(profissionalEntity));
        when(profissionalRepository.findByCpf(anyString())).thenReturn(Optional.empty());
        when(profissionalRepository.findByCns(anyString())).thenReturn(Optional.empty());
        when(profissionalRepository.findByEmailInstitucional(anyString())).thenReturn(Optional.empty());
        when(profissionalRepository.save(any(Profissional.class))).thenReturn(profissionalEntity);
        when(profissionalMapper.toResponseDTO(profissionalEntity)).thenReturn(responseDTO);

        ProfissionalResponseDTO result = profissionalService.atualizarProfissional(1L, updateDTO);

        assertNotNull(result);
        verify(profissionalMapper).updateEntityFromDTO(updateDTO, profissionalEntity);
        verify(profissionalRepository).save(profissionalEntity);
    }

    @Test
    void deveAtualizarProfissionalMudandoUBS() {
        UnidadeSaude novaUbs = new UnidadeSaude();
        novaUbs.setId(2L);
        ProfissionalRequestDTO updateDTO = new ProfissionalRequestDTO();
        updateDTO.setCpf("11122233344");
        updateDTO.setUbsVinculadaId(2L);

        when(profissionalRepository.findById(1L)).thenReturn(Optional.of(profissionalEntity));
        when(unidadeSaudeRepository.findById(2L)).thenReturn(Optional.of(novaUbs));
        when(profissionalRepository.save(any(Profissional.class))).thenReturn(profissionalEntity);
        when(profissionalMapper.toResponseDTO(profissionalEntity)).thenReturn(responseDTO);

        profissionalService.atualizarProfissional(1L, updateDTO);

        verify(unidadeSaudeRepository).findById(2L);
        verify(profissionalRepository).save(profissionalEntity);
    }

    @Test
    void deveDeletarProfissionalComSucesso() {
        when(profissionalRepository.findById(1L)).thenReturn(Optional.of(profissionalEntity));

        profissionalService.deletarProfissional(1L);

        verify(profissionalRepository, times(1)).delete(profissionalEntity);
    }

    @Test
    void deveLancarExcecaoAoDeletarProfissionalInexistente() {
        when(profissionalRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RecursoNaoEncontradoException.class, () -> profissionalService.deletarProfissional(1L));
        verify(profissionalRepository, never()).delete(any());
    }

    @Test
    void deveBuscarPorTipoNome() {
        when(profissionalRepository.findByNomeCompletoContainingIgnoreCase("Dr")).thenReturn(List.of(profissionalEntity));
        when(profissionalMapper.toResponseDTO(profissionalEntity)).thenReturn(responseDTO);

        List<ProfissionalResponseDTO> result = profissionalService.buscarPorTipo("NOME", "Dr");

        assertFalse(result.isEmpty());
        verify(profissionalRepository).findByNomeCompletoContainingIgnoreCase("Dr");
    }

    @Test
    void deveBuscarPorTipoCpf() {
        when(profissionalRepository.findByCpfContaining("11122233344")).thenReturn(List.of(profissionalEntity));
        when(profissionalMapper.toResponseDTO(profissionalEntity)).thenReturn(responseDTO);

        List<ProfissionalResponseDTO> result = profissionalService.buscarPorTipo("CPF", "111.222.333-44");

        assertFalse(result.isEmpty());
        verify(profissionalRepository).findByCpfContaining("11122233344");
    }

    @Test
    void deveBuscarPorTipoCns() {
        when(profissionalRepository.findByCnsContaining("12345")).thenReturn(List.of(profissionalEntity));
        when(profissionalMapper.toResponseDTO(profissionalEntity)).thenReturn(responseDTO);

        List<ProfissionalResponseDTO> result = profissionalService.buscarPorTipo("CNS", "12345");

        assertFalse(result.isEmpty());
        verify(profissionalRepository).findByCnsContaining("12345");
    }

    @Test
    void deveRetornarTodosQuandoBuscarPorTipoSemTermo() {
        when(profissionalRepository.findAll()).thenReturn(List.of(profissionalEntity));
        when(profissionalMapper.toResponseDTO(profissionalEntity)).thenReturn(responseDTO);

        List<ProfissionalResponseDTO> result = profissionalService.buscarPorTipo("NOME", "");

        assertFalse(result.isEmpty());
        verify(profissionalRepository).findAll();
    }

    @Test
    void deveRetornarVazioQuandoBuscarPorTipoInvalido() {
        List<ProfissionalResponseDTO> result = profissionalService.buscarPorTipo("INVALIDO", "termo");
        assertTrue(result.isEmpty());
    }
}