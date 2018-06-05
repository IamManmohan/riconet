package com.rivigo.riconet.notification.test;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rivigo.riconet.core.config.TopicNameConfig;
import com.rivigo.riconet.core.enums.EventName;
import com.rivigo.riconet.core.service.PickupService;
import com.rivigo.riconet.event.consumer.BfPickupChargesActionConsumer;
import java.util.Arrays;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

@Slf4j
public class BfPickupChargesActionConsumerTest {

  @InjectMocks private BfPickupChargesActionConsumer bfPickupChargesActionConsumer;

  @Spy private ObjectMapper objectMapper;

  @Mock private TopicNameConfig topicNameConfig;

  @Mock private PickupService pickupService;

  @Before
  public void initMocks() {
    MockitoAnnotations.initMocks(this);
  }

  @Test
  public void getTopicTest() {
    String topic = "tempTopic";
    when(topicNameConfig.enrichedEventSinkTopic()).thenReturn(topic);
    String result = bfPickupChargesActionConsumer.getTopic();
    Assert.assertEquals(topic, result);
  }

  @Test
  public void getErrorTopicTest() {
    String topic = "tempTopic";
    when(topicNameConfig.enrichedEventSinkErrorTopic()).thenReturn(topic);
    String result = bfPickupChargesActionConsumer.getErrorTopic();
    Assert.assertEquals(topic, result);
  }

  @Test
  public void eventsToBeConsumed() {
    List<EventName> expected =
        Arrays.asList(
            EventName.CN_COMPLETION_ALL_INSTANCES,
            EventName.CN_DELETED,
            EventName.PICKUP_COMPLETION);
    List<EventName> eventNameList = bfPickupChargesActionConsumer.eventNamesToBeConsumed();
    Assert.assertEquals(expected, eventNameList);
  }

  @Test
  public void processMessageTest() {
    bfPickupChargesActionConsumer.processMessage(
        "{\"eventName\"\n"
            + ":\"CN_COMPLETION_ALL_INSTANCES\",\"entityId\":1519260,\"entityName\":\"CN\","
            + "\"eventGUID\":\"CN_1519260\",\"tsMs\":1525910400000,"
            + "\"eventUID\":\"CN_CREATION_CN_15192\n"
            + "60_1525910400000\",\"metadata\":{\"AMOUNT_ORIGINAL\":\"1646\","
            + "\"ORIGIN_FIELD_USER_NAME\":\"SURYA LIGHTING AV & UV  TECHNOLOGIES\","
            + "\"INSTRUMENT_DATE_STRING\"\n"
            + ":\"10/05/2018\",\"INSTRUMENT_NUMBER\":\"068678\",\"PRODUCT_CODE\":\"CHEQUE\","
            + "\"CLEARANCE_DATE\":\"1525910400000\",\"CONSIGNER_ADDRESS\":\"1978, 1st FLOOR , BHAG\n"
            + "IRATH PALACE, CHANDNI CHOWK\",\"CLIENT_ID\":\"51\",\"AMOUNT\":\"1646.00\","
            + "\"ORIGIN_FIELD_USER_PHONE\":\"9868580501\",\"LOCATION_ID\":\"15\","
            + "\"DEPOSIT_DATE_STRING\n"
            + "\":\"10/05/2018\",\"DESTINATION_FIELD_USER_PHONE\":\"7889795613\","
            + "\"CREATED_BY\":\"manuj.vishnoi@partners.rivigo.com\",\"CNOTE\":\"8998768796\","
            + "\"CONSIGNMENT_I\n"
            + "D\":\"1519260\",\"DRAWEE_BANK\":\"ICICI BANK\",\"DESTINATION_FIELD_USER_EMAIL\":null,"
            + "\"INSTRUMENT_DATE\":\"1525910400000\",\"CONSIGNEE_ADDRESS\":\"OKHLA BATIA \n"
            + "HOUSE DELHI\",\"DEPOSIT_DATE\":\"1525910400000\",\"ORIGIN_FIELD_USER_EMAIL\":null,"
            + "\"DESTINATION_FIELD_USER_NAME\":\"SOHAL BHAI\",\"CLEARANCE_DATE_STRING\":\"\n"
            + "10/05/2018\",\"CNOTE_TYPE\":\"RETAIL\",\"PAYMENT_MODE\":\"PREPAID\","
            + "\"TOTAL_AMOUNT\":\"1646\",\"PAYMENT_TYPE\":\"Cheque\","
            + "\"CLIENT_CONSIGNMENT_TYPE\":\"RETAIL\"},\"s\n"
            + "ubscribers\":null,\"conditions\":[\"COLLECTION_CHEQUE_BOUNCE_PAID_CN\","
            + "\"COLLECTION_CHEQUE_BOUNCE_DEFAULT_PAID_CN\"]}");
    verify(pickupService, times(0)).deductPickupCharges(any());
  }

  @Test
  public void processMessageHappyTest() {
    bfPickupChargesActionConsumer.processMessage(
        "{\"eventName\"" + ":\"CN_COMPLETION_ALL_INSTANCES\"}");
    verify(pickupService, times(1)).deductPickupCharges(any());
  }
}
