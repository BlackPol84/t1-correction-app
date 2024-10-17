package ru.t1.correction.app.service;

import ru.t1.correction.app.model.dto.FailedTransactionDto;

import java.util.List;

public interface TransactionService {

    void registerTransaction(List<FailedTransactionDto> messageList);

    void deleteTransaction(FailedTransactionDto dto);

    void createRecord(FailedTransactionDto dto);

}
