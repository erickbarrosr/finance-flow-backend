package br.com.ersoftwares.financeflow.user.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

import java.time.LocalDateTime;

@Getter
@Builder
public class User {

    private final Long id;

    @NonNull
    private final String name;

    @NonNull
    private final String email;

    private final String passwordHash;

    private final LocalDateTime createdAt;

    private final LocalDateTime updatedAt;

    private User(
            Long id,
            String name,
            String email,
            String passwordHash,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email é obrigatório");
        }

        this.id = id;
        this.name = name;
        this.email = email;
        this.passwordHash = passwordHash;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
