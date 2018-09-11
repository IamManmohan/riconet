package com.rivigo.riconet.core.test;

import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rivigo.riconet.core.service.ZoomBackendAPIClientService;
import com.rivigo.riconet.core.service.impl.ConsignmentInvoiceServiceImpl;
import com.rivigo.riconet.core.service.impl.ConsignmentServiceImpl;
import com.rivigo.riconet.core.service.impl.UrlShortnerServiceImpl;
import com.rivigo.zoom.common.model.Consignment;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

/** Created by ashfakh on 06/09/18. */
@Slf4j
public class ConsignmentInvoiceServiceTest {

  @InjectMocks private ConsignmentInvoiceServiceImpl consignmentInvoiceService;

  @Mock private ZoomBackendAPIClientService zoomBackendAPIClientService;

  @Mock private ConsignmentServiceImpl consignmentService;

  @Mock private UrlShortnerServiceImpl urlShortnerService;

  @Spy private ObjectMapper objectMapper;

  @Before
  public void initMocks() {
    MockitoAnnotations.initMocks(this);
  }

  String invoiceEventPayload =
      "{\"encodedUrl\":\"https://public-resource-access-api.rivigo.com/download/v2?url=64633032366361302d616161642d313165372d626632612d30366538343361653631313755726c44746f2875726c3d68747470733a2f2f72697669676f2d6163636f756e74696e672e73332d61702d736f757468656173742d312e616d617a6f6e6177732e636f6d2f636c69656e742d646f63756d656e74732f696e766f696365732f525249524a31383139303030303530322e7064662c2074696d657374616d703d3135333630373038363730393729&downloadfilename=8000468461.pdf\",\"cnote\":\"8000468461\"}";

  @Test
  public void consumeLoadingEventNoTicketTest1() {
    Consignment consignment = new Consignment();
    consignment.setCnote("8000468461");
    consignment.setOrganizationId(1L);
    when(consignmentService.getConsignmentByCnote("8000468461")).thenReturn(consignment);
    consignmentInvoiceService.saveInvoiceDetails(invoiceEventPayload);
  }
}
