package ru.t1.correction.app.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.t1.correction.app.mapper.FailedTransactionMapper;
import ru.t1.correction.app.model.FailedTransaction;
import ru.t1.correction.app.model.dto.FailedTransactionDto;
import ru.t1.correction.app.repository.TransactionRepository;
import ru.t1.correction.app.service.TransactionService;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository repository;
    private final FailedTransactionMapper mapper;

    @Override
    @Transactional
    public void registerTransaction(List<FailedTransactionDto> messageList) {

        List<FailedTransaction> transactions = messageList.stream()
                .map(mapper::toEntity).toList();

        for(FailedTransaction transaction : transactions) {
            if (!repository.existsByOriginalTransactionId(transaction.getOriginalTransactionId())) {

                repository.save(transaction);
                log.info("The transaction with ID {} has been saved.", transaction);
            } else {
                log.info("The transaction with ID {} already exists.", transaction);
            }
        }
    }

    @Transactional
    public void deleteTransaction(FailedTransactionDto dto) {

        FailedTransaction failedTransaction = mapper.toEntity(dto);

        if(repository.existsByOriginalTransactionId(failedTransaction.getOriginalTransactionId())) {

            repository.delete(failedTransaction);
            log.info("FailedTransaction {} has been deleted", failedTransaction.getId());

        } else {
            log.info("FailedTransaction {} does not exist", failedTransaction.getId());
        }
    }

    public void createRecord(FailedTransactionDto dto) {

        FailedTransaction failedTransaction = mapper.toEntity(dto);
        repository.save(failedTransaction);
        log.info("The FailedTransaction {} is saved", failedTransaction.getId());
    }
}
