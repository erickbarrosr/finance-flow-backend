package br.com.ersoftwares.financeflow.user.service;

import br.com.ersoftwares.financeflow.common.exception.BusinessException;
import br.com.ersoftwares.financeflow.user.domain.User;
import br.com.ersoftwares.financeflow.user.gateway.UserGateway;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserGateway userGateway;
    private final PasswordEncoder passwordEncoder;

    public User register(User user, String rawPassword) {
        log.debug("Iniciando registro de usuário com email: {}", user.getEmail());

        if (userGateway.existsByEmail(user.getEmail())) {
            log.warn("Tentativa de registro com email já existente: {}", user.getEmail());
            throw new BusinessException("Email já cadastrado");
        }

        var passwordHash = passwordEncoder.encode(rawPassword);

        var userToSave = User.builder()
                .id(null)
                .name(user.getName())
                .email(user.getEmail())
                .passwordHash(passwordHash)
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();

        User savedUser = userGateway.save(userToSave);
        log.info("Usuário registrado com sucesso. ID: {}, Email: {}",
                savedUser.getId(), savedUser.getEmail());

        return savedUser;
    }
}
