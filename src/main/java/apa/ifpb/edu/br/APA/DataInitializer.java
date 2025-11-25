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
       

        // ---------------------------------------------------------
        // 2. SETUP UNIDADE DE SAÚDE (PSF)
        // ---------------------------------------------------------
        Long unidadeId = null;
        // Verifica se já existe pelo CNES
        var unidadeExistente = unidadeSaudeRepository.findByCodigoCnes("0000000");

        if (unidadeExistente.isEmpty()) {
            UnidadeSaudeDTO dto = new UnidadeSaudeDTO();
            dto.setCodigoCnes("0000000");
            dto.setCep("58540000");
            dto.setCnpj("99999999000199");
            dto.setNome("PSF DE TESTE AUTOMATICO");
            /*dto.setLogradouro("Rua do Teste");
            dto.setBairro("Bairro Teste");
            dto.setMunicipio("Cidade Teste");
            dto.setUf("PB");*/

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
                profissionalService.criarProfissional(proDto);
                System.out.println(" > Profissional criado: Login '" + cpfProfissional + "' / Senha 'medico123'");
            } catch (Exception e) {
                System.err.println(" ! Erro ao criar Profissional: " + e.getMessage());
            }
        }

        // ---------------------------------------------------------
        // 4. SETUP PACIENTE
        // ---------------------------------------------------------
        // O login do paciente é o Email.
        String emailPaciente = "paciente.teste@email.com";
        if (usuarioRepository.findByLogin(emailPaciente).isEmpty()) {
            PacienteRequestDTO pacDto = new PacienteRequestDTO();
            pacDto.setNomeCompleto("Paciente Exemplo da Silva");
            pacDto.setCpf("28551036491");
            pacDto.setCep("58540000");
            pacDto.setCns("200000000000002");
            pacDto.setDataNascimento(LocalDate.of(1990, 1, 1));
            pacDto.setSexo(Sexo.MASCULINO);
            pacDto.setRacacor(RacaCor.PARDA);
            pacDto.setTelefone("83988887777");
            pacDto.setEmail(emailPaciente);
            /*pacDto.setLogradouro("Rua dos Pacientes, 100");
            pacDto.setBairro("Centro");
            pacDto.setMunicipio("Cidade Teste");
            pacDto.setUf("PB");*/
            pacDto.setSenha("paciente123"); // Senha do paciente
            pacDto.setUnidadeSaudeId(unidadeId); // Vincula à unidade criada acima

            try {
                pacienteService.criarPaciente(pacDto);
                System.out.println(" > Paciente criado: Login '" + emailPaciente + "' / Senha 'paciente123'");
            } catch (Exception e) {
                System.err.println(" ! Erro ao criar Paciente: " + e.getMessage());
            }
        }

        System.out.println(">>> DATA INITIALIZER CONCLUÍDO <<<");
    }
    
}