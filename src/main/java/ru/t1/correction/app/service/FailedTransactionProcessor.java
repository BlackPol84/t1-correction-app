package ru.t1.correction.app.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.t1.correction.app.model.dto.FailedTransactionDto;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class FailedTransactionProcessor {

    private final TransactionService service;
    private final RestTemplate restTemplate;

    @Value("${spring.url.account.unlock}")
    private String unlockAccountUrl;

    public void processFailedTransactions(List<FailedTransactionDto> messageList) {

        for(FailedTransactionDto transactionDto : messageList) {
            try {
                ResponseEntity<String> response = restTemplate
                        .postForEntity(unlockAccountUrl, transactionDto, String.class);

                if(response.getStatusCode() == HttpStatus.OK) {
                    service.deleteTransaction(transactionDto);
                } else if (response.getStatusCode() == HttpStatus.FORBIDDEN) {
                    service.createRecord(transactionDto);
                }
            } catch (Exception ex) {
                log.error("Error processing message {}: ", transactionDto, ex);
            }
        }
    }
}
