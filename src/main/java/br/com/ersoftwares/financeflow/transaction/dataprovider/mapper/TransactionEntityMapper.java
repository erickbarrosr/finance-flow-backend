package br.com.ersoftwares.financeflow.transaction.dataprovider.mapper;

import br.com.ersoftwares.financeflow.transaction.dataprovider.entity.TransactionEntity;
import br.com.ersoftwares.financeflow.transaction.domain.Transaction;
import lombok.experimental.UtilityClass;

@UtilityClass
public class TransactionEntityMapper {

    public TransactionEntity toEntity(Transaction transaction) {
        return TransactionEntity.builder()
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

    public Transaction toDomain(TransactionEntity entity) {
        return Transaction.builder()
                .id(entity.getId())
                .userId(entity.getUserId())
                .type(entity.getType())
                .description(entity.getDescription())
                .amount(entity.getAmount())
                .category(entity.getCategory())
                .date(entity.getDate())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
