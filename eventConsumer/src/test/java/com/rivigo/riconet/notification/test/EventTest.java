package com.rivigo.riconet.notification.test;

import static org.junit.Assert.assertEquals;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rivigo.riconet.core.service.EventTriggerService;
import com.rivigo.riconet.event.consumer.ZoomEventTriggerConsumer;
import com.rivigo.riconet.event.main.EventMain;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/** Created by ashfakh on 29/9/17. */
@Slf4j
public class EventTest {

  @InjectMocks private ZoomEventTriggerConsumer zoomEventTriggerConsumer;

  @Mock private EventTriggerService eventTriggerService;

  @Mock
  private ObjectMapper objectMapper;

  @Before
  public void initMocks() {
    MockitoAnnotations.initMocks(this);
  }

  @Test
  public void processEventTrigger() throws IOException {
    String str =
        zoomEventTriggerConsumer.processMessage(
            "{\"eventName\"\n"
                + ":\"COLLECTION_CHEQUE_BOUNCE\",\"entityId\":1519260,\"entityName\":\"CN\","
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
    assertEquals(
        str,
        "{\"eventName\"\n"
            + ":\"COLLECTION_CHEQUE_BOUNCE\",\"entityId\":1519260,\"entityName\":\"CN\","
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
  }

  public void processMain() {
    EventMain.main(null);
  }
}
