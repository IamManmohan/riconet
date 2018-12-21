package com.rivigo.riconet.core.test;

import com.rivigo.riconet.core.service.ConsignmentService;
import com.rivigo.riconet.core.service.impl.ClientConsignmentServiceImpl;
import com.rivigo.riconet.core.test.Utils.ApiServiceUtils;
import com.rivigo.zoom.common.model.mongo.ClientConsignmentMetadata;
import com.rivigo.zoom.common.repository.mongo.ClientConsignmentMetadataRepository;
import com.rivigo.zoom.common.repository.mysql.BoxRepository;
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

public class CientConsignmentServiceTest {

  public static final List<Long> Ids = Arrays.asList(1L, 2L);

  public static final List<String> CNOTES = Arrays.asList("2000120001", "2000220002");

  @Mock private BoxRepository boxRepository;

  @Mock private ConsignmentService consignmentService;

  @Mock private ClientConsignmentMetadataRepository clientConsignmentMetadataRepository;

  @InjectMocks private ClientConsignmentServiceImpl clientConsignmentService;

  @Before
  public void initMocks() {
    MockitoAnnotations.initMocks(this);
    Mockito.when(consignmentService.getIdToCnoteMap(CNOTES))
        .thenReturn(ApiServiceUtils.getDummyIdToCnoteMap(CNOTES, Ids));

    Mockito.when(clientConsignmentMetadataRepository.findByConsignmentIdIn(Ids))
        .thenReturn(ApiServiceUtils.getDummyMetadataList(Ids));

    Mockito.when(boxRepository.findByConsignmentIdIn(Ids))
        .thenReturn(ApiServiceUtils.getDummyBoxList(Ids, CNOTES));

    Mockito.when(boxRepository.findByConsignmentId(Ids.get(0)))
        .thenReturn(ApiServiceUtils.getDummyBoxList(Ids, CNOTES));
  }

  @Test
  public void getCnoteToConsignmentMetadataMapFromCnoteListTest() {
    Map<String, ClientConsignmentMetadata> cnoteToMetadataMapOriginal =
        ApiServiceUtils.CNOTE_TO_METADATA_MAP;
    Map<String, ClientConsignmentMetadata> cnoteToMetadataMapActual =
        clientConsignmentService.getCnoteToConsignmentMetadataMapFromCnoteList(CNOTES);

    Assert.assertEquals(cnoteToMetadataMapOriginal.size(), cnoteToMetadataMapActual.size());
    Assert.assertEquals(
        cnoteToMetadataMapOriginal.keySet().size(), cnoteToMetadataMapActual.keySet().size());
  }

  @Test
  public void getCnoteToBarcodeMapFromCnoteListTest() {
    Map<String, List<String>> cnoteToBarcodeMapOriginal =
        ApiServiceUtils.getDummyCnoteToBarcodeMap(
            CNOTES, Arrays.asList(Arrays.asList("2000120001"), Arrays.asList("2000220002")));
    Map<String, List<String>> cnoteToBarcodeMapActual =
        clientConsignmentService.getCnoteToBarcodeMapFromCnoteList(CNOTES);

    Assert.assertEquals(cnoteToBarcodeMapOriginal.size(), cnoteToBarcodeMapActual.size());
    Assert.assertEquals(
        cnoteToBarcodeMapOriginal.keySet().size(), cnoteToBarcodeMapActual.keySet().size());
  }

  @Test
  public void getBarcodeListFromConsignmentIdTest() {
    List<String> barcodesOriginal = CNOTES;
    List<String> barcodesActual =
        clientConsignmentService.getBarcodeListFromConsignmentId(Ids.get(0));

    Assert.assertEquals(barcodesOriginal.size(), barcodesActual.size());
    Assert.assertEquals(barcodesOriginal.get(0), barcodesActual.get(0));
    Assert.assertEquals(barcodesOriginal.get(1), barcodesActual.get(1));
  }
}
