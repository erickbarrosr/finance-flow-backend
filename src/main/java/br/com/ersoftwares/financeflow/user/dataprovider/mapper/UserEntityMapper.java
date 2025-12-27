package br.com.ersoftwares.financeflow.user.dataprovider.mapper;

import br.com.ersoftwares.financeflow.user.dataprovider.entity.UserEntity;
import br.com.ersoftwares.financeflow.user.domain.User;
import lombok.experimental.UtilityClass;

@UtilityClass
public class UserEntityMapper {

    public UserEntity toEntity(User user) {
        return UserEntity.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .passwordHash(user.getPasswordHash())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }

    public User toDomain(UserEntity entity) {
        return User.builder()
                .id(entity.getId())
                .name(entity.getName())
                .email(entity.getEmail())
                .passwordHash(entity.getPasswordHash())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
