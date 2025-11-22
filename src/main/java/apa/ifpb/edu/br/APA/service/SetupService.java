package apa.ifpb.edu.br.APA.service;

import apa.ifpb.edu.br.APA.dto.AdminRequestDTO;
import apa.ifpb.edu.br.APA.exception.OperacaoInvalidaException;
import apa.ifpb.edu.br.APA.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SetupService {

    private final UsuarioRepository usuarioRepository;
    private final AdminService adminService; // Injeta o AdminService

    public boolean isSistemaInicializado() {
        return usuarioRepository.count() > 0;
    }

    @Transactional
    public void criarPrimeiroAdmin(AdminRequestDTO dto) {
        // Trava de segurança
        if (isSistemaInicializado()) {
            throw new OperacaoInvalidaException("O sistema já possui um administrador configurado.");
        }

        // Reutiliza a lógica centralizada
        adminService.criarAdmin(dto);
    }
}