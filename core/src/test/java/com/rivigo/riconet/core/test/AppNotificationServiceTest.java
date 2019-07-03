package com.rivigo.riconet.core.test;

import static com.rivigo.riconet.core.enums.ZoomPropertyName.STG_NOTIF_ALLOWED_APP_USER_IDS;

import com.rivigo.riconet.core.dto.NotificationDTO;
import com.rivigo.riconet.core.enums.EventName;
import com.rivigo.riconet.core.enums.KairosExpressAppEventName;
import com.rivigo.riconet.core.enums.WmsEventName;
import com.rivigo.riconet.core.enums.ZoomCommunicationFieldNames;
import com.rivigo.riconet.core.enums.ZoomCommunicationFieldNames.Wms;
import com.rivigo.riconet.core.service.LocationService;
import com.rivigo.riconet.core.service.PushNotificationService;
import com.rivigo.riconet.core.service.UserMasterService;
import com.rivigo.riconet.core.service.ZoomPropertyService;
import com.rivigo.riconet.core.service.impl.AppNotificationServiceImpl;
import com.rivigo.riconet.core.test.Utils.TestConstants;
import com.rivigo.zoom.common.enums.ApplicationId;
import com.rivigo.zoom.common.model.DeviceAppVersionMapper;
import com.rivigo.zoom.common.model.User;
import com.rivigo.zoom.common.model.neo4j.Location;
import com.rivigo.zoom.common.repository.mysql.DeviceAppVersionMapperRepository;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

/** Created by ashfakh on 15/10/18. */
@Slf4j
public class AppNotificationServiceTest {

  @Mock private PushNotificationService pushNotificationService;

  @Mock private DeviceAppVersionMapperRepository deviceAppVersionMapperRepository;

  @Mock private ZoomPropertyService zoomPropertyService;

  @Mock private LocationService locationService;

  @Mock private UserMasterService userMasterService;

  @InjectMocks private AppNotificationServiceImpl appNotificationService;

  @Before
  public void initMocks() {
    MockitoAnnotations.initMocks(this);
    User user = new User();
    user.setId(1L);
    user.setEmail(RandomStringUtils.randomAlphanumeric(10));
    Mockito.when(userMasterService.getById(Mockito.anyLong())).thenReturn(user);
    Mockito.when(userMasterService.getByEmail(Mockito.anyString())).thenReturn(user);
    Mockito.when(locationService.getLocationById(Mockito.anyLong())).thenReturn(getDummyLocation());
  }

  @Test
  public void sendTaskNotifications() throws IOException {
    NotificationDTO notificationDTO = new NotificationDTO();
    notificationDTO.setEntityId(1L);
    notificationDTO.setTsMs(DateTime.now().getMillis());
    Map<String, String> metadata = new HashMap<>();
    metadata.put(Wms.USER_EMAIL_LIST.name(), "test@rivigo.com");
    metadata.put(Wms.TASK_ID.name(), "10");
    metadata.put(Wms.PARENT_TASK_ID.name(), "100");
    notificationDTO.setMetadata(metadata);
    DeviceAppVersionMapper deviceAppVersionMapper1 = new DeviceAppVersionMapper();
    deviceAppVersionMapper1.setUserId(1L);
    deviceAppVersionMapper1.setFirebaseToken(RandomStringUtils.randomAlphanumeric(10));
    DeviceAppVersionMapper deviceAppVersionMapper2 = new DeviceAppVersionMapper();
    deviceAppVersionMapper2.setUserId(1L);
    deviceAppVersionMapper2.setFirebaseToken(RandomStringUtils.randomAlphanumeric(10));
    List<DeviceAppVersionMapper> deviceAppVersionMappers =
        new ArrayList<>(Arrays.asList(deviceAppVersionMapper1, deviceAppVersionMapper2));
    User user = new User();
    user.setId(1L);
    Mockito.when(userMasterService.getByEmailIn(Collections.singletonList("test@rivigo.com")))
        .thenReturn(Collections.singletonList(user));
    Mockito.when(
            deviceAppVersionMapperRepository.findByUserIdInAndAppId(
                Collections.singletonList(1L), ApplicationId.scan_app))
        .thenReturn(deviceAppVersionMappers);
    Mockito.when(zoomPropertyService.getString(STG_NOTIF_ALLOWED_APP_USER_IDS, "57"))
        .thenReturn("1");
    appNotificationService.sendTaskNotifications(notificationDTO, WmsEventName.TASK_UPSERT);
    Mockito.verify(pushNotificationService, Mockito.atLeastOnce())
        .send(
            Mockito.any(),
            Mockito.anyString(),
            Mockito.anyString(),
            Mockito.any(ApplicationId.class));
  }

