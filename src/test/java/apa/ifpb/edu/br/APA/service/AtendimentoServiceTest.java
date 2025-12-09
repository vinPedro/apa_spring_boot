package apa.ifpb.edu.br.APA.service;

import apa.ifpb.edu.br.APA.dto.AtendimentoRequestDTO;
import apa.ifpb.edu.br.APA.dto.AtendimentoResponseDTO;
import apa.ifpb.edu.br.APA.exception.RecursoNaoEncontradoException;
import apa.ifpb.edu.br.APA.mapper.AtendimentoMapper;
import apa.ifpb.edu.br.APA.model.*;
import apa.ifpb.edu.br.APA.repository.AtendimentoRepository;
import apa.ifpb.edu.br.APA.repository.PacienteRepository;
import apa.ifpb.edu.br.APA.repository.UnidadeSaudeRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AtendimentoServiceTest {

    @Mock
    private AtendimentoRepository atendimentoRepository;

    @Mock
    private PacienteRepository pacienteRepository;

    @Mock
    private UnidadeSaudeRepository unidadeSaudeRepository;

    @Mock
    private AtendimentoMapper atendimentoMapper;

    @InjectMocks
    private AtendimentoService atendimentoService;


    @Test
    @DisplayName("Deve gerar senha NORMAL (P1) quando é o primeiro do dia")
    void entrarNaFila_Normal_PrimeiroDoDia() {
        AtendimentoRequestDTO dto = criarDto(TipoPrioridade.NORMAL);

        mockRepositoriesFind(true, true);

        when(atendimentoRepository.contarAtendimentosDoDia(eq(10L), eq(TipoPrioridade.NORMAL), any(LocalDateTime.class)))
                .thenReturn(0L);

        Atendimento atendimentoMock = new Atendimento();
        when(atendimentoMapper.toEntity(dto)).thenReturn(atendimentoMock);

        when(atendimentoRepository.save(any(Atendimento.class))).thenAnswer(inv -> {
            Atendimento a = inv.getArgument(0);
            a.setId(100L);
            return a;
        });

        when(atendimentoMapper.toResponseDTO(any())).thenReturn(new AtendimentoResponseDTO());

        atendimentoService.entrarNaFila(dto);
        assertEquals("P1", atendimentoMock.getSenha());
        assertEquals(StatusAtendimento.AGUARDANDO, atendimentoMock.getStatus());
        verify(atendimentoRepository).save(atendimentoMock);
    }

    @Test
    @DisplayName("Deve gerar senha PRIORIDADE (PR3) quando já existem 2 anteriores")
    void entrarNaFila_Prioridade_ComAnteriores() {
        AtendimentoRequestDTO dto = criarDto(TipoPrioridade.PRIORIDADE);

        mockRepositoriesFind(true, true);

        when(atendimentoRepository.contarAtendimentosDoDia(eq(10L), eq(TipoPrioridade.PRIORIDADE), any(LocalDateTime.class)))
                .thenReturn(2L);

        Atendimento atendimentoMock = new Atendimento();
        when(atendimentoMapper.toEntity(dto)).thenReturn(atendimentoMock);
        when(atendimentoRepository.save(any(Atendimento.class))).thenReturn(atendimentoMock);
        when(atendimentoMapper.toResponseDTO(any())).thenReturn(new AtendimentoResponseDTO());

        atendimentoService.entrarNaFila(dto);

        assertEquals("PR3", atendimentoMock.getSenha());
    }


    @Test
    @DisplayName("Deve lançar exceção se Paciente não existe")
    void entrarNaFila_PacienteInexistente() {
        AtendimentoRequestDTO dto = criarDto(TipoPrioridade.NORMAL);

        when(pacienteRepository.findById(dto.getPacienteId())).thenReturn(Optional.empty());

        assertThrows(RecursoNaoEncontradoException.class, () -> atendimentoService.entrarNaFila(dto));

        verify(atendimentoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção se Unidade de Saúde não existe")
    void entrarNaFila_UnidadeInexistente() {
        AtendimentoRequestDTO dto = criarDto(TipoPrioridade.NORMAL);

        mockRepositoriesFind(true, false);

        assertThrows(RecursoNaoEncontradoException.class, () -> atendimentoService.entrarNaFila(dto));

        verify(atendimentoRepository, never()).save(any());
    }


    private AtendimentoRequestDTO criarDto(TipoPrioridade prioridade) {
        AtendimentoRequestDTO dto = new AtendimentoRequestDTO();
        dto.setPacienteId(1L);
        dto.setUnidadeSaudeId(10L);
        dto.setPrioridade(prioridade);
        return dto;
    }

    private void mockRepositoriesFind(boolean pacienteExiste, boolean unidadeExiste) {
        if (pacienteExiste) {
            Paciente p = new Paciente();
            p.setId(1L);
            when(pacienteRepository.findById(1L)).thenReturn(Optional.of(p));
        }

        if (pacienteExiste && unidadeExiste) {
            UnidadeSaude u = new UnidadeSaude();
            u.setId(10L);
            when(unidadeSaudeRepository.findById(10L)).thenReturn(Optional.of(u));
        } else if (pacienteExiste && !unidadeExiste) {
            when(unidadeSaudeRepository.findById(10L)).thenReturn(Optional.empty());
        }
    }
}