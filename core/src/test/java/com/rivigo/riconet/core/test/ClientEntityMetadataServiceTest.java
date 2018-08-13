package com.rivigo.riconet.core.test;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.rivigo.riconet.core.constants.ConsignmentConstant;
import com.rivigo.riconet.core.service.impl.ClientEntityMetadataServiceImpl;
import com.rivigo.zoom.common.enums.ClientEntityType;
import com.rivigo.zoom.common.enums.OperationalStatus;
import com.rivigo.zoom.common.model.Client;
import com.rivigo.zoom.common.model.Consignment;
import com.rivigo.zoom.common.model.neo4j.AdministrativeEntity;
import com.rivigo.zoom.common.repository.mysql.ClientEntityMetadataRepository;
import com.rivigo.zoom.common.repository.neo4j.AdministrativeEntityRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

@Slf4j
public class ClientEntityMetadataServiceTest {

  @InjectMocks private ClientEntityMetadataServiceImpl clientEntityMetadataService;

  @Mock private ClientEntityMetadataRepository clientEntityMetadataRepository;

  @Mock private AdministrativeEntityRepository administrativeEntityRepository;

  @Rule public ExpectedException expectedException = ExpectedException.none();

  @Before
  public void initMocks() {
    MockitoAnnotations.initMocks(this);
  }

  @Test
  public void getByEntityTypeAndEntityIdAndClientIdAndOrganizationIdAndStatusTest() {
    clientEntityMetadataService.getByEntityTypeAndEntityIdAndClientIdAndOrganizationIdAndStatus(
        ClientEntityType.CLUSTER, 2l, 3l, 4l, OperationalStatus.ACTIVE);
    verify(clientEntityMetadataRepository, times(1))
        .findByEntityTypeAndEntityIdAndClientIdAndOrganizationIdAndStatus(
            ClientEntityType.CLUSTER, 2l, 3l, 4l, OperationalStatus.ACTIVE);
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
    when(administrativeEntityRepository.findParentCluster(consignment.getFromId()))
        .thenReturn(administrativeEntity);
    clientEntityMetadataService.getClientClusterMetadata(consignment);
    verify(clientEntityMetadataRepository, times(1))
        .findByEntityTypeAndEntityIdAndClientIdAndOrganizationIdAndStatus(
            ClientEntityType.CLUSTER, 20l, 30l, null, OperationalStatus.ACTIVE);
  }

  @Test
  public void getClientClusterMetadataBfTest() {
    Consignment consignment = new Consignment();
    consignment.setOrganizationId(ConsignmentConstant.RIVIGO_ORGANIZATION_ID + 1);
    consignment.setFromId(10l);
    AdministrativeEntity administrativeEntity = new AdministrativeEntity();
    administrativeEntity.setId(20l);
    when(administrativeEntityRepository.findParentCluster(consignment.getFromId()))
        .thenReturn(administrativeEntity);
    clientEntityMetadataService.getClientClusterMetadata(consignment);
    verify(clientEntityMetadataRepository, times(1))
        .findByEntityTypeAndEntityIdAndClientIdAndOrganizationIdAndStatus(
            ClientEntityType.CLUSTER,
            20l,
            null,
            ConsignmentConstant.RIVIGO_ORGANIZATION_ID + 1,
            OperationalStatus.ACTIVE);
  }
}
