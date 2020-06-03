package com.rivigo.riconet.event.consumer;

import akka.Done;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rivigo.riconet.core.dto.ConsumerMessage;
import com.rivigo.riconet.core.dto.NotificationDTO;
import com.rivigo.riconet.core.enums.TransactionManagerEventNames;
import com.rivigo.riconet.core.utils.MDCUtils;
import com.rivigo.riconet.event.config.EventTopicNameConfig;
import com.rivigo.riconet.event.service.TransactionManagerEventService;
import java.io.IOException;
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

/** This class represents the event consumer for transaction manager related activities. */
@Slf4j
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class TransactionManagerEventConsumer extends EventConsumer {

  /** Bean for transaction manager event service. */
  private final TransactionManagerEventService transactionManagerEventService;

  /** bean for event topic name config. */
  private final EventTopicNameConfig eventTopicNameConfig;

  /** bean for object mapper for DTO conversion. */
  private final ObjectMapper objectMapper;

  /**
   * This function processes the records and returns the completion stage.
   *
   * @param record string, string mapping of consumer record.
   * @return completion stage.
   */
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
      processMessage(record.value());
    } else if (record.topic().equals(getErrorTopic())) {
      MDCUtils.setEventDetails(record);
      ConsumerMessage consumerMessage = null;
      try {
        consumerMessage = objectMapper.readValue(record.value(), ConsumerMessage.class);
        processMessage(consumerMessage.getMessage());
      } catch (IOException e) {
        final String errorMsg = getStackTrace(e);
        processError(consumerMessage, errorMsg);
        log.error("error", e);
      }
    }
    return CompletableFuture.completedFuture(Done.getInstance());
  }

  /**
   * This function returns the list of transaction manager event names.
   *
   * @return
   */
  @Override
  public List<Enum> eventNamesToBeConsumed() {
    return Arrays.asList(TransactionManagerEventNames.values());
  }

  /**
   * This function returns transaction manager event sink name.
   *
   * @return name of transaction manager event sink.
   */
  @Override
  public String getTopic() {
    return eventTopicNameConfig.getTransactionManagerEventSink();
  }

  /**
   * Function used for processing notification.
   *
   * @param notificationDTO dto to be processed.
   */
  @Override
  public void doAction(NotificationDTO notificationDTO) {
    log.info("Action on notification {}", notificationDTO);
    try {
      transactionManagerEventService.processNotification(notificationDTO);
    } catch (Exception e) {
      log.error("Here's what went wrong - {}", e.getMessage(), e);
      throw new RuntimeException(e);
    }
  }

  /**
   * This function returns the name of class as the consumer name.
   *
   * @return name of the class.
   */
  @Override
  public String getConsumerName() {
    return getClass().getName();
  }

  /**
   * This function returns transaction manager event sink error name.
   *
   * @return name of transaction manager event sink error.
   */
  @Override
  public String getErrorTopic() {
    return eventTopicNameConfig.getTransactionManagerEventSinkError();
  }
}
