package br.com.ersoftwares.financeflow.user;

import br.com.ersoftwares.financeflow.user.domain.User;
import br.com.ersoftwares.financeflow.user.dto.request.CreateUserRequestDTO;
import br.com.ersoftwares.financeflow.user.dto.request.LoginRequestDTO;
import br.com.ersoftwares.financeflow.user.dto.response.LoginResponseDTO;
import br.com.ersoftwares.financeflow.user.gateway.UserGateway;
import br.com.ersoftwares.financeflow.user.mapper.UserDTOMapper;
import br.com.ersoftwares.financeflow.user.service.AuthService;
import br.com.ersoftwares.financeflow.user.service.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.UUID;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("local")
public class AuthIntegrationTest {

    @Autowired
    private UserService userService;

    @Autowired
    private AuthService authService;

    @Autowired
    private UserGateway userGateway;

    @Test
    void registerAndLogin() {
        String uniqueEmail = "test.user+" + UUID.randomUUID() + "@example.com";

        var createDto = CreateUserRequestDTO.builder()
                .email(uniqueEmail)
                .name("Test User")
                .password("password123")
                .build();

        User domain = UserDTOMapper.toDomain(createDto, null);
        var saved = userService.register(domain, createDto.getPassword());

        Assertions.assertNotNull(saved.getId());

        var loginReq = LoginRequestDTO.builder()
                .email(createDto.getEmail())
                .password(createDto.getPassword())
                .build();

        LoginResponseDTO resp = authService.login(loginReq);
        Assertions.assertNotNull(resp.getToken());
        Assertions.assertFalse(resp.getToken().isBlank());
    }
}
