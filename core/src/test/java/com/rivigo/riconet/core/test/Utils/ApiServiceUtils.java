package com.rivigo.riconet.core.test.Utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rivigo.riconet.core.dto.NotificationDTO;
import com.rivigo.riconet.core.dto.client.ClientIntegrationResponseDTO;
import com.rivigo.riconet.core.dto.client.FlipkartLoginResponseDTO;
import com.rivigo.riconet.core.dto.hilti.HiltiResponseDto;
import com.rivigo.riconet.core.enums.CnActionEventName;
import com.rivigo.riconet.core.enums.ZoomCommunicationFieldNames;
import com.rivigo.zoom.common.enums.ConsignmentLocationStatus;
import com.rivigo.zoom.common.enums.FileTypes;
import com.rivigo.zoom.common.enums.LocationTypeV2;
import com.rivigo.zoom.common.model.Box;
import com.rivigo.zoom.common.model.BoxHistory;
import com.rivigo.zoom.common.model.Consignment;
import com.rivigo.zoom.common.model.ConsignmentReadOnly;
import com.rivigo.zoom.common.model.ConsignmentSchedule;
import com.rivigo.zoom.common.model.ConsignmentUploadedFiles;
import com.rivigo.zoom.common.model.Pickup;
import com.rivigo.zoom.common.model.UndeliveredConsignment;
import com.rivigo.zoom.common.model.neo4j.Location;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.rivigo.zoom.common.repository.mysql.BoxHistoryRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.joda.time.DateTime;

/** Created by aditya on 3/5/18. */
@Slf4j
public class ApiServiceUtils {

  public static final Long PICKUP_ID = 1L;
  public static final Long CLIENT_ID = 1L;
  public static final Long CONSIGNMENT_ID = 1L;
  public static final Long START_LOCATION_ID = 1L;
  public static final Long CURRENT_LOCATION_ID = 2L;
  public static final Long END_LOCATION_ID = 3L;
  public static final Map<String, List<String>> CNOTE_TO_BARCODE_MAP =
      new HashMap<String, List<String>>() {
        {
          put(
              "1000010000",
              new ArrayList<String>() {
                {
                  add("10001");
                  add("10002");
                }
              });
          put(
              "1000110001",
              new ArrayList<String>() {
                {
                  add("20001");
                  add("20002");
                }
              });
        }
      };

  public static final List<String> BARCODE_LIST =
      new ArrayList<String>() {
        {
          add("30001");
          add("30002");
          add("30003");
          add("30004");
          add("30005");
        }
      };

  public static final Map<String, Map<String, String>> CNOTE_TO_METADATA_MAP =
      new HashMap<String, Map<String, String>>() {
        {
          put("2000120001", new HashMap<>());
          put("2000220002", new HashMap<>());
        }
      };

  public static JsonNode getSampleJsonNode() throws IOException {
    ObjectMapper mapper = new ObjectMapper();
    return mapper.readTree("{\"test-key\":\"test-value\"}");
  }

  public static JsonNode getDatastoreSuccessResponseSampleJsonNode() throws IOException {
    ObjectMapper mapper = new ObjectMapper();
    return mapper.readTree("{\"status\":\"SUCCESS\",\"payload\":\"true\"}");
  }

  public static JsonNode getDatastoreFailureResponseSampleJsonNode() throws IOException {
    ObjectMapper mapper = new ObjectMapper();
    return mapper.readTree(
        "{\"status\":\"FAILURE\",\"errorMessage\":\"Error while cleanup from datastore\"}");
  }

  public static NotificationDTO getDummyPickupCompleteNotificationDto() {
    Map<String, String> hmap = new HashMap<>();
    hmap.put(ZoomCommunicationFieldNames.CLIENT_ID.name(), CLIENT_ID.toString());
    return NotificationDTO.builder()
        .entityId(PICKUP_ID)
        .metadata(hmap)
        .eventName(CnActionEventName.PICKUP_COMPLETION.name())
        .tsMs(DateTime.now().getMillis())
        .build();
  }

