package com.rivigo.riconet.core.utils;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.MDC;

@Slf4j
@UtilityClass
public class MDCUtils {

  private static final String KEY = "mdcData";

  public static void setEventDetails(ConsumerRecord<String, String> record) {
    try {
      String mdcData =
          String.format(
              "topic:%s, partition:%d, offset:%d",
              record.topic(), record.partition(), record.offset());
      MDC.put(KEY, mdcData);
    } catch (Exception e) {
      log.warn("Unable to set MDC data for record: {}", record);
    }
  }
}
