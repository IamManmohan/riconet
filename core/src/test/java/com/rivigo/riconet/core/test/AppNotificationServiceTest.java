package com.rivigo.riconet.core.test;

import static com.rivigo.riconet.core.enums.ZoomPropertyName.DEFAULT_APP_USER_IDS;

import com.rivigo.riconet.core.dto.NotificationDTO;
import com.rivigo.riconet.core.dto.TaskDto;
import com.rivigo.riconet.core.enums.EventName;
import com.rivigo.riconet.core.enums.ZoomCommunicationFieldNames;
import com.rivigo.riconet.core.service.ConsignmentScheduleService;
import com.rivigo.riconet.core.service.LocationService;
import com.rivigo.riconet.core.service.PushNotificationService;
import com.rivigo.riconet.core.service.RestClientUtilityService;
import com.rivigo.riconet.core.service.UserMasterService;
import com.rivigo.riconet.core.service.ZoomPropertyService;
import com.rivigo.riconet.core.service.impl.AppNotificationServiceImpl;
import com.rivigo.riconet.core.test.Utils.TestConstants;
import com.rivigo.zoom.common.enums.ApplicationId;
import com.rivigo.zoom.common.enums.LocationTypeV2;
import com.rivigo.zoom.common.enums.TaskType;
import com.rivigo.zoom.common.enums.ZoomTripType;
import com.rivigo.zoom.common.model.ConsignmentScheduleCache;
import com.rivigo.zoom.common.model.DeviceAppVersionMapper;
import com.rivigo.zoom.common.model.User;
import com.rivigo.zoom.common.model.neo4j.Location;
import com.rivigo.zoom.common.repository.mysql.DeviceAppVersionMapperRepository;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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

  @Mock private RestClientUtilityService restClientUtilityService;

  @Mock private LocationService locationService;

  @Mock private ConsignmentScheduleService consignmentScheduleService;

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
  public void sendUnloadingInLoadingNotification() {
    NotificationDTO notificationDTO = new NotificationDTO();
    notificationDTO.setEntityId(1L);
    notificationDTO.setTsMs(DateTime.now().getMillis());
    Map<String, String> metadata = new HashMap<>();
    metadata.put(ZoomCommunicationFieldNames.USER_ID.name(), "1");
    metadata.put(ZoomCommunicationFieldNames.PARENT_TASK_ID.name(), "100");
    notificationDTO.setMetadata(metadata);
    DeviceAppVersionMapper deviceAppVersionMapper1 = new DeviceAppVersionMapper();
    DeviceAppVersionMapper deviceAppVersionMapper2 = new DeviceAppVersionMapper();
    List<DeviceAppVersionMapper> deviceAppVersionMappers =
        new ArrayList<>(Arrays.asList(deviceAppVersionMapper1, deviceAppVersionMapper2));
    Mockito.when(deviceAppVersionMapperRepository.findByUserId(1L))
        .thenReturn(deviceAppVersionMappers);
    Mockito.when(zoomPropertyService.getString(DEFAULT_APP_USER_IDS, "57")).thenReturn("1");
    appNotificationService.sendTaskUpsertNotification(notificationDTO);
  }

  @Test
  public void sendLoadingUnloadingNotification() {
    NotificationDTO notificationDTO = new NotificationDTO();
    notificationDTO.setEntityId(1L);
    notificationDTO.setTsMs(DateTime.now().getMillis());
    Map<String, String> metadata = new HashMap<>();
    metadata.put(ZoomCommunicationFieldNames.USER_ID.name(), "1");
    metadata.put(ZoomCommunicationFieldNames.TASK_TYPE.name(), "LOADING");
    notificationDTO.setMetadata(metadata);
    DeviceAppVersionMapper deviceAppVersionMapper1 = new DeviceAppVersionMapper();
    DeviceAppVersionMapper deviceAppVersionMapper2 = new DeviceAppVersionMapper();
    List<DeviceAppVersionMapper> deviceAppVersionMappers =
        new ArrayList<>(Arrays.asList(deviceAppVersionMapper1, deviceAppVersionMapper2));
    Mockito.when(deviceAppVersionMapperRepository.findByUserId(1L))
        .thenReturn(deviceAppVersionMappers);
    Mockito.when(zoomPropertyService.getString(DEFAULT_APP_USER_IDS, "57")).thenReturn("1");
    appNotificationService.sendLoadingUnloadingNotification(notificationDTO);
  }

  @Test
  public void sendIBClearNotification() throws IOException {
    NotificationDTO notificationDTO = new NotificationDTO();
    notificationDTO.setEntityId(1L);
    notificationDTO.setTsMs(DateTime.now().getMillis());
    Map<String, String> metadata = new HashMap<>();
    metadata.put(ZoomCommunicationFieldNames.LOCATION_ID.name(), "1");
    notificationDTO.setMetadata(metadata);
    DeviceAppVersionMapper deviceAppVersionMapper1 = new DeviceAppVersionMapper();
    deviceAppVersionMapper1.setFirebaseToken(RandomStringUtils.randomAlphanumeric(5));
    DeviceAppVersionMapper deviceAppVersionMapper2 = new DeviceAppVersionMapper();
    List<DeviceAppVersionMapper> deviceAppVersionMappers =
        new ArrayList<>(Arrays.asList(deviceAppVersionMapper1, deviceAppVersionMapper2));
    Mockito.when(
            deviceAppVersionMapperRepository.findByUserIdInAndAppId(
                Mockito.any(), Mockito.any(ApplicationId.class)))
        .thenReturn(deviceAppVersionMappers);
    Mockito.when(zoomPropertyService.getString(DEFAULT_APP_USER_IDS, "57")).thenReturn("1");
    Mockito.when(
            consignmentScheduleService.getCacheForConsignmentAtLocation(
                Mockito.anyLong(), Mockito.anyLong()))
        .thenReturn(Optional.ofNullable(getDummyConsignmentScheduleCache()));
    Mockito.when(locationService.getLocationById(Mockito.anyLong())).thenReturn(getDummyLocation());
    Mockito.when(
            restClientUtilityService.executeRest(
                Mockito.anyString(), Mockito.any(), Mockito.any(), Mockito.any()))
        .thenReturn(Optional.of(TaskDto.builder().id(1L).taskType(TaskType.LOADING).build()));
    appNotificationService.sendIBClearEvent(notificationDTO);
    Mockito.verify(pushNotificationService, Mockito.atLeastOnce())
        .send(
            Mockito.any(),
            Mockito.anyString(),
            Mockito.anyString(),
            Mockito.any(ApplicationId.class));
  }

  private ConsignmentScheduleCache getDummyConsignmentScheduleCache() {
    ConsignmentScheduleCache consignmentSchedule = new ConsignmentScheduleCache();
    consignmentSchedule.setLocationId(1L);
    consignmentSchedule.setLocationType(LocationTypeV2.LOCATION);
    consignmentSchedule.setTripId(1L);
    consignmentSchedule.setTripType(ZoomTripType.TRIP);
    return consignmentSchedule;
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
    DeviceAppVersionMapper deviceAppVersionMapper2 = new DeviceAppVersionMapper();
    List<DeviceAppVersionMapper> deviceAppVersionMappers =
        new ArrayList<>(Arrays.asList(deviceAppVersionMapper1, deviceAppVersionMapper2));
    Mockito.when(zoomPropertyService.getString(DEFAULT_APP_USER_IDS, "57")).thenReturn("1");
    Mockito.when(
            deviceAppVersionMapperRepository.findByUserIdInAndAppId(Mockito.any(), Mockito.any()))
        .thenReturn(deviceAppVersionMappers);
    Mockito.when(zoomPropertyService.getString(DEFAULT_APP_USER_IDS, "57")).thenReturn("1");
    appNotificationService.sendPickupCancellationNotification(notificationDTO);
    Mockito.verify(pushNotificationService, Mockito.atLeastOnce())
        .send(Mockito.any(), Mockito.anyString(), Mockito.anyString(), Mockito.any());
  }
}
