package apa.ifpb.edu.br.APA.controller;

import apa.ifpb.edu.br.APA.dto.LoginRequestDTO;
import apa.ifpb.edu.br.APA.dto.LoginResponseDTO;
import apa.ifpb.edu.br.APA.model.Usuario;
import apa.ifpb.edu.br.APA.security.TokenService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody @Valid LoginRequestDTO dto) {

        var usernamePassword = new UsernamePasswordAuthenticationToken(
                dto.getLogin(),
                dto.getSenha()
        );

        var auth = this.authenticationManager.authenticate(usernamePassword);

        var usuario = (Usuario) auth.getPrincipal();

        var token = tokenService.gerarToken(usuario);

        return ResponseEntity.ok(new LoginResponseDTO(token));
    }
}