package ru.t1.correction.app.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.t1.correction.app.mapper.FailedTransactionMapper;
import ru.t1.correction.app.model.FailedTransaction;
import ru.t1.correction.app.model.dto.FailedTransactionDto;

import java.util.List;

import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.OK;

@Slf4j
@EnableScheduling
@RequiredArgsConstructor
@Service
public class FailedTransactionProcessor {

    private final TransactionService service;
    private final RestTemplate restTemplate;
    private final FailedTransactionMapper mapper;

    @Value("${spring.url.account.unlock}")
    private String unlockAccountUrl;

    private final long frequency = 5000L;

    public void processFailedTransactions(List<FailedTransactionDto> messageList) {

        for(FailedTransactionDto transactionDto : messageList) {
            try {
                handleTransaction(transactionDto);
            } catch (Exception ex) {
                log.error("Error processing message {}: ", transactionDto, ex);
            }
        }
    }

    private void handleTransaction(FailedTransactionDto transactionDto) {
        ResponseEntity<String> response = restTemplate
                .postForEntity(unlockAccountUrl, transactionDto, String.class);

        if(response.getStatusCode() == OK) {
            service.delete(transactionDto);
        } else if (response.getStatusCode() == FORBIDDEN) {
            service.createRecord(transactionDto);
        }  else {
            log.warn("Received unexpected status code {} for transaction {}",
                    response.getStatusCode(), transactionDto);
        }
    }

    @Scheduled(fixedRate = frequency)
    private void reUnlockAccount() {
        List<FailedTransaction> failedTransactions = service.findAll();

        if(!failedTransactions.isEmpty()) {
            List<FailedTransactionDto> failedTransactionDtos = failedTransactions
                    .stream().map(mapper::toDto).toList();
            processFailedTransactions(failedTransactionDtos);
        } else {
            log.debug("The list of FailedTransaction is empty");
        }
    }
}
