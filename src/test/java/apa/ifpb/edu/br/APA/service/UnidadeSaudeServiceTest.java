package apa.ifpb.edu.br.APA.service;

import apa.ifpb.edu.br.APA.cliente.ViaCEPCliente;
import apa.ifpb.edu.br.APA.dto.UnidadeSaudeDTO;
import apa.ifpb.edu.br.APA.dto.ViaCEPResponseDTO;
import apa.ifpb.edu.br.APA.exception.OperacaoInvalidaException;
import apa.ifpb.edu.br.APA.exception.RecursoJaExisteException;
import apa.ifpb.edu.br.APA.exception.RecursoNaoEncontradoException;
import apa.ifpb.edu.br.APA.mapper.UnidadeSaudeMapper;
import apa.ifpb.edu.br.APA.model.UnidadeSaude;
import apa.ifpb.edu.br.APA.repository.PacienteRepository;
import apa.ifpb.edu.br.APA.repository.ProfissionalRepository;
import apa.ifpb.edu.br.APA.repository.UnidadeSaudeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UnidadeSaudeServiceTest {

    @InjectMocks
    private UnidadeSaudeService unidadeSaudeService;

    @Mock
    private UnidadeSaudeRepository unidadeSaudeRepository;

    @Mock
    private UnidadeSaudeMapper unidadeSaudeMapper;

    @Mock
    private ViaCEPCliente viaCEPCliente;

    @Mock
    private ProfissionalRepository profissionalRepository;

    @Mock
    private PacienteRepository pacienteRepository;

    private UnidadeSaudeDTO dto;
    private UnidadeSaude entity;
    private ViaCEPResponseDTO viaCepResponse;

    @BeforeEach
    void setUp() {
        dto = new UnidadeSaudeDTO();
        dto.setId(1L);
        dto.setCodigoCnes("1234567");
        dto.setCnpj("00000000000191");
        dto.setCep("58000000");
        dto.setNome("UBS Teste");

        entity = new UnidadeSaude();
        entity.setId(1L);
        entity.setCodigoCnes("1234567");
        entity.setCnpj("00000000000191");
        entity.setNome("UBS Teste");

        viaCepResponse = new ViaCEPResponseDTO();
        viaCepResponse.setCep("58000000");
        viaCepResponse.setLogradouro("Rua Teste");
        viaCepResponse.setBairro("Bairro Teste");
        viaCepResponse.setLocalidade("Cidade Teste");
        viaCepResponse.setUf("PB");
    }

    @Test
    void deveListarTodasUnidades() {
        when(unidadeSaudeRepository.findAll()).thenReturn(List.of(entity));
        when(unidadeSaudeMapper.toDTO(entity)).thenReturn(dto);

        List<UnidadeSaudeDTO> result = unidadeSaudeService.listarTodas();

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        verify(unidadeSaudeRepository).findAll();
    }

    @Test
    void deveBuscarPorIdComSucesso() {
        when(unidadeSaudeRepository.findById(1L)).thenReturn(Optional.of(entity));
        when(unidadeSaudeMapper.toDTO(entity)).thenReturn(dto);

        UnidadeSaudeDTO result = unidadeSaudeService.buscarPorId(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void deveLancarExcecaoAoBuscarPorIdInexistente() {
        when(unidadeSaudeRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RecursoNaoEncontradoException.class, () -> unidadeSaudeService.buscarPorId(1L));
    }

    @Test
    void deveCriarUnidadeComSucesso() {
        when(unidadeSaudeRepository.findByCodigoCnes(anyString())).thenReturn(Optional.empty());
        when(unidadeSaudeRepository.findByCnpj(anyString())).thenReturn(Optional.empty());
        when(unidadeSaudeMapper.toUnidadeSaude(dto)).thenReturn(entity);
        when(viaCEPCliente.buscarCEP(anyString())).thenReturn(viaCepResponse);
        when(unidadeSaudeRepository.save(any(UnidadeSaude.class))).thenReturn(entity);
        when(unidadeSaudeMapper.toDTO(entity)).thenReturn(dto);

        UnidadeSaudeDTO result = unidadeSaudeService.criarUnidade(dto);

        assertNotNull(result);
        verify(unidadeSaudeRepository).save(entity);
        verify(viaCEPCliente).buscarCEP(dto.getCep());
    }

    @Test
    void deveLancarExcecaoAoCriarComCnesDuplicado() {
        when(unidadeSaudeRepository.findByCodigoCnes(dto.getCodigoCnes())).thenReturn(Optional.of(entity));

        assertThrows(RecursoJaExisteException.class, () -> unidadeSaudeService.criarUnidade(dto));
        verify(unidadeSaudeRepository, never()).save(any());
    }

    @Test
    void deveLancarExcecaoAoCriarComCnpjDuplicado() {
        when(unidadeSaudeRepository.findByCodigoCnes(anyString())).thenReturn(Optional.empty());
        when(unidadeSaudeRepository.findByCnpj(dto.getCnpj())).thenReturn(Optional.of(entity));

        assertThrows(RecursoJaExisteException.class, () -> unidadeSaudeService.criarUnidade(dto));
        verify(unidadeSaudeRepository, never()).save(any());
    }

    @Test
    void deveAtualizarUnidadeComSucesso() {
        when(unidadeSaudeRepository.findById(1L)).thenReturn(Optional.of(entity));
        when(viaCEPCliente.buscarCEP(anyString())).thenReturn(viaCepResponse);
        when(unidadeSaudeRepository.save(entity)).thenReturn(entity);
        when(unidadeSaudeMapper.toDTO(entity)).thenReturn(dto);

        UnidadeSaudeDTO result = unidadeSaudeService.atualizarUnidade(1L, dto);

        assertNotNull(result);
        verify(unidadeSaudeMapper).updateFromDto(dto, entity);
        verify(unidadeSaudeRepository).save(entity);
    }

    @Test
    void deveLancarExcecaoAoAtualizarUnidadeInexistente() {
        when(unidadeSaudeRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RecursoNaoEncontradoException.class, () -> unidadeSaudeService.atualizarUnidade(1L, dto));
    }

    @Test
    void deveLancarExcecaoAoAtualizarComCnesDuplicadoDeOutraUnidade() {
        UnidadeSaude outraUnidade = new UnidadeSaude();
        outraUnidade.setId(2L);

        when(unidadeSaudeRepository.findById(1L)).thenReturn(Optional.of(entity));
        dto.setCodigoCnes("9999999");
        when(unidadeSaudeRepository.findByCodigoCnes("9999999")).thenReturn(Optional.of(outraUnidade));

        assertThrows(RecursoJaExisteException.class, () -> unidadeSaudeService.atualizarUnidade(1L, dto));
    }

    @Test
    void deveLancarExcecaoAoAtualizarComCnpjDuplicadoDeOutraUnidade() {
        UnidadeSaude outraUnidade = new UnidadeSaude();
        outraUnidade.setId(2L);

        when(unidadeSaudeRepository.findById(1L)).thenReturn(Optional.of(entity));
        dto.setCnpj("99999999000199");
        when(unidadeSaudeRepository.findByCnpj("99999999000199")).thenReturn(Optional.of(outraUnidade));

        assertThrows(RecursoJaExisteException.class, () -> unidadeSaudeService.atualizarUnidade(1L, dto));
    }

    @Test
    void deveDeletarUnidadeComSucesso() {
        when(unidadeSaudeRepository.findById(1L)).thenReturn(Optional.of(entity));
        when(pacienteRepository.existsByUnidadeSaudeVinculadaId(1L)).thenReturn(false);
        when(profissionalRepository.existsByUbsVinculadaId(1L)).thenReturn(false);

        unidadeSaudeService.deletarUnidade(1L);

        verify(unidadeSaudeRepository).delete(entity);
    }

    @Test
    void deveLancarExcecaoAoDeletarUnidadeInexistente() {
        when(unidadeSaudeRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RecursoNaoEncontradoException.class, () -> unidadeSaudeService.deletarUnidade(1L));
    }

    @Test
    void deveLancarExcecaoAoDeletarUnidadeComPacientesVinculados() {
        when(unidadeSaudeRepository.findById(1L)).thenReturn(Optional.of(entity));
        when(pacienteRepository.existsByUnidadeSaudeVinculadaId(1L)).thenReturn(true);

        assertThrows(OperacaoInvalidaException.class, () -> unidadeSaudeService.deletarUnidade(1L));
        verify(unidadeSaudeRepository, never()).delete(any());
    }

    @Test
    void deveLancarExcecaoAoDeletarUnidadeComProfissionaisVinculados() {
        when(unidadeSaudeRepository.findById(1L)).thenReturn(Optional.of(entity));
        when(pacienteRepository.existsByUnidadeSaudeVinculadaId(1L)).thenReturn(false);
        when(profissionalRepository.existsByUbsVinculadaId(1L)).thenReturn(true);

        assertThrows(OperacaoInvalidaException.class, () -> unidadeSaudeService.deletarUnidade(1L));
        verify(unidadeSaudeRepository, never()).delete(any());
    }
}