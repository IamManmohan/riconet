package com.rivigo.riconet.core.test;

import static org.junit.Assert.assertEquals;

import com.rivigo.riconet.core.dto.ConsignmentCompletionEventDTO;
import com.rivigo.riconet.core.service.impl.QcServiceImpl;
import com.rivigo.zoom.common.dto.client.ClientClusterMetadataDTO;
import com.rivigo.zoom.common.dto.client.ClientPincodeMetadataDTO;
import com.rivigo.zoom.common.enums.CnoteType;
import com.rivigo.zoom.common.model.Consignment;
import com.rivigo.zoom.common.repository.mysql.ruleengine.RuleEngineRuleRepository;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author ajay mittal
 */

public class QCServiceTestUsingTesterBase extends TesterBase{

  @Autowired
  QcServiceImpl qcService;

  @Mock
  RuleEngineRuleRepository ruleEngineRuleRepository;


  @Test
  public void checkTestCase(){
    ConsignmentCompletionEventDTO completionEventDTO = getConsignmentCompletionDTO();
    Consignment consignment= getConsignmentDTO();

    boolean result = qcService.check(completionEventDTO,consignment);
    assertEquals(result,false);
  }

  private ConsignmentCompletionEventDTO getConsignmentCompletionDTO(){
    ConsignmentCompletionEventDTO consignmentCompletionEventDTO = new ConsignmentCompletionEventDTO();
    ClientPincodeMetadataDTO clientPincodeMetadataDTO = new ClientPincodeMetadataDTO();
    ClientClusterMetadataDTO clientClusterMetadataDTO = new ClientClusterMetadataDTO();

    clientPincodeMetadataDTO.setCount((long)31);
    clientPincodeMetadataDTO.setMinWeight(8.0);
    clientPincodeMetadataDTO.setMaxWeight(12.0);
    clientPincodeMetadataDTO.setMinChargedWeightPerWeight(8.0);
    clientPincodeMetadataDTO.setMaxChargedWeightPerWeight(12.0);
    clientPincodeMetadataDTO.setMinInvoicePerWeight(80.0);
    clientPincodeMetadataDTO.setMaxInvoicePerWeight(120.0);

    consignmentCompletionEventDTO.setClientClusterMetadataDTO(clientClusterMetadataDTO);
    consignmentCompletionEventDTO.setClientPincodeMetadataDTO(clientPincodeMetadataDTO);
    consignmentCompletionEventDTO.setCnote("NORMAL");

    return consignmentCompletionEventDTO;
  }

  private Consignment getConsignmentDTO(){
    Consignment consignment = new Consignment();
    consignment.setWeight(10.0);
    consignment.setChargedWeight(10.0);
    consignment.setValue(100.0);
    consignment.setCnoteType(CnoteType.NORMAL);

    return consignment;
  }
}
