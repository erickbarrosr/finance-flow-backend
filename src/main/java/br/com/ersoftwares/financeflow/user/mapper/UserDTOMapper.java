package br.com.ersoftwares.financeflow.user.mapper;

import br.com.ersoftwares.financeflow.user.domain.User;
import br.com.ersoftwares.financeflow.user.dto.request.CreateUserRequestDTO;
import br.com.ersoftwares.financeflow.user.dto.response.UserResponseDTO;
import lombok.experimental.UtilityClass;

import java.time.LocalDateTime;

@UtilityClass
public class UserDTOMapper {

    public User toDomain(
            CreateUserRequestDTO dto,
            String passwordHash
    ) {
        var now = LocalDateTime.now();

        return User.builder()
                .id(null)
                .name(dto.getName())
                .email(dto.getEmail())
                .passwordHash(passwordHash)
                .createdAt(now)
                .updatedAt(now)
                .build();
    }

    public UserResponseDTO toResponse(User user) {
        return UserResponseDTO.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .createdAt(user.getCreatedAt())
                .build();
    }
}
