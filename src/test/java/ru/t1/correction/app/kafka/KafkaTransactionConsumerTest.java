package ru.t1.correction.app.kafka;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.support.Acknowledgment;
import ru.t1.correction.app.model.dto.FailedTransactionDto;
import ru.t1.correction.app.service.FailedTransactionProcessor;

import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class KafkaTransactionConsumerTest {

    @InjectMocks
    private KafkaTransactionConsumer consumer;

    @Mock
    private FailedTransactionProcessor processor;

    @Mock
    private Acknowledgment acknowledgment;

    @Test
    void listener_WithValidMessageList_ShouldAcknowledge() {

        FailedTransactionDto transactionDto = new FailedTransactionDto();
        transactionDto.setOriginalTransactionId(1L);
        transactionDto.setAccountId(1L);

        FailedTransactionDto transactionDto2 = new FailedTransactionDto();
        transactionDto2.setOriginalTransactionId(2L);
        transactionDto2.setAccountId(2L);

        List<FailedTransactionDto> messageList = List.of(transactionDto, transactionDto2);

        consumer.listener(messageList, acknowledgment);

        verify(processor, times(1)).processFailedTransactions(messageList);
        verify(acknowledgment, times(1)).acknowledge();
    }

    @Test
    void listener_WhenServiceThrowsException_ShouldNotAcknowledge() {

        FailedTransactionDto transactionDto = new FailedTransactionDto();
        FailedTransactionDto transactionDto2 = new FailedTransactionDto();

        List<FailedTransactionDto> messageList = List.of(transactionDto, transactionDto2);

        doThrow(new RuntimeException("Processing Error"))
                .when(processor).processFailedTransactions(anyList());

        consumer.listener(messageList, acknowledgment);

        verify(processor, times(1)).processFailedTransactions(messageList);
        verify(acknowledgment, never()).acknowledge();
    }

    @Test
    void listener_WithEmptyMessageList_ShouldAcknowledge() {

        List<FailedTransactionDto> messageList = Collections.emptyList();

        consumer.listener(messageList, acknowledgment);

        verify(processor, times(1)).processFailedTransactions(messageList);
        verify(acknowledgment, times(1)).acknowledge();
    }

    @Test
    void listener_WithNullMessageList_ShouldAcknowledge() {

        consumer.listener(null, acknowledgment);

        verify(processor, times(1)).processFailedTransactions(any());
        verify(acknowledgment, times(1)).acknowledge();
    }
}
