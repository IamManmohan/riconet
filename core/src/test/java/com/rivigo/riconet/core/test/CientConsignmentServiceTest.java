package com.rivigo.riconet.core.test;

import static com.rivigo.riconet.core.constants.ConsignmentConstant.METADATA;

import com.rivigo.riconet.core.service.BoxService;
import com.rivigo.riconet.core.service.ConsignmentService;
import com.rivigo.riconet.core.service.impl.ClientConsignmentServiceImpl;
import com.rivigo.riconet.core.test.Utils.ApiServiceUtils;
import com.rivigo.zoom.common.enums.BoxStatus;
import com.rivigo.zoom.common.enums.CustomFieldsMetadataIdentifier;
import com.rivigo.zoom.common.model.Box;
import com.rivigo.zoom.common.model.BoxHistory;
import com.rivigo.zoom.common.model.consignmentcustomfields.ConsignmentCustomFieldMetadata;
import com.rivigo.zoom.common.model.consignmentcustomfields.ConsignmentCustomFieldValue;
import com.rivigo.zoom.common.repository.mysql.ConsignmentCustomFieldMetadataRepository;
import com.rivigo.zoom.common.repository.mysql.ConsignmentCustomFieldValueRepository;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

public class CientConsignmentServiceTest {

  public static final List<Long> Ids = Arrays.asList(1L, 2L);

  public static final List<String> CNOTES = Arrays.asList("2000120001", "2000220002");

  @Mock private BoxService boxService;

  @Mock private ConsignmentService consignmentService;

  @Mock private ConsignmentCustomFieldMetadataRepository consignmentCustomFieldMetadataRepository;

  @Mock private ConsignmentCustomFieldValueRepository consignmentCustomFieldValueRepository;

  @InjectMocks private ClientConsignmentServiceImpl clientConsignmentService;

  @Before
  public void initMocks() {
    MockitoAnnotations.initMocks(this);
    Mockito.when(consignmentService.getIdToCnoteMap(CNOTES))
        .thenReturn(ApiServiceUtils.getDummyIdToCnoteMap(CNOTES, Ids));

    ConsignmentCustomFieldMetadata consignmentCustomFieldMetadata =
        new ConsignmentCustomFieldMetadata();
    consignmentCustomFieldMetadata.setId(12L);
    Mockito.when(
            consignmentCustomFieldMetadataRepository
                .findByCustomFieldsMetadataIdentifierAndFieldName(
                    CustomFieldsMetadataIdentifier.CN_CREATE_UPDATE_API, METADATA))
        .thenReturn(consignmentCustomFieldMetadata);

    Mockito.when(boxService.getByConsignmentIdInIncludingInactive(new HashSet<>(Ids)))
        .thenReturn(ApiServiceUtils.getDummyBoxList(Ids, CNOTES));

    Mockito.when(boxService.getByConsignmentIdIncludingInactive(Ids.get(0)))
        .thenReturn(ApiServiceUtils.getDummyBoxList(Ids, CNOTES));

    Mockito.when(
            boxService.getHistoryByBoxIdInAndStatus(
                Matchers.eq(Ids), Matchers.eq(BoxStatus.DRAFTED)))
        .thenReturn(ApiServiceUtils.getDummyBoxHistoryList(Ids, CNOTES));
  }

  @Test
  public void getCnoteToConsignmentMetadataMapFromCnoteListTest() {
    Map<String, String> map = new HashMap<>();
    map.put("key", "val");
    ConsignmentCustomFieldValue consignmentCustomFieldValue = new ConsignmentCustomFieldValue();
    consignmentCustomFieldValue.setConsignmentId(1L);
    consignmentCustomFieldValue.setJsonValue(map);
    ConsignmentCustomFieldValue consignmentCustomFieldValue1 = new ConsignmentCustomFieldValue();
    consignmentCustomFieldValue1.setConsignmentId(2L);
    consignmentCustomFieldValue1.setJsonValue(map);
    Mockito.when(
            consignmentCustomFieldValueRepository.findByConsignmentIdInAndMetadataIdAndIsActiveTrue(
                Mockito.any(), Mockito.any()))
        .thenReturn(Arrays.asList(consignmentCustomFieldValue, consignmentCustomFieldValue1));

    Map<String, Map<String, String>> cnoteToMetadataMapOriginal =
        clientConsignmentService.getCnoteToConsignmentMetadataMapFromCnoteList(CNOTES);
    Assert.assertEquals("val", cnoteToMetadataMapOriginal.get(CNOTES.get(0)).get("key"));
    Assert.assertEquals("val", cnoteToMetadataMapOriginal.get(CNOTES.get(1)).get("key"));
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
  public void getBarcodeListFromConsignmentIdTest1() {
    List<String> barcodesOriginal = CNOTES;
    List<String> barcodesActual =
        clientConsignmentService.getBarcodeListFromConsignmentId(Ids.get(0));

    Assert.assertEquals(barcodesOriginal.size(), barcodesActual.size());
    Assert.assertEquals(barcodesOriginal.get(0), barcodesActual.get(0));
    Assert.assertEquals(barcodesOriginal.get(1), barcodesActual.get(1));
  }

  // flipkart barcode test
  @Test
  public void getBarcodeListFromConsignmentIdTest2() {
    String sampleBarcode = "fk_mp_436043_450";
    Box box = new Box();
    box.setBarCode(sampleBarcode + "_1581312901444");
    box.setStatus(BoxStatus.DELETED);
    List<Box> barcodesOriginal = Collections.singletonList(box);
    Mockito.when(boxService.getByConsignmentIdIncludingInactive(1L)).thenReturn(barcodesOriginal);

    List<String> barcodesActual = clientConsignmentService.getBarcodeListFromConsignmentId(1L);

    Assert.assertEquals(barcodesActual.get(0), sampleBarcode);
  }

  @Test
  public void getBarcodeListFromConsignmentIdTest3() {
    Box box = new Box();
    box.setId(1L);
    box.setBarCode("fk_mp_436043_450");
    box.setStatus(BoxStatus.CREATED);
    Mockito.when(boxService.getByConsignmentIdIncludingInactive(1L))
        .thenReturn(Collections.singletonList(box));

    BoxHistory boxHistory1 = new BoxHistory();
    boxHistory1.setBarCode("myBarcode");
    boxHistory1.setBoxId(1L);
    boxHistory1.setStatus(BoxStatus.DRAFTED);

    BoxHistory boxHistory2 = new BoxHistory();
    boxHistory2.setBarCode("myBarcode2");
    boxHistory2.setBoxId(2L);
    boxHistory2.setStatus(BoxStatus.DRAFTED);

    Mockito.when(
            boxService.getHistoryByBoxIdInAndStatus(
                Matchers.eq(Collections.singletonList(1L)), Matchers.eq(BoxStatus.DRAFTED)))
        .thenReturn(Arrays.asList(boxHistory1, boxHistory2));

    List<String> barcodesActual = clientConsignmentService.getBarcodeListFromConsignmentId(1L);

    Assert.assertEquals(barcodesActual.get(0), boxHistory1.getBarCode());
  }
}