  public static List<ConsignmentSchedule> getDummyConsignmentSchedule() {
    ConsignmentSchedule cs1 = new ConsignmentSchedule();
    cs1.setPlanStatus(ConsignmentLocationStatus.LEFT);
    cs1.setLocationId(START_LOCATION_ID);
    cs1.setLocationType(LocationTypeV2.LOCATION);
    ConsignmentSchedule cs2 = new ConsignmentSchedule();
    cs2.setPlanStatus(ConsignmentLocationStatus.REACHED);
    cs2.setLocationId(CURRENT_LOCATION_ID);
    cs2.setLocationType(LocationTypeV2.LOCATION);
    ConsignmentSchedule cs3 = new ConsignmentSchedule();
    cs3.setPlanStatus(ConsignmentLocationStatus.NOT_REACHED);
    cs3.setLocationId(END_LOCATION_ID);
    cs3.setLocationType(LocationTypeV2.LOCATION);
    return Arrays.asList(cs1, cs2, cs3);
  }

  public static List<ConsignmentSchedule> getDummyConsignmentScheduleAtDestination() {
    List<ConsignmentSchedule> cs = getDummyConsignmentSchedule();
    cs.get(1).setPlanStatus(ConsignmentLocationStatus.LEFT);
    cs.get(2).setPlanStatus(ConsignmentLocationStatus.REACHED);
    return cs;
  }

  public static NotificationDTO getDummyCnNotificationDtoForEvent(
      CnActionEventName eventName, String cnote) {
    Map<String, String> hmap = new HashMap<>();
    hmap.put(ZoomCommunicationFieldNames.CLIENT_ID.name(), CLIENT_ID.toString());
    hmap.put(ZoomCommunicationFieldNames.CNOTE.name(), cnote);
    return NotificationDTO.builder()
        .entityId(CONSIGNMENT_ID)
        .metadata(hmap)
        .eventName(eventName.name())
        .tsMs(DateTime.now().getMillis())
        .build();
  }

  public static Pickup getDummyPickup() {
    Pickup pickup = new Pickup();
    pickup.setPickupDate(DateTime.now());
    return pickup;
  }

  public static ConsignmentReadOnly getDummyConsignmentWithCnote(String cnote) {
    ConsignmentReadOnly consignment = new ConsignmentReadOnly();
    consignment.setFromLocationId(START_LOCATION_ID);
    consignment.setToLocationId(END_LOCATION_ID);
    consignment.setLocationId(CURRENT_LOCATION_ID);
    consignment.setCnote(cnote);
    consignment.setPromisedDeliveryDateTime(DateTime.now().plusDays(RandomUtils.nextInt(2, 10)));
    return consignment;
  }

  public static ConsignmentReadOnly getDummyConsignmentWithCnoteAtDestination(String cnote) {
    ConsignmentReadOnly consignment = getDummyConsignmentWithCnote(cnote);
    consignment.setLocationId(END_LOCATION_ID);
    log.info("Consignment {}", consignment);
    return consignment;
  }

  public static List<Location> getDummyLocations() {
    Location l1 = new Location();
    l1.setId(START_LOCATION_ID);
    l1.setName("DELHI");
    Location l2 = new Location();
    l2.setId(CURRENT_LOCATION_ID);
    l2.setName("BOMBAY");
    Location l3 = new Location();
    l3.setId(END_LOCATION_ID);
    l3.setName("BANGALORE");
    return Arrays.asList(l1, l2, l3);
  }

  public static List<ConsignmentUploadedFiles> getDummyCnUploadedFiles() {
    ConsignmentUploadedFiles c1 = new ConsignmentUploadedFiles();
    c1.setFileTypes(FileTypes.COD_DOD);
    c1.setS3URL("dummy1/a");
    ConsignmentUploadedFiles c2 = new ConsignmentUploadedFiles();
    c2.setFileTypes(FileTypes.DELIVERY_CHALLAN);
    c2.setS3URL("dummy1/b");
    ConsignmentUploadedFiles c3 = new ConsignmentUploadedFiles();
    c3.setFileTypes(FileTypes.POD);
    c3.setS3URL("dummy1/c");
    return Arrays.asList(c1, c2, c3);
  }

