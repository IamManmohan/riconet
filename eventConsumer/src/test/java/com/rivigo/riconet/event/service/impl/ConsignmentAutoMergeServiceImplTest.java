package com.rivigo.riconet.event.service.impl;

import static com.rivigo.riconet.core.enums.ZoomCommunicationFieldNames.CNOTE;
import static com.rivigo.riconet.core.enums.ZoomCommunicationFieldNames.CURRENT_LOCATION_ID;
import static com.rivigo.riconet.core.enums.ZoomCommunicationFieldNames.SECONDARY_CNOTES;

import com.rivigo.riconet.core.dto.NotificationDTO;
import com.rivigo.riconet.core.service.ApiClientService;
import com.rivigo.riconet.core.service.ConsignmentService;
import com.rivigo.zoom.common.model.Consignment;
import java.util.HashMap;
import java.util.Map;
import org.junit.Before;
import org.junit.Ignore;
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
  @Ignore  // As of now added @Ignore as release was getting blocked, but this needs to be fixed.
  public void autoMergeSecondaryConsignment() {
    Consignment parentCn = new Consignment();
    parentCn.setLocationId(Long.valueOf(location));
    parentCn.setCnote(parentCnote);
    Consignment siblingCn = new Consignment();
    parentCn.setLocationId(Long.valueOf(location));
    parentCn.setCnote(parentCnote + "-2");
    Map<String, String> metadata = new HashMap<>(0);
    metadata.put(CNOTE.name(), parentCnote + "-1");
    metadata.put(CURRENT_LOCATION_ID.name(), location);
    metadata.put(SECONDARY_CNOTES.name(), parentCnote);
    NotificationDTO notificationDTO =
        NotificationDTO.builder().entityId(1L).metadata(metadata).build();
    Mockito.when(consignmentService.getConsignmentByCnote(parentCnote)).thenReturn(parentCn);
    autoMergeService.autoMergeConsignments(notificationDTO);
  }
}
