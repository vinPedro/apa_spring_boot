package apa.ifpb.edu.br.APA;

import apa.ifpb.edu.br.APA.dto.UnidadeSaudeDTO;
import apa.ifpb.edu.br.APA.repository.UnidadeSaudeRepository;
import apa.ifpb.edu.br.APA.service.UnidadeSaudeService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UnidadeSaudeService unidadeSaudeService;
    private final UnidadeSaudeRepository unidadeSaudeRepository;

    // private final UsuarioRepository usuarioRepository;
    // private final AdminRepository adminRepository;
    // private final PasswordEncoder passwordEncoder;
    // private final ProfissionalService profissionalService;
    // private final PacienteService pacienteService;

    @Override
    public void run(String... args) throws Exception {

        System.out.println(">>> INICIANDO DATA INITIALIZER <<<");

        // ---------------------------------------------------------
        // 1. SETUP ADMIN (COMENTADO PARA TESTE DE PRIMEIRO ACESSO)
        // ---------------------------------------------------------
        /*
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
        */

        // ---------------------------------------------------------
        // 2. SETUP UNIDADE DE SAÚDE (PSF) - ESTE VAI RODAR!
        // ---------------------------------------------------------
        Long unidadeId = null;
        
        // Verifica se já existe pelo CNES (usando um número válido ou fictício)
        String cnesTeste = "0000000"; 
        var unidadeExistente = unidadeSaudeRepository.findByCodigoCnes(cnesTeste);

        if (unidadeExistente.isEmpty()) {
            UnidadeSaudeDTO dto = new UnidadeSaudeDTO();
            dto.setCodigoCnes(cnesTeste);
            dto.setCep("58500000"); // CEP de Monteiro/PB (Exemplo)
            dto.setCnpj("12345678000199"); // CNPJ limpo (apenas números)
            dto.setNome("PSF DE TESTE AUTOMATICO");
            dto.setMunicipio("Monteiro");
            dto.setUf("PB");
            dto.setLogradouro("Rua Principal");
            dto.setBairro("Centro");

            try {
                UnidadeSaudeDTO salva = unidadeSaudeService.criarUnidade(dto);
                unidadeId = salva.getId();
                System.out.println(" > Unidade de Saúde criada: " + salva.getNome() + " (ID: " + unidadeId + ")");
            } catch (Exception e) {
                System.err.println(" ! Erro ao criar Unidade: " + e.getMessage());
            }
        } else {
            unidadeId = unidadeExistente.get().getId();
            System.out.println(" > Unidade de Saúde já existe (ID: " + unidadeId + ")");
        }

        // ---------------------------------------------------------
        // 3. SETUP PROFISSIONAL (COMENTADO)
        // ---------------------------------------------------------
        /*
        String cpfProfissional = "02902248458";
        if (usuarioRepository.findByLogin(cpfProfissional).isEmpty()) {
            ProfissionalRequestDTO proDto = new ProfissionalRequestDTO();
            proDto.setNomeCompleto("Dr. Médico de Teste");
            proDto.setCpf(cpfProfissional);
            proDto.setCns("100000000000001");
            proDto.setConselhoProfissional(ConselhoProfissional.CRM);
            proDto.setRegistroConselho("12345-PB");
            proDto.setUfConselho("PB");
            proDto.setUbsVinculadaId(unidadeId);
            proDto.setEmailInstitucional("medico.teste@apa.gov.br");
            proDto.setTelefoneContato("83999990000");
            proDto.setSenha("medico123");

            try {
                profissionalService.criarProfissional(proDto);
                System.out.println(" > Profissional criado: Login '" + cpfProfissional + "' / Senha 'medico123'");
            } catch (Exception e) {
                System.err.println(" ! Erro ao criar Profissional: " + e.getMessage());
            }
        }
        */

        // ---------------------------------------------------------
        // 4. SETUP PACIENTE (COMENTADO)
        // ---------------------------------------------------------
        /*
        String emailPaciente = "paciente.teste@email.com";
        if (usuarioRepository.findByLogin(emailPaciente).isEmpty()) {
            PacienteRequestDTO pacDto = new PacienteRequestDTO();
            pacDto.setNomeCompleto("Paciente Exemplo da Silva");
            // Use um CPF válido gerado para evitar erro de validação
            pacDto.setCpf("43396860025"); 
            pacDto.setCep("58500000");
            pacDto.setCns("200000000000002");
            pacDto.setDataNascimento(LocalDate.of(1990, 1, 1));
            pacDto.setSexo(Sexo.MASCULINO);
            pacDto.setRacacor(RacaCor.PARDA);
            pacDto.setTelefone("83988887777");
            pacDto.setEmail(emailPaciente);
            pacDto.setSenha("paciente123");
            pacDto.setUnidadeSaudeId(unidadeId);

            try {
                pacienteService.criarPaciente(pacDto);
                System.out.println(" > Paciente criado: Login '" + emailPaciente + "' / Senha 'paciente123'");
            } catch (Exception e) {
                System.err.println(" ! Erro ao criar Paciente: " + e.getMessage());
            }
        }
        */

        System.out.println(">>> DATA INITIALIZER CONCLUÍDO <<<");
    }
}