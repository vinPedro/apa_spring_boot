package apa.ifpb.edu.br.APA.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import java.util.Arrays;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final SecurityFilter securityFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000")); 
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "HEAD", "PATCH")); 
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "Accept")); 
        configuration.setAllowCredentials(true);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration); 
        return source;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .headers(headers -> headers.frameOptions(frameOptions -> frameOptions.disable()))

                .authorizeHttpRequests(authorize -> authorize
                        // --- ROTAS PÚBLICAS ---
                        .requestMatchers(HttpMethod.POST, "/api/auth/login").permitAll()
                        .requestMatchers("/h2-console/**").permitAll()
                        .requestMatchers("/api/setup/**").permitAll()
                        .requestMatchers("/error").permitAll()
                        // Cadastro de paciente é público
                        .requestMatchers(HttpMethod.POST, "/api/pacientes").permitAll() 

                        // --- ROTAS DE UNIDADES DE SAÚDE ---
                        // GET: Pacientes e Profissionais precisam listar as unidades
                        .requestMatchers(HttpMethod.GET, "/api/unidades/**").authenticated() 
                        // POST/PUT/DELETE: Só Admin gerencia unidades
                        .requestMatchers("/api/unidades/**").hasRole("ADMIN")

                        // --- ROTAS DE ATENDIMENTO (FILA) ---
                        // POST: Paciente entra na fila
                        .requestMatchers(HttpMethod.POST, "/api/atendimentos").hasAnyRole("PACIENTE", "ADMIN")
                        // GET: Profissional vê a fila (e Paciente talvez queira ver sua posição)
                        .requestMatchers(HttpMethod.GET, "/api/atendimentos/**").authenticated()
                        // PATCH: Profissional chama o paciente (altera status)
                        .requestMatchers(HttpMethod.PATCH, "/api/atendimentos/**").hasAnyRole("PROFISSIONAL", "ADMIN")

                        // --- ROTAS DE PACIENTES ---
                        // GET: Paciente precisa buscar seus dados (pelo email na lista)
                        .requestMatchers(HttpMethod.GET, "/api/pacientes/**").authenticated()
                        // PUT/DELETE: Admin ou o próprio (implementar segurança a nível de método futuramente)
                        .requestMatchers("/api/pacientes/**").hasRole("ADMIN")

                        // --- OUTRAS ROTAS ---
                        .requestMatchers("/api/profissionais/**").hasRole("ADMIN")
                        .requestMatchers("/api/especialidades/**").hasRole("ADMIN")
                        .requestMatchers("/api/admins/**").hasRole("ADMIN")

                        .anyRequest().authenticated()
                )

                .addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}