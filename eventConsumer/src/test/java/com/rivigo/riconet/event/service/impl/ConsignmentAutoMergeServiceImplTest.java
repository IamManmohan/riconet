package com.rivigo.riconet.event.service.impl;

import static com.rivigo.riconet.core.enums.ZoomCommunicationFieldNames.CNOTE;
import static com.rivigo.riconet.core.enums.ZoomCommunicationFieldNames.CURRENT_LOCATION_ID;

import com.rivigo.riconet.core.dto.NotificationDTO;
import com.rivigo.riconet.core.service.ApiClientService;
import com.rivigo.riconet.core.service.ConsignmentService;
import com.rivigo.zoom.common.enums.ConsignmentStatus;
import com.rivigo.zoom.common.model.Consignment;
import java.util.Arrays;
import java.util.Map;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

public class ConsignmentAutoMergeServiceImplTest {

  @InjectMocks private ConsignmentAutoMergeServiceImpl autoMergeService;

  @Mock private ConsignmentService consignmentService;

  @Mock private ApiClientService apiClientService;

  private String parentCnote = "12121211";
  private String location = "1";

  @Before
  public void initMocks() {
    MockitoAnnotations.initMocks(this);
  }

  @Test
  public void autoMergeSecondaryConsignment() {
    Map<String, String> metadata =
        Map.of(CNOTE.name(), parentCnote + "-1", CURRENT_LOCATION_ID.name(), location);
    NotificationDTO notificationDTO =
        NotificationDTO.builder().entityId(1L).metadata(metadata).build();
    Consignment parentCn = new Consignment();
    parentCn.setLocationId(Long.valueOf(location));
    parentCn.setCnote(parentCnote);
    Consignment siblingCn = new Consignment();
    parentCn.setLocationId(Long.valueOf(location));
    parentCn.setCnote(parentCnote + "-2");
    Mockito.when(consignmentService.getConsignmentByCnote(parentCnote)).thenReturn(parentCn);
    Mockito.when(
            consignmentService.getChildCnotesAtLocation(
                parentCnote, Long.valueOf(location), ConsignmentStatus.RECEIVED_AT_OU))
        .thenReturn(Arrays.asList(parentCnote, parentCnote + "-2", parentCnote + "-1"));
    autoMergeService.autoMergeConsignments(notificationDTO);
  }
}
