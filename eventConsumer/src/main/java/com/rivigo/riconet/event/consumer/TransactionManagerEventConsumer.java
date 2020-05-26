package com.rivigo.riconet.event.consumer;

import akka.Done;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rivigo.riconet.core.dto.ConsumerMessage;
import com.rivigo.riconet.core.dto.NotificationDTO;
import com.rivigo.riconet.core.enums.TransactionManagerEventNames;
import com.rivigo.riconet.core.service.TransactionManagerEventService;
import com.rivigo.riconet.core.utils.MDCUtils;
import com.rivigo.riconet.event.config.EventTopicNameConfig;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class TransactionManagerEventConsumer extends EventConsumer {

  private final TransactionManagerEventService transactionManagerEventService;

  private final EventTopicNameConfig eventTopicNameConfig;

  private final ObjectMapper objectMapper;

  @Async
  @Override
  public CompletionStage<Done> save(ConsumerRecord<String, String> record) {
    if (record.topic().equals(getTopic())) {
      MDCUtils.setEventDetails(record);
      log.info(
          "Processing message {} on topic {} partition {} ",
          record.value(),
          record.topic(),
          record.partition());
      MDCUtils.setEventDetails(record);
      try {
        processMessage(record.value());
      } catch (Exception e) {
        String errorMsg = getStackTrace(e);
        processFirstTimeError(record.value(), errorMsg);
      }
    } else if (record.topic().equals(getErrorTopic())) {
      MDCUtils.setEventDetails(record);
      ConsumerMessage consumerMessage = null;
      try {
        consumerMessage = objectMapper.readValue(record.value(), ConsumerMessage.class);
        processMessage(consumerMessage.getMessage());
      } catch (Exception e) {
        String errorMsg = getStackTrace(e);
        processError(consumerMessage, errorMsg);
        log.error("error", e);
      }
    }
    return CompletableFuture.completedFuture(Done.getInstance());
  }

  @Override
  public List<Enum> eventNamesToBeConsumed() {
    return Arrays.asList(TransactionManagerEventNames.values());
  }

  @Override
  public String getTopic() {
    return eventTopicNameConfig.getTransactionManagerEventSink();
  }

  @Override
  public void doAction(NotificationDTO notificationDTO) {
    transactionManagerEventService.processNotification(notificationDTO);
  }

  @Override
  public String getConsumerName() {
    return getClass().getName();
  }

  @Override
  public String getErrorTopic() {
    return eventTopicNameConfig.getTransactionManagerEventSinkError();
  }
}