  private Location getDummyLocation() {
    Location location = new Location();
    location.setCode("locationCode");
    location.setId(1L);
    return location;
  }

  @Test
  public void sendPickupCancellationNotificationTest() throws IOException {
    Map<String, String> metadata = new HashMap<>();
    metadata.put(
        ZoomCommunicationFieldNames.PICKUP_CREATED_BY_USER_ID.name(),
        TestConstants.USER_ID.toString());
    NotificationDTO notificationDTO =
        NotificationDTO.builder()
            .eventName(EventName.PICKUP_CANCELLATION.name())
            .metadata(metadata)
            .build();
    DeviceAppVersionMapper deviceAppVersionMapper1 = new DeviceAppVersionMapper();
    deviceAppVersionMapper1.setFirebaseToken(RandomStringUtils.randomAlphanumeric(5));
    deviceAppVersionMapper1.setUserId(TestConstants.USER_ID);
    DeviceAppVersionMapper deviceAppVersionMapper2 = new DeviceAppVersionMapper();
    deviceAppVersionMapper2.setUserId(TestConstants.USER_ID);
    List<DeviceAppVersionMapper> deviceAppVersionMappers =
        new ArrayList<>(Arrays.asList(deviceAppVersionMapper1, deviceAppVersionMapper2));
    Mockito.when(zoomPropertyService.getString(STG_NOTIF_ALLOWED_APP_USER_IDS, "57"))
        .thenReturn(TestConstants.USER_ID.toString());
    Mockito.when(
            deviceAppVersionMapperRepository.findByUserIdAndAppId(Mockito.any(), Mockito.any()))
        .thenReturn(deviceAppVersionMappers);
    appNotificationService.sendPickupCancellationNotification(notificationDTO);
    Mockito.verify(pushNotificationService, Mockito.atLeastOnce())
        .send(Mockito.any(), Mockito.anyString(), Mockito.anyString(), Mockito.any());
  }

  @Test
  public void sendCnFirstOuDispatchNotificationTest() throws IOException {
    Map<String, String> metadata = new HashMap<>();
    metadata.put(
        ZoomCommunicationFieldNames.CONSIGNOR_USER_ID.name(), TestConstants.USER_ID.toString());
    metadata.put(
        ZoomCommunicationFieldNames.CONSIGNEE_USER_ID.name(), TestConstants.USER_ID_1.toString());
    metadata.put(ZoomCommunicationFieldNames.CNOTE.name(), RandomStringUtils.randomAlphabetic(10));
    NotificationDTO notificationDTO =
        NotificationDTO.builder()
            .eventName(EventName.CN_TRIP_DISPATCHED.name())
            .metadata(metadata)
            .build();
    DeviceAppVersionMapper deviceAppVersionMapper1 = new DeviceAppVersionMapper();
    deviceAppVersionMapper1.setFirebaseToken(RandomStringUtils.randomAlphanumeric(5));
    deviceAppVersionMapper1.setUserId(TestConstants.USER_ID);
    DeviceAppVersionMapper deviceAppVersionMapper2 = new DeviceAppVersionMapper();
    deviceAppVersionMapper2.setUserId(TestConstants.USER_ID);
    List<DeviceAppVersionMapper> deviceAppVersionMappers =
        new ArrayList<>(Arrays.asList(deviceAppVersionMapper1, deviceAppVersionMapper2));
    Mockito.when(zoomPropertyService.getString(STG_NOTIF_ALLOWED_APP_USER_IDS, "57"))
        .thenReturn(TestConstants.USER_ID.toString());
    Mockito.when(
            deviceAppVersionMapperRepository.findByUserIdAndAppId(Mockito.any(), Mockito.any()))
        .thenReturn(deviceAppVersionMappers);
    appNotificationService.sendCnFirstOuDispatchNotification(notificationDTO);
    Mockito.verify(pushNotificationService, Mockito.atLeastOnce())
        .send(Mockito.any(), Mockito.anyString(), Mockito.anyString(), Mockito.any());
  }

