package com.rivigo.riconet.core.test;

import com.rivigo.riconet.core.dto.NotificationDTO;
import com.rivigo.riconet.core.dto.zoomticketing.GroupDTO;
import com.rivigo.riconet.core.dto.zoomticketing.TicketDTO;
import com.rivigo.riconet.core.enums.EventName;
import com.rivigo.riconet.core.enums.zoomticketing.LocationType;
import com.rivigo.riconet.core.service.impl.ChequeBounceServiceImpl;
import com.rivigo.riconet.core.service.impl.UserMasterServiceImpl;
import com.rivigo.riconet.core.service.impl.ZoomTicketingAPIClientServiceImpl;
import com.rivigo.zoom.common.model.User;
import com.rivigo.zoom.exceptions.ZoomException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

/** Created by ashfakh on 11/5/18. */
@Slf4j
public class ChequeBounceServiceTest {

  @InjectMocks private ChequeBounceServiceImpl chequeBounceService;

  @Mock private ZoomTicketingAPIClientServiceImpl zoomTicketingAPIClientService;

  @Mock private UserMasterServiceImpl userMasterService;

  @Captor private ArgumentCaptor<Object> valueCapture;

  @Rule public ExpectedException expectedException = ExpectedException.none();

  @Before
  public void initMocks() {
    MockitoAnnotations.initMocks(this);
  }

  @Test
  public void createTicketForChequeBouceTest() {
    GroupDTO groupDTO = new GroupDTO();
    groupDTO.setId(1L);
    groupDTO.setLocationId(15L);
    groupDTO.setGroupTypeId(1L);
    User user = new User();
    user.setId(1L);
    user.setEmail("dummyuser@rivigo.com");
    user.setMobileNo("9876543210");
    user.setName("Dummy User");
    TicketDTO ticketDTO = new TicketDTO();
    ticketDTO.setId(1L);
    Mockito.when(zoomTicketingAPIClientService.getGroupId(15L, "RETAIL", LocationType.OU))
        .thenReturn(groupDTO);
    Mockito.when(userMasterService.getByEmail("dummyuser@rivigo.com")).thenReturn(user);
    Mockito.when(zoomTicketingAPIClientService.createTicket((TicketDTO) valueCapture.capture()))
        .thenReturn(ticketDTO);
    TicketDTO resultDTO = chequeBounceService.consumeChequeBounceEvent(getNotificationDTO());
    Assert.assertEquals((Object) resultDTO.getId(), 1L);
    ticketDTO.setId(2L);
    NotificationDTO notificationDTO = getNotificationDTO();
    notificationDTO.getMetadata().put("PAYMENT_MODE", "TO_PAY");
    resultDTO = chequeBounceService.consumeChequeBounceEvent(notificationDTO);
    Assert.assertEquals((Object) resultDTO.getId(), 2L);
  }

  @Test
  public void createTicketGroupExceptionTest() {
    User user = new User();
    user.setId(1L);
    user.setEmail("dummyuser@rivigo.com");
    user.setMobileNo("9876543210");
    user.setName("Dummy User");
    expectedException.expect(ZoomException.class);
    expectedException.expectMessage("Group cannot be null, creating ticket failed");
    Mockito.when(userMasterService.getByEmail("dummyuser@rivigo.com")).thenReturn(user);
    chequeBounceService.consumeChequeBounceEvent(getNotificationDTO());
  }

  @Test
  public void createTicketNullUserTest() {
    GroupDTO groupDTO = new GroupDTO();
    groupDTO.setId(1L);
    groupDTO.setLocationId(15L);
    groupDTO.setGroupTypeId(1L);
    Mockito.when(zoomTicketingAPIClientService.getGroupId(15L, "RETAIL", LocationType.OU))
        .thenReturn(groupDTO);
    TicketDTO ticketDTO = chequeBounceService.consumeChequeBounceEvent(getNotificationDTO());
    Assert.assertEquals(null, ticketDTO);
  }

  private NotificationDTO getNotificationDTO() {
    NotificationDTO notificationDTO = new NotificationDTO();
    notificationDTO.setEventName(EventName.COLLECTION_CHEQUE_BOUNCE.name());
    notificationDTO.setEntityId(1518945L);
    notificationDTO.setEntityName("CN");
    notificationDTO.setEventGUID("CN_1518945");
    notificationDTO.setTsMs(1513555200000L);
    notificationDTO.setEventUID("CN_CREATION_CN_1518945_1513555200000");
    Map<String, String> metadata = new HashMap<>();
    metadata.put("DESTINATION_FIELD_USER_PHONE", "9910289797");
    metadata.put("CNOTE", "8968689689");
    metadata.put("CONSIGNMENT_ID", "1518945");
    metadata.put("DRAWEE_BANK", "ICICI BANK");
    metadata.put("ORIGIN_FIELD_USER_NAME", "Plot No. 711");
    metadata.put("INSTRUMENT_DATE", "1513555200000");
    metadata.put("INSTRUMENT_NUMBER", "003443");
    metadata.put("PRODUCT_CODE", "CHEQUE");
    metadata.put("CLEARANCE_DATE", "1513555200000");
    metadata.put("DEPOSIT_DATE", "1513555200000");
    metadata.put("DESTINATION_FIELD_USER_NAME", "JITENDRA SINGH");
    metadata.put("CLIENT_ID", "1697");
    metadata.put("AMOUNT", "1060.0");
    metadata.put("ORIGIN_FIELD_USER_PHONE", "9818276992");
    metadata.put("CNOTE_TYPE", "RETAIL");
    metadata.put("PAYMENT_MODE", "PAID");
    metadata.put("TOTAL_AMOUNT", "1060");
    metadata.put("PAYMENT_TYPE", "Cheque");
    metadata.put("CREATED_BY", "dummyuser@rivigo.com");
    metadata.put("LOCATION_ID", "15");
    notificationDTO.setMetadata(metadata);
    notificationDTO.setConditions(Arrays.asList("COLLECTION_CHEQUE_BOUNCE_PAID_CN"));
    return notificationDTO;
  }
}
