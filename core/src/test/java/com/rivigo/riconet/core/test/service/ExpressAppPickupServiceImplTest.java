package com.rivigo.riconet.core.test.service;

import com.rivigo.riconet.core.enums.KairosMessageFieldNames;
import com.rivigo.riconet.core.service.ZoomBackendAPIClientService;
import com.rivigo.riconet.core.service.impl.ExpressAppPickupServiceImpl;
import java.util.HashMap;
import java.util.Map;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

public class ExpressAppPickupServiceImplTest {

  @InjectMocks ExpressAppPickupServiceImpl expressAppPickupService;

  @Mock ZoomBackendAPIClientService zoomBackendAPIClientService;

  @Before
  public void setUp() {
    MockitoAnnotations.initMocks(this);
  }

  @Test
  public void processExpressPickupAutoCancellationEventTest() {

    Long pickupId = 1L;
    Map<String, String> inputMap = new HashMap<>();
    inputMap.put(KairosMessageFieldNames.METADATA.toString(), pickupId.toString());
    Mockito.when(zoomBackendAPIClientService.deletePickup(pickupId)).thenReturn(Boolean.TRUE);
  }

  @Test
  public void processExpressPickupAutoCancellationEventTest2() {

    Long pickupId = 1L;
    Map<String, String> inputMap = new HashMap<>();
    Mockito.when(zoomBackendAPIClientService.deletePickup(pickupId)).thenReturn(Boolean.TRUE);
  }
}