  @Test
  public void sendPickUpAssignmentEventTest() throws IOException {
    Map<String, String> metadata = new HashMap<>();
    metadata.put(
        ZoomCommunicationFieldNames.PICKUP_CREATED_BY_USER_ID.name(),
        TestConstants.USER_ID.toString());
    metadata.put(
        ZoomCommunicationFieldNames.PICKUP_CAPTAIN_NAME.name(),
        RandomStringUtils.randomAlphabetic(15));
    metadata.put(
        ZoomCommunicationFieldNames.PICKUP_CAPTAIN_CONTACT_NUMBER.name(),
        RandomStringUtils.randomNumeric(10));
    metadata.put(
        ZoomCommunicationFieldNames.Pickup.PICKUP_ID.name(), TestConstants.PICKUP_ID.toString());
    NotificationDTO notificationDTO =
        NotificationDTO.builder()
            .eventName(EventName.PICKUP_ASSIGNMENT.name())
            .metadata(metadata)
            .build();
    DeviceAppVersionMapper deviceAppVersionMapper1 = new DeviceAppVersionMapper();
    deviceAppVersionMapper1.setFirebaseToken(RandomStringUtils.randomAlphanumeric(5));
    deviceAppVersionMapper1.setUserId(TestConstants.USER_ID);
    DeviceAppVersionMapper deviceAppVersionMapper2 = new DeviceAppVersionMapper();
    deviceAppVersionMapper2.setUserId(TestConstants.USER_ID);
    List<DeviceAppVersionMapper> deviceAppVersionMappers =
        new ArrayList<>(Arrays.asList(deviceAppVersionMapper1, deviceAppVersionMapper2));
    Mockito.when(zoomPropertyService.getString(STG_NOTIF_ALLOWED_APP_USER_IDS, "57"))
        .thenReturn(TestConstants.USER_ID.toString());
    Mockito.when(
            deviceAppVersionMapperRepository.findByUserIdAndAppId(Mockito.any(), Mockito.any()))
        .thenReturn(deviceAppVersionMappers);
    appNotificationService.sendPickUpAssignmentEvent(notificationDTO);
    Mockito.verify(pushNotificationService, Mockito.atLeastOnce())
        .send(Mockito.any(), Mockito.anyString(), Mockito.anyString(), Mockito.any());
  }

