package br.com.ersoftwares.financeflow.transaction.service;

import br.com.ersoftwares.financeflow.common.exception.BusinessException;
import br.com.ersoftwares.financeflow.transaction.domain.Transaction;
import br.com.ersoftwares.financeflow.transaction.gateway.TransactionGateway;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionService {

    private final TransactionGateway transactionGateway;

    public Transaction create(Transaction transaction) {
        log.debug("Criando transação para usuário: {}", transaction.getUserId());

        var saved = transactionGateway.save(transaction);
        log.info("Transação criada com sucesso. ID: {}, User ID: {}",
                saved.getId(), saved.getUserId());

        return saved;
    }

    public List<Transaction> listByUserId(Long userId) {
        log.debug("Listando transações para usuário: {}", userId);

        var transactions = transactionGateway.findByUserId(userId);
        log.info("Encontradas {} transações para usuário: {}", transactions.size(), userId);

        return transactions;
    }

    public Transaction findById(Long id, Long userId) {
        log.debug("Buscando transação com ID: {}, User ID: {}", id, userId);

        var transaction = transactionGateway.findByIdAndUserId(id, userId)
                .orElseThrow(() -> {
                    log.warn("Transação não encontrada. ID: {}, User ID: {}", id, userId);
                    return new BusinessException("Transação não encontrada");
                });

        return transaction;
    }

    public Transaction update(Long id, Long userId, Transaction updatedTransaction) {
        log.debug("Atualizando transação com ID: {}, User ID: {}", id, userId);

        var existing = findById(id, userId);

        var updated = transactionGateway.save(updatedTransaction);
        log.info("Transação atualizada com sucesso. ID: {}", updated.getId());

        return updated;
    }

    public void delete(Long id, Long userId) {
        log.debug("Deletando transação com ID: {}, User ID: {}", id, userId);

        var transaction = findById(id, userId);

        transactionGateway.delete(id);
        log.info("Transação deletada com sucesso. ID: {}", id);
    }
}
