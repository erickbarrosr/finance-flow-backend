package br.com.ersoftwares.financeflow.transaction.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Builder
public class Transaction {

    private final Long id;

    @NonNull
    private final Long userId;

    @NonNull
    private final String type;

    private final String description;

    @NonNull
    private final BigDecimal amount;

    @NonNull
    private final String category;

    private final LocalDate date;

    private final LocalDateTime createdAt;

    private final LocalDateTime updatedAt;

    private Transaction(
            Long id,
            Long userId,
            String type,
            String description,
            BigDecimal amount,
            String category,
            LocalDate date,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
        if (userId == null) {
            throw new IllegalArgumentException("User ID é obrigatório");
        }
        if (type == null || type.isBlank()) {
            throw new IllegalArgumentException("Tipo de transação é obrigatório");
        }
        if (amount == null) {
            throw new IllegalArgumentException("Valor é obrigatório");
        }
        if (category == null || category.isBlank()) {
            throw new IllegalArgumentException("Categoria é obrigatória");
        }

        this.id = id;
        this.userId = userId;
        this.type = type;
        this.description = description;
        this.amount = amount;
        this.category = category;
        this.date = date;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
