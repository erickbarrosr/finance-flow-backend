package br.com.ersoftwares.financeflow.transaction.dataprovider;

import br.com.ersoftwares.financeflow.transaction.dataprovider.mapper.TransactionEntityMapper;
import br.com.ersoftwares.financeflow.transaction.domain.Transaction;
import br.com.ersoftwares.financeflow.transaction.gateway.TransactionGateway;
import br.com.ersoftwares.financeflow.transaction.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class TransactionDataProvider implements TransactionGateway {

    private final TransactionRepository transactionRepository;

    @Override
    public Transaction save(Transaction transaction) {
        var entity = TransactionEntityMapper.toEntity(transaction);
        var saved = transactionRepository.save(entity);
        return TransactionEntityMapper.toDomain(saved);
    }

    @Override
    public Optional<Transaction> findById(Long id) {
        return transactionRepository.findById(id)
                .map(TransactionEntityMapper::toDomain);
    }

    @Override
    public Optional<Transaction> findByIdAndUserId(Long id, Long userId) {
        return transactionRepository.findByIdAndUserId(id, userId)
                .map(TransactionEntityMapper::toDomain);
    }

    @Override
    public List<Transaction> findByUserId(Long userId) {
        return transactionRepository.findByUserId(userId)
                .stream()
                .map(TransactionEntityMapper::toDomain)
                .toList();
    }

    @Override
    public void delete(Long id) {
        transactionRepository.deleteById(id);
    }

    @Override
    public boolean existsById(Long id) {
        return transactionRepository.existsById(id);
    }
}