  public static UndeliveredConsignment getDummyUndeliveredConsignment() {
    UndeliveredConsignment undeliveredConsignment = new UndeliveredConsignment();
    undeliveredConsignment.setReason(RandomStringUtils.randomAlphabetic(10));
    undeliveredConsignment.setSubReason(RandomStringUtils.randomAlphabetic(10));
    return undeliveredConsignment;
  }

  public static List<Consignment> getDummyConignmentListFromCnoteList(List<String> cnoteList) {
    List<Consignment> consignmentList = new ArrayList<>();
    Long i = 1L;
    for (String cnote : cnoteList) {
      Consignment consignment = new Consignment();
      consignment.setId(i);
      consignment.setCnote(cnote);
      ++i;
      consignmentList.add(consignment);
    }
    return consignmentList;
  }

  public static Map<Long, String> getDummyIdToCnoteMap(List<String> cnoteList, List<Long> ids) {
    Map<Long, String> idToCnoteMap = new HashMap<Long, String>();
    for (Long id : ids) {
      idToCnoteMap.put(id, cnoteList.get(ids.indexOf(id)));
    }
    return idToCnoteMap;
  }

  public static List<Box> getDummyBoxList(List<Long> Ids, List<String> Cnotes) {
    List<Box> boxList = new ArrayList<>();
    for (Long id : Ids) {
      Box box = new Box();
      box.setId(id);
      box.setCnote(Cnotes.get(Ids.indexOf(id)));
      box.setBarCode(Cnotes.get(Ids.indexOf(id)));
      boxList.add(box);
    }
    return boxList;
  }

  public static List<BoxHistory> getDummyBoxHistoryList(List<Long> Ids, List<String> cnotes){
    List<BoxHistory> boxHistoryList = new ArrayList<>();
    for(Long id: Ids){
      BoxHistory boxHistory = new BoxHistory();
      boxHistory.setBoxId(id);
      boxHistory.setBarCode(cnotes.get(Ids.indexOf(id)));
    }
    return boxHistoryList;
  }

  public static Map<String, List<String>> getDummyCnoteToBarcodeMap(
      List<String> cnotes, List<List<String>> Barcodes) {
    Map<String, List<String>> cnoteToBarcodesMap = new HashMap<String, List<String>>();
    for (String cnote : cnotes) {
      cnoteToBarcodesMap.put(cnote, Barcodes.get(cnotes.indexOf(cnote)));
    }
    return cnoteToBarcodesMap;
  }

  public static HiltiResponseDto getHiltiResponseDTO() {
    HiltiResponseDto responseDto = new HiltiResponseDto();
    responseDto.setSuccessCount(1L);
    responseDto.setSuccessMessage(
        new ArrayList<String>() {
          {
            add("OK");
            add("OK");
          }
        });
    responseDto.setFailCount(0L);
    return responseDto;
  }

  public static FlipkartLoginResponseDTO getFlipkartLoginResponseDTO() {
    FlipkartLoginResponseDTO responseDto = new FlipkartLoginResponseDTO();
    responseDto.setHttpStatus("OK");
    responseDto.setSuccess(Boolean.TRUE);
    responseDto.setData(
        new HashMap<String, String>() {
          {
            put("access_token", "Bearer eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9");
          }
        });
    return responseDto;
  }

  public static ClientIntegrationResponseDTO getClientResponseDTO() {
    ClientIntegrationResponseDTO responseDto = new ClientIntegrationResponseDTO();
    responseDto.setHttpStatus("OK");
    responseDto.setSuccess(Boolean.TRUE);
    responseDto.setSuccessDescription("ok");
    responseDto.setSuccessCode("OK");
    return responseDto;
  }
}
