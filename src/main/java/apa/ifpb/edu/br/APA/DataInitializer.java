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

        // --- Bloco 1: Criar Admin (como antes) ---
        if (usuarioRepository.findByLogin("admin").isEmpty()) {

            Usuario adminUser = new Usuario();
            adminUser.setLogin("admin");
            adminUser.setSenha(passwordEncoder.encode("admin123"));
            adminUser.setRole(Role.ROLE_ADMIN);

            Admin adminProfile = new Admin();
            adminProfile.setUsuario(adminUser);

            adminRepository.save(adminProfile);

            System.out.println("**************************************************");
            System.out.println(">>> USUÁRIO ADMIN PADRÃO CRIADO COM SUCESSO <<<");
            System.out.println(">>> Login: admin / Senha: admin123           <<<");
            System.out.println("**************************************************");
        }

        // --- Bloco 2: O TESTE DEFINITIVO (Criar Unidade de Saúde) ---
        // Vamos tentar criar uma unidade com CNES "0000000"
        if (unidadeSaudeRepository.findByCodigoCnes("0000000").isEmpty()) {

            System.out.println(">>> INICIANDO TESTE: Criando Unidade de Saúde via Java...");

            // 1. Criamos um DTO limpo, em Java.
            UnidadeSaudeDTO dto = new UnidadeSaudeDTO();
            dto.setCodigoCnes("0000000");
            dto.setCnpj("99999999000199");
            dto.setNome("PSF DE TESTE DO INITIALIZER"); // <-- O CAMPO 'NOME' NÃO É NULO
            dto.setLogradouro("Rua do Teste");
            dto.setBairro("Bairro Teste");
            dto.setMunicipio("Cidade Teste");
            dto.setUf("PB");

            try {
                // 2. Chamamos o MESMO service que o Controller chama
                unidadeSaudeService.criarUnidade(dto);

                System.out.println(">>> TESTE OK: Unidade de Saúde de teste criada com SUCESSO.");
            } catch (Exception e) {
                System.err.println(">>> TESTE FALHOU: FALHA AO CRIAR UNIDADE DE TESTE: " + e.getMessage());
                e.printStackTrace(); // Imprime o stack trace completo do erro
            }
        }
    }
}