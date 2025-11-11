package apa.ifpb.edu.br.APA.service;

import apa.ifpb.edu.br.APA.dto.AdminResponseDTO;
import apa.ifpb.edu.br.APA.exception.RecursoNaoEncontradoException;
import apa.ifpb.edu.br.APA.mapper.AdminMapper;
import apa.ifpb.edu.br.APA.model.Admin;
import apa.ifpb.edu.br.APA.repository.AdminRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminService {
    private final AdminRepository adminRepository;
    private final AdminMapper adminMapper;

    //Busca todos os Admins
    @Transactional(readOnly = true)
    public List<AdminResponseDTO> listarTodos() {
        return adminRepository.findAll()
                .stream()
                .map(adminMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    //Busca um admin especifico pelo Id
    @Transactional(readOnly = true)
    public AdminResponseDTO buscarPorId(Long id) {
        Admin admin = adminRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Admin não encontrado com id: " + id));

        return adminMapper.toResponseDTO(admin);
    }

    //Busca um admin especifico pelo login
    @Transactional(readOnly = true)
    public AdminResponseDTO buscarPorLogin(String login) {
        Admin admin = adminRepository.findByUsuarioLogin(login)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Admin não encontrado com login: " + login));

        return adminMapper.toResponseDTO(admin);
    }

}
