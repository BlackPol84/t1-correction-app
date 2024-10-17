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

    @Transactional
    public void delete(FailedTransactionDto dto) {

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

        if(!repository.existsByOriginalTransactionId(failedTransaction.getOriginalTransactionId())) {
            repository.save(failedTransaction);
            log.info("The FailedTransaction {} is saved", failedTransaction.getId());
        }
    }

    public List<FailedTransaction> findAll() {
        return repository.findAll();
    }
}