  @Test
  public void sendPickUpReachedAtClientAddressTest() throws IOException {
    Map<String, String> metadata = new HashMap<>();
    metadata.put(
        ZoomCommunicationFieldNames.PICKUP_CREATED_BY_USER_ID.name(),
        TestConstants.USER_ID.toString());
    metadata.put(
        ZoomCommunicationFieldNames.PICKUP_CAPTAIN_NAME.name(),
        RandomStringUtils.randomAlphabetic(15));
    metadata.put(
        ZoomCommunicationFieldNames.PICKUP_CAPTAIN_CONTACT_NUMBER.name(),
        RandomStringUtils.randomNumeric(10));
    metadata.put(
        ZoomCommunicationFieldNames.Pickup.PICKUP_ID.name(), TestConstants.PICKUP_ID.toString());
    NotificationDTO notificationDTO =
        NotificationDTO.builder()
            .eventName(EventName.PICKUP_REACHED_AT_CLIENT_WAREHOUSE.name())
            .metadata(metadata)
            .build();
    DeviceAppVersionMapper deviceAppVersionMapper1 = new DeviceAppVersionMapper();
    deviceAppVersionMapper1.setFirebaseToken(RandomStringUtils.randomAlphanumeric(5));
    deviceAppVersionMapper1.setUserId(TestConstants.USER_ID);
    DeviceAppVersionMapper deviceAppVersionMapper2 = new DeviceAppVersionMapper();
    deviceAppVersionMapper2.setUserId(TestConstants.USER_ID);
    List<DeviceAppVersionMapper> deviceAppVersionMappers =
        new ArrayList<>(Arrays.asList(deviceAppVersionMapper1, deviceAppVersionMapper2));
    Mockito.when(zoomPropertyService.getString(STG_NOTIF_ALLOWED_APP_USER_IDS, "57"))
        .thenReturn(TestConstants.USER_ID.toString());
    Mockito.when(
            deviceAppVersionMapperRepository.findByUserIdInAndAppId(Mockito.any(), Mockito.any()))
        .thenReturn(deviceAppVersionMappers);
    Mockito.when(
            deviceAppVersionMapperRepository.findByUserIdAndAppId(Mockito.any(), Mockito.any()))
        .thenReturn(deviceAppVersionMappers);
    appNotificationService.sendPickUpReachedAtClientAddress(notificationDTO);
    Mockito.verify(pushNotificationService, Mockito.atLeastOnce())
        .send(Mockito.any(), Mockito.anyString(), Mockito.anyString(), Mockito.any());
  }

  @Test
  public void sendCnDrsDispatchEventTest() throws IOException {
    Map<String, String> metadata = new HashMap<>();
    metadata.put(
        ZoomCommunicationFieldNames.CONSIGNOR_USER_ID.name(), TestConstants.USER_ID.toString());
    metadata.put(
        ZoomCommunicationFieldNames.CONSIGNEE_USER_ID.name(), TestConstants.USER_ID_1.toString());
    metadata.put(ZoomCommunicationFieldNames.CNOTE.name(), RandomStringUtils.randomAlphabetic(10));
    NotificationDTO notificationDTO =
        NotificationDTO.builder()
            .eventName(EventName.CN_DRS_DISPATCH.name())
            .metadata(metadata)
            .build();
    DeviceAppVersionMapper deviceAppVersionMapper1 = new DeviceAppVersionMapper();
    deviceAppVersionMapper1.setFirebaseToken(RandomStringUtils.randomAlphanumeric(5));
    deviceAppVersionMapper1.setUserId(TestConstants.USER_ID);
    DeviceAppVersionMapper deviceAppVersionMapper2 = new DeviceAppVersionMapper();
    deviceAppVersionMapper2.setUserId(TestConstants.USER_ID);
    List<DeviceAppVersionMapper> deviceAppVersionMappers =
        new ArrayList<>(Arrays.asList(deviceAppVersionMapper1, deviceAppVersionMapper2));
    Mockito.when(zoomPropertyService.getString(STG_NOTIF_ALLOWED_APP_USER_IDS, "57"))
        .thenReturn(TestConstants.USER_ID.toString());
    Mockito.when(
            deviceAppVersionMapperRepository.findByUserIdAndAppId(Mockito.any(), Mockito.any()))
        .thenReturn(deviceAppVersionMappers);
    appNotificationService.sendCnDrsDispatchEvent(notificationDTO);
    Mockito.verify(pushNotificationService, Mockito.atLeastOnce())
        .send(Mockito.any(), Mockito.anyString(), Mockito.anyString(), Mockito.any());
  }

