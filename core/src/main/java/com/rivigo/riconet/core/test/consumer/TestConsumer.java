package com.rivigo.riconet.core.test.consumer;

import com.rivigo.riconet.core.consumerabstract.ConsumerModel;
import com.rivigo.riconet.core.service.DEPSRecordService;
import com.rivigo.zoom.common.enums.Topic;
import com.rivigo.zoom.exceptions.ZoomException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class TestConsumer extends ConsumerModel {

  @Autowired private DEPSRecordService depsRecordService;

  @Override
  public String getTopic() {
    return Topic.COM_RIVIGO_ZOOM_SHORTAGE_NOTIFICATION.name();
  }

  @Override
  public String getErrorTopic() {
    return Topic.COM_RIVIGO_ZOOM_SHORTAGE_NOTIFICATION_ERROR.name();
  }

  public void processMessage(String str) {
    if (str.equals("1")) {
      return;
    }
    throw new ZoomException("test");
  }
}
