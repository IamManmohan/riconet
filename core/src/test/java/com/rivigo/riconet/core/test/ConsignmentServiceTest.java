package com.rivigo.riconet.core.test;

import com.rivigo.riconet.core.service.impl.ConsignmentServiceImpl;
import com.rivigo.riconet.core.test.Utils.ApiServiceUtils;
import com.rivigo.zoom.common.model.Consignment;
import com.rivigo.zoom.common.repository.mysql.ConsignmentRepository;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class ConsignmentServiceTest {
    public static final List<Long> Ids =
            Arrays.asList(1L, 2L, 3L, 4L);

    public static final List<String> CNOTES =
            Arrays.asList("1000110001", "1000210002", "1000310003", "1000410004");

    @Mock
    private ConsignmentRepository consignmentRepo;

    @InjectMocks
    private ConsignmentServiceImpl consignmentService;

    @Before
    public void initMocks() {
        MockitoAnnotations.initMocks(this);
        Mockito.when(
                consignmentRepo.findByCnoteIn(
                        CNOTES))
                .thenReturn(ApiServiceUtils.getDummyConignmentListFromCnoteList(CNOTES));
    }

    @Test
    public void getConsignmentListByCnoteListTest() {
        List<Consignment> consignmentListOriginal = ApiServiceUtils.getDummyConignmentListFromCnoteList(CNOTES);
        List<Consignment> consignmentListActual = consignmentService.getConsignmentListByCnoteList(CNOTES);

        Assert.assertEquals(consignmentListOriginal.size(), consignmentListActual.size());
        Assert.assertEquals(consignmentListOriginal.get(0).getCnote(), consignmentListActual.get(0).getCnote());
        Assert.assertEquals(consignmentListOriginal.get(3).getCnote(), consignmentListActual.get(3).getCnote());
    }

    @Test
    public void getIdToCnoteMapTest() {
        Map<Long, String> idToCnoteMapOriginal = ApiServiceUtils.getDummyIdToCnoteMap(CNOTES, Ids);
        Map<Long, String> idToCnoteMapActual = consignmentService.getIdToCnoteMap(CNOTES);

        Assert.assertEquals(idToCnoteMapOriginal.size(), idToCnoteMapActual.size());
        Assert.assertEquals(idToCnoteMapOriginal.get("1000210002"), idToCnoteMapActual.get("1000210002"));
    }
}
