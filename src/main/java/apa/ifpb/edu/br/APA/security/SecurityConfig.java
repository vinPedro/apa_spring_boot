// Caminho: src/main/java/apa/ifpb/edu/br/APA/security/SecurityConfig.java
package apa.ifpb.edu.br.APA.security;

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

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(HttpMethod.POST, "/api/auth/login").permitAll()

                        .requestMatchers("/api/pacientes/**").hasRole("ADMIN")

                        .requestMatchers("/api/profissionais/**").hasRole("ADMIN")

                        .requestMatchers("/api/unidades/**").hasRole("ADMIN")

                        .requestMatchers("/api/especialidades/**").hasRole("ADMIN")

                        .requestMatchers("/api/admins/**").hasRole("ADMIN")

                        .anyRequest().authenticated()
                );

        // (VAMOS ADICIONAR O FILTRO JWT AQUI DAQUI A POUCO)

        return http.build();
    }
}