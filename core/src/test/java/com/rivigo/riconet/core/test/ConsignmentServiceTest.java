package com.rivigo.riconet.core.test;

import com.rivigo.riconet.core.dto.ConsignmentBasicDTO;
import com.rivigo.riconet.core.service.ConsignmentScheduleService;
import com.rivigo.riconet.core.service.ConsignmentService;
import com.rivigo.riconet.core.service.OrganizationService;
import com.rivigo.riconet.core.service.ZoomBackendAPIClientService;
import com.rivigo.riconet.core.service.impl.ConsignmentServiceImpl;
import com.rivigo.riconet.core.test.Utils.ApiServiceUtils;
import com.rivigo.zoom.common.model.Consignment;
import com.rivigo.zoom.common.model.ConsignmentSchedule;
import com.rivigo.zoom.common.repository.mysql.ConsignmentRepository;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

public class ConsignmentServiceTest {
  public static final List<Long> Ids = Arrays.asList(1L, 2L, 3L, 4L);

  public static final List<String> CNOTES =
      Arrays.asList("1000110001", "1000210002", "1000310003", "1000410004");

  @Mock private ConsignmentRepository consignmentRepo;

  @Mock private OrganizationService organizationService;

  @Mock private ZoomBackendAPIClientService zoomBackendAPIClientService;

  @Mock private ConsignmentScheduleService consignmentScheduleService;

  @Mock private ConsignmentService consignmentServiceMock;

  @InjectMocks private ConsignmentServiceImpl consignmentService;

  @Before
  public void initMocks() {
    MockitoAnnotations.initMocks(this);
    Mockito.when(consignmentRepo.findByCnoteIn(CNOTES))
        .thenReturn(ApiServiceUtils.getDummyConignmentListFromCnoteList(CNOTES));
  }

  @Test
  public void getConsignmentListByCnoteListTest() {
    List<Consignment> consignmentListOriginal =
        ApiServiceUtils.getDummyConignmentListFromCnoteList(CNOTES);
    List<Consignment> consignmentListActual =
        consignmentService.getConsignmentListByCnoteList(CNOTES);

    Assert.assertEquals(consignmentListOriginal.size(), consignmentListActual.size());
    Assert.assertEquals(
        consignmentListOriginal.get(0).getCnote(), consignmentListActual.get(0).getCnote());
    Assert.assertEquals(
        consignmentListOriginal.get(3).getCnote(), consignmentListActual.get(3).getCnote());
  }

  @Test
  public void getIdToCnoteMapTest() {
    Map<Long, String> idToCnoteMapOriginal = ApiServiceUtils.getDummyIdToCnoteMap(CNOTES, Ids);
    Map<Long, String> idToCnoteMapActual = consignmentService.getIdToCnoteMap(CNOTES);

    Assert.assertEquals(idToCnoteMapOriginal.size(), idToCnoteMapActual.size());
    Assert.assertEquals(
        idToCnoteMapOriginal.get("1000210002"), idToCnoteMapActual.get("1000210002"));
  }

  @Test
  public void triggerBfFlowsTest() {
    ConsignmentBasicDTO unloadingEventDTO = ApiServiceUtils.getDummyConsignmentBasicDTO();
    Mockito.when(consignmentRepo.getOrganizationId(unloadingEventDTO.getConsignmentId()))
        .thenReturn(new BigInteger("1003"));
    Mockito.when(organizationService.getById(1003L))
        .thenReturn(ApiServiceUtils.getDummyOrganization());
    Mockito.doNothing()
        .when(zoomBackendAPIClientService)
        .triggerInsurancePolicyGeneration(unloadingEventDTO.getCnote());
    List<ConsignmentSchedule> scheduleList = ApiServiceUtils.getDummyConsignmentSchedule();
    Mockito.when(consignmentScheduleService.getActivePlan(unloadingEventDTO.getConsignmentId()))
        .thenReturn(scheduleList);
    Consignment cn = ApiServiceUtils.getDummyConsignment();
    Mockito.when(consignmentServiceMock.getConsignmentById(unloadingEventDTO.getConsignmentId()))
        .thenReturn(cn);
    Mockito.doNothing()
        .when(zoomBackendAPIClientService)
        .recalculateCpdOfBf(unloadingEventDTO.getConsignmentId());
    consignmentService.triggerBfFlows(unloadingEventDTO);
    Assert.assertNotNull(cn);
  }
}
