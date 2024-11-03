package ru.t1.correction.app.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockserver.client.MockServerClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.jdbc.JdbcTestUtils;
import ru.t1.correction.app.model.FailedTransaction;
import ru.t1.correction.app.model.dto.FailedTransactionDto;
import ru.t1.correction.app.repository.TransactionRepository;
import ru.t1.correction.app.util.AbstractIntegrationTestInitializer;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

public class FailedTransactionProcessorTest extends AbstractIntegrationTestInitializer {

    @Autowired
    private FailedTransactionProcessor processor;

    @Autowired
    private TransactionRepository repository;

    private static MockServerClient serverClient;

    @BeforeAll
    public static void setUp() {
        serverClient = new MockServerClient(mockServer.getHost(), mockServer.getServerPort());
    }

    @AfterEach
    public void tearDown() {
        repository.deleteAll();
        serverClient.reset();
    }

    @Test
    public void processFailedTransactions_WhenResponseUnlockAndFailedTransactionNotExists_ThenFailedTransactionNotSave() throws JsonProcessingException {

        FailedTransactionDto transactionDto = new FailedTransactionDto();
        transactionDto.setOriginalTransactionId(1L);
        transactionDto.setAccountId(1L);

        ObjectMapper mapper = new ObjectMapper();
        String transactionDtoString = mapper.writeValueAsString(transactionDto);

        serverClient.when(request().withPath("/unlock").withMethod("POST")
                        .withBody(transactionDtoString))
                        .respond(response().withBody("Account unlocked"));

        processor.processFailedTransactions(List.of(transactionDto));

        List<FailedTransaction> transactions = repository.findAll();

        assertTrue(transactions.isEmpty());

    }

    @Test
    @Sql(scripts = "classpath:db/create-failed-transaction.sql")
    public void processFailedTransactions_WhenResponseUnlockAndFailedTransactionExists_ThenFailedTransactionDelete() throws JsonProcessingException {

        FailedTransactionDto transactionDto = new FailedTransactionDto();
        transactionDto.setId(1L);
        transactionDto.setOriginalTransactionId(1L);
        transactionDto.setAccountId(1L);

        ObjectMapper mapper = new ObjectMapper();
        String transactionDtoString = mapper.writeValueAsString(transactionDto);

        serverClient.when(request().withPath("/unlock").withMethod("POST")
                        .withBody(transactionDtoString))
                        .respond(response().withBody("Account unlocked"));

        processor.processFailedTransactions(List.of(transactionDto));

        List<FailedTransaction> transactions = repository.findAll();

        assertTrue(transactions.isEmpty());
    }

    @Test
    public void processFailedTransactions_WhenResponseInsufficientFundsAndFailedTransactionNotExists_ThenFailedTransactionSave() throws JsonProcessingException {

        FailedTransactionDto transactionDto = new FailedTransactionDto();
        transactionDto.setOriginalTransactionId(1L);
        transactionDto.setAccountId(1L);

        ObjectMapper mapper = new ObjectMapper();
        String transactionDtoString = mapper.writeValueAsString(transactionDto);

        serverClient.when(request().withPath("/unlock").withMethod("POST")
                        .withBody(transactionDtoString))
                        .respond(response().withBody("Insufficient funds to unlock account."));

        processor.processFailedTransactions(List.of(transactionDto));

        List<FailedTransaction> transactions = repository.findAll();

        assertEquals(1, transactions.size());
        assertNotNull(transactions.get(0).getId());
        assertEquals(1, transactions.get(0).getOriginalTransactionId());
        assertEquals(1, transactions.get(0).getAccountId());
    }

    @Test
    @Sql(scripts = "classpath:db/create-failed-transaction.sql")
    public void processFailedTransactions_WhenResponseInsufficientFundsAndFailedTransactionExists_ThenFailedTransactionSave() throws JsonProcessingException {

        FailedTransactionDto transactionDto = new FailedTransactionDto();
        transactionDto.setId(1L);
        transactionDto.setOriginalTransactionId(1L);
        transactionDto.setAccountId(1L);

        ObjectMapper mapper = new ObjectMapper();
        String transactionDtoString = mapper.writeValueAsString(transactionDto);

        serverClient.when(request().withPath("/unlock").withMethod("POST")
                        .withBody(transactionDtoString))
                .respond(response().withBody("Insufficient funds to unlock account."));

        processor.processFailedTransactions(List.of(transactionDto));

        List<FailedTransaction> transactions = repository.findAll();

        assertEquals(1, transactions.size());
        assertNotNull(transactions.get(0).getId());
        assertEquals(1, transactions.get(0).getOriginalTransactionId());
        assertEquals(1, transactions.get(0).getAccountId());

    }
}