  @Test
  public void sendCnDeliveredNotificationTest() throws IOException {
    Map<String, String> metadata = new HashMap<>();
    metadata.put(
        ZoomCommunicationFieldNames.CONSIGNOR_USER_ID.name(), TestConstants.USER_ID.toString());
    metadata.put(
        ZoomCommunicationFieldNames.CONSIGNEE_USER_ID.name(), TestConstants.USER_ID_1.toString());
    metadata.put(ZoomCommunicationFieldNames.CNOTE.name(), RandomStringUtils.randomAlphabetic(10));
    NotificationDTO notificationDTO =
        NotificationDTO.builder()
            .eventName(EventName.CN_DELIVERY.name())
            .metadata(metadata)
            .build();
    DeviceAppVersionMapper deviceAppVersionMapper1 = new DeviceAppVersionMapper();
    deviceAppVersionMapper1.setFirebaseToken(RandomStringUtils.randomAlphanumeric(5));
    deviceAppVersionMapper1.setUserId(TestConstants.USER_ID);
    DeviceAppVersionMapper deviceAppVersionMapper2 = new DeviceAppVersionMapper();
    deviceAppVersionMapper2.setUserId(TestConstants.USER_ID);
    List<DeviceAppVersionMapper> deviceAppVersionMappers =
        new ArrayList<>(Arrays.asList(deviceAppVersionMapper1, deviceAppVersionMapper2));
    Mockito.when(zoomPropertyService.getString(STG_NOTIF_ALLOWED_APP_USER_IDS, "57"))
        .thenReturn(TestConstants.USER_ID.toString());
    Mockito.when(
            deviceAppVersionMapperRepository.findByUserIdAndAppId(Mockito.any(), Mockito.any()))
        .thenReturn(deviceAppVersionMappers);
    appNotificationService.sendCnDeliveredNotification(notificationDTO);
    Mockito.verify(pushNotificationService, Mockito.atLeastOnce())
        .send(Mockito.any(), Mockito.anyString(), Mockito.anyString(), Mockito.any());
  }

  @Test
  public void sendCnDelayedNotificationTest() throws IOException {
    Map<String, String> metadata = new HashMap<>();
    metadata.put(
        ZoomCommunicationFieldNames.CONSIGNOR_USER_ID.name(), TestConstants.USER_ID.toString());
    metadata.put(
        ZoomCommunicationFieldNames.CONSIGNEE_USER_ID.name(), TestConstants.USER_ID_1.toString());
    metadata.put(ZoomCommunicationFieldNames.CNOTE.name(), RandomStringUtils.randomAlphabetic(10));
    NotificationDTO notificationDTO =
        NotificationDTO.builder()
            .eventName(KairosExpressAppEventName.CN_DELAYED.name())
            .metadata(metadata)
            .build();
    DeviceAppVersionMapper deviceAppVersionMapper1 = new DeviceAppVersionMapper();
    deviceAppVersionMapper1.setFirebaseToken(RandomStringUtils.randomAlphanumeric(5));
    deviceAppVersionMapper1.setUserId(TestConstants.USER_ID);
    DeviceAppVersionMapper deviceAppVersionMapper2 = new DeviceAppVersionMapper();
    deviceAppVersionMapper2.setUserId(TestConstants.USER_ID_1);
    List<DeviceAppVersionMapper> deviceAppVersionMappers =
        new ArrayList<>(Arrays.asList(deviceAppVersionMapper1, deviceAppVersionMapper2));
    Mockito.when(zoomPropertyService.getString(STG_NOTIF_ALLOWED_APP_USER_IDS, "57"))
        .thenReturn(TestConstants.USER_ID.toString());
    Mockito.when(
            deviceAppVersionMapperRepository.findByUserIdAndAppId(Mockito.any(), Mockito.any()))
        .thenReturn(deviceAppVersionMappers);
    appNotificationService.sendCnDelayedNotification(notificationDTO);
    Mockito.verify(pushNotificationService, Mockito.atLeastOnce())
        .send(Mockito.any(), Mockito.anyString(), Mockito.anyString(), Mockito.any());
  }
}
