package br.com.ersoftwares.financeflow.user.controller;

import br.com.ersoftwares.financeflow.user.dto.request.CreateUserRequestDTO;
import br.com.ersoftwares.financeflow.user.dto.response.UserResponseDTO;
import br.com.ersoftwares.financeflow.user.mapper.UserDTOMapper;
import br.com.ersoftwares.financeflow.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<UserResponseDTO> register(
            @RequestBody @Valid CreateUserRequestDTO dto
    ) {
        log.info("Recebida requisição de registro de usuário: {}", dto.getEmail());

        var domain = UserDTOMapper.toDomain(dto, null);
        var saved = userService.register(domain, dto.getEmail());
        var response = UserDTOMapper.toResponse(saved);

        log.info("Usuário registrado com sucesso. ID: {}", saved.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
