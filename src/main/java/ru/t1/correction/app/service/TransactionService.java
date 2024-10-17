package ru.t1.correction.app.service;

import ru.t1.correction.app.model.FailedTransaction;
import ru.t1.correction.app.model.dto.FailedTransactionDto;

import java.util.List;

public interface TransactionService {

    void delete(FailedTransactionDto dto);

    void createRecord(FailedTransactionDto dto);

    public List<FailedTransaction> findAll();

}
