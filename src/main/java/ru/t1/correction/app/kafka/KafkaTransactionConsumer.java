package ru.t1.correction.app.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import ru.t1.correction.app.model.dto.FailedTransactionDto;
import ru.t1.correction.app.service.FailedTransactionProcessor;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public class KafkaTransactionConsumer {

    private final FailedTransactionProcessor processor;

    @KafkaListener(id = "${spring.kafka.consumer.group-id}",
            topics = "${spring.kafka.topic.transaction-errors}",
            containerFactory = "kafkaListenerContainerFactory")
    public void listener(@Payload List<FailedTransactionDto> messageList,
                         Acknowledgment ack) {
        log.debug("Transaction consumer: Processing new messages");

        try {
            processor.processFailedTransactions(messageList);
            ack.acknowledge();

        } catch (Exception ex) {
            log.error("Message processing error: ", ex);
        }
        log.debug("Transaction consumer: the records have been processed");
    }
}
