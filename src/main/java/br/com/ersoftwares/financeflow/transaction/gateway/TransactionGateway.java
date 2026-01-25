package br.com.ersoftwares.financeflow.transaction.gateway;

import br.com.ersoftwares.financeflow.transaction.domain.Transaction;

import java.util.List;
import java.util.Optional;

public interface TransactionGateway {

    Transaction save(Transaction transaction);

    Optional<Transaction> findById(Long id);

    Optional<Transaction> findByIdAndUserId(Long id, Long userId);

    List<Transaction> findByUserId(Long userId);

    void delete(Long id);

    boolean existsById(Long id);
}
