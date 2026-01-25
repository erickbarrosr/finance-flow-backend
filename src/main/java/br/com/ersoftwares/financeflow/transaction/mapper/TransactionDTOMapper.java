package br.com.ersoftwares.financeflow.transaction.mapper;

import br.com.ersoftwares.financeflow.transaction.domain.Transaction;
import br.com.ersoftwares.financeflow.transaction.dto.request.CreateTransactionRequestDTO;
import br.com.ersoftwares.financeflow.transaction.dto.request.UpdateTransactionRequestDTO;
import br.com.ersoftwares.financeflow.transaction.dto.response.TransactionResponseDTO;
import lombok.experimental.UtilityClass;

import java.time.LocalDateTime;

@UtilityClass
public class TransactionDTOMapper {

    public Transaction toDomain(CreateTransactionRequestDTO dto, Long userId) {
        var now = LocalDateTime.now();

        return Transaction.builder()
                .id(null)
                .userId(userId)
                .type(dto.getType())
                .description(dto.getDescription())
                .amount(dto.getAmount())
                .category(dto.getCategory())
                .date(dto.getDate())
                .createdAt(now)
                .updatedAt(now)
                .build();
    }

    public Transaction toDomain(UpdateTransactionRequestDTO dto, Transaction existing) {
        return Transaction.builder()
                .id(existing.getId())
                .userId(existing.getUserId())
                .type(dto.getType() != null ? dto.getType() : existing.getType())
                .description(dto.getDescription() != null ? dto.getDescription() : existing.getDescription())
                .amount(dto.getAmount() != null ? dto.getAmount() : existing.getAmount())
                .category(dto.getCategory() != null ? dto.getCategory() : existing.getCategory())
                .date(dto.getDate() != null ? dto.getDate() : existing.getDate())
                .createdAt(existing.getCreatedAt())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    public TransactionResponseDTO toResponse(Transaction transaction) {
        return TransactionResponseDTO.builder()
                .id(transaction.getId())
                .userId(transaction.getUserId())
                .type(transaction.getType())
                .description(transaction.getDescription())
                .amount(transaction.getAmount())
                .category(transaction.getCategory())
                .date(transaction.getDate())
                .createdAt(transaction.getCreatedAt())
                .updatedAt(transaction.getUpdatedAt())
                .build();
    }
}
