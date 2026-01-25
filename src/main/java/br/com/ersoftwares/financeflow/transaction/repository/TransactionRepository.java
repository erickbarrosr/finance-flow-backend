package br.com.ersoftwares.financeflow.transaction.repository;

import br.com.ersoftwares.financeflow.transaction.dataprovider.entity.TransactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TransactionRepository extends JpaRepository<TransactionEntity, Long> {

    @Query("SELECT t FROM TransactionEntity t WHERE t.userId = :userId ORDER BY t.date DESC")
    List<TransactionEntity> findByUserId(@Param("userId") Long userId);

    @Query("SELECT t FROM TransactionEntity t WHERE t.userId = :userId AND t.id = :id")
    java.util.Optional<TransactionEntity> findByIdAndUserId(
            @Param("id") Long id,
            @Param("userId") Long userId
    );
}
