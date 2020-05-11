package com.rivigo.riconet.event.consumer;

import com.rivigo.riconet.core.dto.NotificationDTO;
import com.rivigo.riconet.core.enums.TransactionManagerEventNames;
import com.rivigo.riconet.core.service.TransactionManagerEventService;
import com.rivigo.riconet.event.config.EventTopicNameConfig;
import java.util.Arrays;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class TransactionManagerEventConsumer extends EventConsumer {

  private final TransactionManagerEventService transactionManagerEventService;

  private final EventTopicNameConfig eventTopicNameConfig;

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
