package br.com.ersoftwares.financeflow.user.controller;

import br.com.ersoftwares.financeflow.user.dto.request.LoginRequestDTO;
import br.com.ersoftwares.financeflow.user.dto.response.LoginResponseDTO;
import br.com.ersoftwares.financeflow.user.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody @Valid LoginRequestDTO request) {
        log.info("Requisição de Login recebida para o email={}", request.getEmail());

        LoginResponseDTO response = authService.login(request);

        return ResponseEntity.ok(response);
    }
}
