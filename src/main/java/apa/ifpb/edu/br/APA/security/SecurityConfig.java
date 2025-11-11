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

// <-- NOVOS IMPORTS
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import java.util.Arrays;
// -->

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
        // Permite requisições da origem do seu frontend React
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000")); 
        // Permite os métodos HTTP que sua API usa
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "HEAD")); 
        // Permite cabeçalhos comuns e o cabeçalho "Authorization" (para o token JWT)
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "Accept")); 
        configuration.setAllowCredentials(true);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration); // Aplica para todas as URLs
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
                        .requestMatchers(HttpMethod.POST, "/api/auth/login").permitAll()
                        .requestMatchers("/h2-console/**").permitAll()
                        
                        // Permite que QUALQUER UM crie um novo paciente (cadastro público)
                        .requestMatchers(HttpMethod.POST, "/api/pacientes").permitAll() 
                        
                        // O resto (GET, PUT, DELETE) continua protegido
                        .requestMatchers("/api/pacientes/**").hasRole("ADMIN") 
                        // --- FIM DA MUDANÇA ---

                        .requestMatchers("/api/profissionais/**").hasRole("ADMIN")
                        .requestMatchers("/api/unidades/**").hasRole("ADMIN")
                        .requestMatchers("/api/especialidades/**").hasRole("ADMIN")
                        .requestMatchers("/api/admins/**").hasRole("ADMIN")

                        .anyRequest().authenticated()
                )

                .addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}