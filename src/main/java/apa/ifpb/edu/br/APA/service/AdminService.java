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
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminService {
    private final AdminRepository adminRepository;
    private final AdminMapper adminMapper;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public AdminResponseDTO criarAdmin(AdminRequestDTO dto) {

        if (usuarioRepository.findByLogin(dto.getLogin()).isPresent()) {
            throw new OperacaoInvalidaException("Já existe um usuário com o login: " + dto.getLogin());
        }

        Usuario usuario = new Usuario();
        usuario.setLogin(dto.getLogin());
        usuario.setSenha(passwordEncoder.encode(dto.getSenha())); // Criptografa
        usuario.setRole(Role.ROLE_ADMIN); // Define como Admin

        Admin admin = new Admin();
        admin.setUsuario(usuario);

        Admin salvo = adminRepository.save(admin);
        return adminMapper.toResponseDTO(salvo);
    }

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
