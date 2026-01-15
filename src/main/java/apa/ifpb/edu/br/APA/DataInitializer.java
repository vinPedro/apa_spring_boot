// Caminho: src/main/java/apa/ifpb/edu/br/APA/DataInitializer.java
package apa.ifpb.edu.br.APA;

import apa.ifpb.edu.br.APA.dto.PacienteRequestDTO;
import apa.ifpb.edu.br.APA.dto.ProfissionalRequestDTO;
import apa.ifpb.edu.br.APA.dto.UnidadeSaudeDTO;
import apa.ifpb.edu.br.APA.model.*;
import apa.ifpb.edu.br.APA.repository.AdminRepository;
import apa.ifpb.edu.br.APA.repository.UnidadeSaudeRepository;
import apa.ifpb.edu.br.APA.repository.UsuarioRepository;
import apa.ifpb.edu.br.APA.service.PacienteService;
import apa.ifpb.edu.br.APA.service.ProfissionalService;
import apa.ifpb.edu.br.APA.service.UnidadeSaudeService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;

    private final UnidadeSaudeService unidadeSaudeService;
    private final UnidadeSaudeRepository unidadeSaudeRepository;

    private final ProfissionalService profissionalService;
    private final PacienteService pacienteService;

    @Override
    public void run(String... args) throws Exception {

        System.out.println(">>> INICIANDO DATA INITIALIZER <<<");

        // ---------------------------------------------------------
        // 1. SETUP ADMIN
        // ---------------------------------------------------------
        if (usuarioRepository.findByLogin("admin").isEmpty()) {
            Usuario adminUser = new Usuario();
            adminUser.setLogin("admin");
            adminUser.setSenha(passwordEncoder.encode("admin123"));
            adminUser.setRole(Role.ROLE_ADMIN);

            Admin adminProfile = new Admin();
            adminProfile.setUsuario(adminUser);
            adminRepository.save(adminProfile);

            System.out.println(" > Admin criado: Login 'admin' / Senha 'admin123'");
        }

        // ---------------------------------------------------------
        // 2. SETUP UNIDADE DE SAÚDE (PSF)
        // ---------------------------------------------------------
        Long unidadeId = null;
        // Verifica se já existe pelo CNES
        var unidadeExistente = unidadeSaudeRepository.findByCodigoCnes("0000000");

        if (unidadeExistente.isEmpty()) {
            UnidadeSaudeDTO dto = new UnidadeSaudeDTO();
            dto.setCodigoCnes("0000000");
            dto.setCep("58540000"); // CEP Válido para o ViaCEP buscar
            dto.setCnpj("99999999000199");
            dto.setNome("PSF DE TESTE AUTOMATICO");
            // Endereço será preenchido pelo Service via CEP (se implementado no UnidadeService também)
            // Caso contrário, pode descomentar abaixo se o UnidadeService NÃO usar ViaCEP
            /* dto.setLogradouro("Rua do Teste");
            dto.setBairro("Bairro Teste");
            dto.setMunicipio("Cidade Teste");
            dto.setUf("PB");
            */

            UnidadeSaudeDTO salva = unidadeSaudeService.criarUnidade(dto);
            unidadeId = salva.getId();
            System.out.println(" > Unidade de Saúde criada: " + salva.getNome() + " (ID: " + unidadeId + ")");
        } else {
            unidadeId = unidadeExistente.get().getId();
            System.out.println(" > Unidade de Saúde já existe (ID: " + unidadeId + ")");
        }

        // ---------------------------------------------------------
        // 3. SETUP PROFISSIONAL
        // ---------------------------------------------------------
        // O login do profissional é o CPF. Vamos usar um CPF de teste.
        String cpfProfissional = "02902248458";

        if (usuarioRepository.findByLogin(cpfProfissional).isEmpty()) {
            ProfissionalRequestDTO proDto = new ProfissionalRequestDTO();
            proDto.setNomeCompleto("Dr. Médico de Teste");
            proDto.setCpf(cpfProfissional);
            proDto.setCns("100000000000001");
            proDto.setConselhoProfissional(ConselhoProfissional.CRM);
            proDto.setRegistroConselho("12345-PB");
            proDto.setUfConselho("PB");
            proDto.setUbsVinculadaId(unidadeId); // Vincula à unidade criada acima
            proDto.setEmailInstitucional("medico.teste@apa.gov.br");
            proDto.setTelefoneContato("83999990000");
            proDto.setSenha("medico123"); // Senha do profissional

            try {
                // O Service vai buscar endereço pelo CEP (se houver CEP no DTO) e criar o Usuário com Login=CPF
                profissionalService.criarProfissional(proDto);
                System.out.println(" > Profissional criado: Login '" + cpfProfissional + "' / Senha 'medico123'");
            } catch (Exception e) {
                System.err.println(" ! Erro ao criar Profissional: " + e.getMessage());
            }
        }

        // ---------------------------------------------------------
        // 4. SETUP PACIENTE
        // ---------------------------------------------------------
        // --- CORREÇÃO CARD 3: Login deve ser o CPF ---
        String cpfPaciente = "28551036491";

        // Verificamos se existe pelo CPF (que é o login agora)
        if (usuarioRepository.findByLogin(cpfPaciente).isEmpty()) {
            PacienteRequestDTO pacDto = new PacienteRequestDTO();
            pacDto.setNomeCompleto("Paciente Exemplo da Silva");
            pacDto.setCpf(cpfPaciente);

            pacDto.setCep("58540000"); // ViaCEP vai preencher logradouro, bairro, etc.
            pacDto.setCns("200000000000002");
            pacDto.setDataNascimento(LocalDate.of(1990, 1, 1));
            pacDto.setSexo(Sexo.MASCULINO);
            pacDto.setRacacor(RacaCor.PARDA);
            pacDto.setTelefone("83988887777");
            pacDto.setEmail("paciente.teste@email.com"); // Email informativo

            // Campos de endereço comentados pois o Service usa ViaCEP
            /*
            pacDto.setLogradouro("Rua dos Pacientes, 100");
            pacDto.setBairro("Centro");
            pacDto.setMunicipio("Cidade Teste");
            pacDto.setUf("PB");
            */

            pacDto.setSenha("paciente123"); // Senha do paciente
            pacDto.setUnidadeSaudeId(unidadeId); // Vincula à unidade criada acima

            try {
                // O Service cria o Usuário automaticamente com Login = CPF
                pacienteService.criarPaciente(pacDto);
                System.out.println(" > Paciente criado: Login '" + cpfPaciente + "' / Senha 'paciente123'");
            } catch (Exception e) {
                System.err.println(" ! Erro ao criar Paciente: " + e.getMessage());
            }
        }

        System.out.println(">>> DATA INITIALIZER CONCLUÍDO <<<");
    }

}