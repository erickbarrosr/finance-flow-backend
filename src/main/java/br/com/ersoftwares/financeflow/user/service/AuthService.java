package br.com.ersoftwares.financeflow.user.service;

import br.com.ersoftwares.financeflow.common.exception.BusinessException;
import br.com.ersoftwares.financeflow.config.security.jwt.JwtService;
import br.com.ersoftwares.financeflow.user.domain.User;
import br.com.ersoftwares.financeflow.user.dto.request.LoginRequestDTO;
import br.com.ersoftwares.financeflow.user.dto.response.LoginResponseDTO;
import br.com.ersoftwares.financeflow.user.gateway.UserGateway;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserGateway userGateway;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public LoginResponseDTO login(LoginRequestDTO request) {
        log.info("Tentativa de login para o email: {}", request.getEmail());

        User user = userGateway.findByEmail(request.getEmail())
                .orElseThrow(() -> {
                    log.warn("Tentativa de login com email não cadastrado: {}", request.getEmail());
                    return new BusinessException(
                            "INVALID_CREDENTIALS",
                            "E-mail ou senha inválidos"
                    );
                });

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            log.warn("Tentativa de login com senha incorreta para: {}", request.getEmail());
            throw new BusinessException(
                    "INVALID_CREDENTIALS",
                    "E-mail ou senha inválidos"
            );
        }

        String token = jwtService.generateToken(user.getId(), user.getEmail());

        log.info("Login realizado com sucesso para: {} (ID: {})", user.getEmail(), user.getId());

        return LoginResponseDTO.builder()
                .token(token)
                .build();
    }
}
