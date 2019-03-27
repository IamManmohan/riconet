package com.rivigo.riconet.core.test;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.rivigo.riconet.core.constants.ConsignmentConstant;
import com.rivigo.riconet.core.service.AdministrativeEntityService;
import com.rivigo.riconet.core.service.ZoomUserMasterService;
import com.rivigo.riconet.core.service.impl.UserEntityMetadataServiceImpl;
import com.rivigo.zoom.common.enums.CnoteType;
import com.rivigo.zoom.common.enums.LocationEntityType;
import com.rivigo.zoom.common.enums.OperationalStatus;
import com.rivigo.zoom.common.enums.UserEntityType;
import com.rivigo.zoom.common.model.BusinessPartner;
import com.rivigo.zoom.common.model.Client;
import com.rivigo.zoom.common.model.Consignment;
import com.rivigo.zoom.common.model.PickupRunSheet;
import com.rivigo.zoom.common.model.neo4j.AdministrativeEntity;
import com.rivigo.zoom.common.repository.mysql.UserEntityMetadataRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

@Slf4j
public class UserEntityMetadataServiceTest {

  @InjectMocks private UserEntityMetadataServiceImpl userEntityMetadataService;

  @Mock private UserEntityMetadataRepository userEntityMetadataRepository;

  @Mock private AdministrativeEntityService administrativeEntityService;

  @Mock private ZoomUserMasterService zoomUserMasterService;

  @Rule public ExpectedException expectedException = ExpectedException.none();

  @Before
  public void initMocks() {
    MockitoAnnotations.initMocks(this);
  }

  @Test
  public void getClientClusterMetadataRivigoTest() {
    Consignment consignment = new Consignment();
    consignment.setOrganizationId(ConsignmentConstant.RIVIGO_ORGANIZATION_ID);
    consignment.setFromId(10l);
    Client client = new Client();
    client.setId(30l);
    consignment.setClient(client);
    AdministrativeEntity administrativeEntity = new AdministrativeEntity();
    administrativeEntity.setId(20l);
    when(administrativeEntityService.findParentCluster(consignment.getFromId()))
        .thenReturn(administrativeEntity);
    userEntityMetadataService.getUserClusterMetadata(consignment);
    verify(userEntityMetadataRepository, times(1))
        .findByLocationEntityTypeAndLocationEntityIdAndUserEntityTypeAndUserEntityIdAndStatus(
            LocationEntityType.CLUSTER, 20l, UserEntityType.CLIENT, 30l, OperationalStatus.ACTIVE);
  }

  @Test
  public void getClientClusterMetadataRetailPRSnullTest() {
    Consignment consignment = new Consignment();
    consignment.setOrganizationId(ConsignmentConstant.RIVIGO_ORGANIZATION_ID);
    consignment.setFromId(10l);
    consignment.setCnoteType(CnoteType.RETAIL);
    AdministrativeEntity administrativeEntity = new AdministrativeEntity();
    administrativeEntity.setId(20l);
    when(administrativeEntityService.findParentCluster(consignment.getFromId()))
        .thenReturn(administrativeEntity);
    userEntityMetadataService.getUserClusterMetadata(consignment);
  }

  @Test
  public void getClientClusterMetadataRetailBPnullTest() {
    Consignment consignment = new Consignment();
    consignment.setOrganizationId(ConsignmentConstant.RIVIGO_ORGANIZATION_ID);
    consignment.setFromId(10l);
    consignment.setCnoteType(CnoteType.RETAIL);
    consignment.setPrs(new PickupRunSheet());
    AdministrativeEntity administrativeEntity = new AdministrativeEntity();
    administrativeEntity.setId(20l);
    when(administrativeEntityService.findParentCluster(consignment.getFromId()))
        .thenReturn(administrativeEntity);
    userEntityMetadataService.getUserClusterMetadata(consignment);
  }

  @Test
  public void getClientClusterMetadataRetailTest() {
    Consignment consignment = new Consignment();
    consignment.setOrganizationId(ConsignmentConstant.RIVIGO_ORGANIZATION_ID);
    consignment.setFromId(10l);
    consignment.setCnoteType(CnoteType.RETAIL);
    PickupRunSheet pickupRunSheet = new PickupRunSheet();
    BusinessPartner businessPartner = new BusinessPartner();
    businessPartner.setId(1L);
    pickupRunSheet.setBusinessPartner(businessPartner);
    consignment.setPrs(pickupRunSheet);
    AdministrativeEntity administrativeEntity = new AdministrativeEntity();
    administrativeEntity.setId(20l);
    when(administrativeEntityService.findParentCluster(consignment.getFromId()))
        .thenReturn(administrativeEntity);
    userEntityMetadataService.getUserClusterMetadata(consignment);
  }

  @Test
  public void getClientClusterMetadataBfTest() {
    Consignment consignment = new Consignment();
    consignment.setOrganizationId(ConsignmentConstant.RIVIGO_ORGANIZATION_ID + 1);
    consignment.setFromId(10l);
    AdministrativeEntity administrativeEntity = new AdministrativeEntity();
    administrativeEntity.setId(20l);
    when(administrativeEntityService.findParentCluster(consignment.getFromId()))
        .thenReturn(administrativeEntity);
    userEntityMetadataService.getUserClusterMetadata(consignment);
    verify(userEntityMetadataRepository, times(1))
        .findByLocationEntityTypeAndLocationEntityIdAndUserEntityTypeAndUserEntityIdAndStatus(
            LocationEntityType.CLUSTER,
            20l,
            UserEntityType.ORGANIZATION,
            ConsignmentConstant.RIVIGO_ORGANIZATION_ID + 1,
            OperationalStatus.ACTIVE);
  }
}
