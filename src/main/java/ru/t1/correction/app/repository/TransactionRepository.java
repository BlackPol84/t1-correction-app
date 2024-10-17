package ru.t1.correction.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.t1.correction.app.model.FailedTransaction;

import java.util.List;
import java.util.Optional;

public interface TransactionRepository extends JpaRepository<FailedTransaction, Long> {

    boolean existsByOriginalTransactionId(Long originalTransactionId);

    @Override
    List<FailedTransaction> findAll();

}
