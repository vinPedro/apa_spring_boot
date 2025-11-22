// Caminho: src/main/java/apa/ifpb/edu/br/APA/DataInitializer.java
package apa.ifpb.edu.br.APA;

import apa.ifpb.edu.br.APA.dto.UnidadeSaudeDTO;
import apa.ifpb.edu.br.APA.model.Admin;
import apa.ifpb.edu.br.APA.model.Role;
import apa.ifpb.edu.br.APA.model.Usuario;
import apa.ifpb.edu.br.APA.repository.AdminRepository;
import apa.ifpb.edu.br.APA.repository.UnidadeSaudeRepository;
import apa.ifpb.edu.br.APA.repository.UsuarioRepository;
import apa.ifpb.edu.br.APA.service.UnidadeSaudeService; // IMPORTAR
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;

    // --- NOVAS DEPENDÊNCIAS PARA O TESTE ---
    private final UnidadeSaudeService unidadeSaudeService;
    private final UnidadeSaudeRepository unidadeSaudeRepository;


    @Override
    public void run(String... args) throws Exception {

        /**
         * --- Bloco 1: Criar Admin (como antes) ---
         *         if (usuarioRepository.findByLogin("admin").isEmpty()) {
         *
         *             Usuario adminUser = new Usuario();
         *             adminUser.setLogin("admin");
         *             adminUser.setSenha(passwordEncoder.encode("admin123"));
         *             adminUser.setRole(Role.ROLE_ADMIN);
         *
         *             Admin adminProfile = new Admin();
         *             adminProfile.setUsuario(adminUser);
         *
         *             adminRepository.save(adminProfile);
         *
         *             System.out.println("**************************************************");
         *             System.out.println(">>> USUÁRIO ADMIN PADRÃO CRIADO COM SUCESSO <<<");
         *             System.out.println(">>> Login: admin / Senha: admin123           <<<");
         *             System.out.println("**************************************************");
         *         }
         *
         */

        System.out.println(">>> Aplicação Inicializada <<<");
    }
}