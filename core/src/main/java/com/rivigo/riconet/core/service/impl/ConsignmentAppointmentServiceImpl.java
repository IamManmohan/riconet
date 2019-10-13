package com.rivigo.riconet.core.service.impl;

import com.rivigo.riconet.core.constants.EmailConstant;
import com.rivigo.riconet.core.dto.notification.AppointmentNotification;
import com.rivigo.riconet.core.enums.ZoomPropertyName;
import com.rivigo.riconet.core.service.ConsignmentAppointmentService;
import com.rivigo.riconet.core.service.ConsignmentScheduleService;
import com.rivigo.riconet.core.service.ConsignmentService;
import com.rivigo.riconet.core.service.EmailService;
import com.rivigo.riconet.core.service.LocationService;
import com.rivigo.riconet.core.service.UserMasterService;
import com.rivigo.riconet.core.service.ZoomPropertyService;
import com.rivigo.riconet.core.service.ZoomUserMasterService;
import com.rivigo.zoom.common.dto.AppointmentNotificationDTO;
import com.rivigo.zoom.common.dto.LocationBasicDTO;
import com.rivigo.zoom.common.dto.UserBasicDTO;
import com.rivigo.zoom.common.enums.ConsignmentLocationStatus;
import com.rivigo.zoom.common.enums.ConsignmentStatus;
import com.rivigo.zoom.common.enums.EmailDlName;
import com.rivigo.zoom.common.enums.ZoomUserType;
import com.rivigo.zoom.common.model.Consignment;
import com.rivigo.zoom.common.model.ConsignmentAppointmentRecord;
import com.rivigo.zoom.common.model.ConsignmentHistory;
import com.rivigo.zoom.common.model.ConsignmentSchedule;
import com.rivigo.zoom.common.model.User;
import com.rivigo.zoom.common.model.ZoomUser;
import com.rivigo.zoom.common.model.neo4j.Location;
import com.rivigo.zoom.common.repository.mysql.ConsignmentAppointmentRepository;
import com.rivigo.zoom.exceptions.ZoomException;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.text.StrSubstitutor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Days;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

@Slf4j
@Service
public class ConsignmentAppointmentServiceImpl implements ConsignmentAppointmentService {

  @Autowired private ConsignmentAppointmentRepository consignmentAppointmentRepository;

  @Autowired private ConsignmentService consignmentService;

  @Autowired private LocationService locationService;

  @Autowired private UserMasterService userMasterService;

  @Autowired private ZoomUserMasterService zoomUserMasterService;

  @Autowired private EmailService emailService;

  @Autowired private ZoomPropertyService zoomPropertyService;

  @Autowired private ConsignmentScheduleService consignmentScheduleService;

  private static final String IST_TIME_ZONE_ID = "Asia/Kolkata";

  @Override
  public void processAppointmentNotification(AppointmentNotificationDTO dto) {
    if (dto == null) {
      return;
    }
    List<AppointmentNotification> notificationList;
    Map<Long, List<AppointmentNotification>> notificationMap;
    List<ConsignmentStatus> statusList = new ArrayList();
    statusList.add(ConsignmentStatus.DELIVERED_POD_PENDING);
    statusList.add(ConsignmentStatus.DELIVERED);
    DateTime now = DateTime.now();
    switch (dto.getNotificationType()) {
      case APPOINTMENT_DELIVERED_LATE:
        processLateDelivery(dto);
        break;
      case APPOINTMENT_MISSED:
        notificationList =
            processAppointmentMissed(new DateTime(dto.getLastExecutionTime()), now, statusList);
        notificationList.forEach(
            notification ->
                sendNotifications(
                    notification,
                    ZoomPropertyName.APPOINTMENT_MISSED_EMAIL,
                    ZoomPropertyName.APPOINTMENT_MISSED_SUBJECT));
        break;
      case APPOINTMENT_WRONG_UNDELIVERED_MARKED:
        processFakeUndeliveryReason(dto);
        break;
      case APPOINTMENT_MISSED_SUMMARY:
        notificationList =
            processAppointmentMissed(new DateTime(dto.getLastExecutionTime()), now, statusList);
        notificationMap =
            notificationList
                .stream()
                .collect(
                    Collectors.groupingBy(
                        notification -> notification.getResponsibleLocation().getId()));
        notificationMap
            .keySet()
            .forEach(
                list ->
                    sendNotificationList(
                        notificationMap.get(list),
                        ZoomPropertyName.APPOINTMENT_MISSED_SUMMARY_EMAIL,
                        ZoomPropertyName.APPOINTMENT_MISSED_SUMMARY_SUBJECT));
        break;
      case APPOINTMENT_NOT_OFD_FIRST_HALF:
        processAppointmentNotOfd(
            statusList,
            now,
            now.withZone(DateTimeZone.forID(IST_TIME_ZONE_ID)).withMillisOfDay(0).plusHours(12));
        break;
      case APPOINTMENT_NOT_OFD_SECOND_HALF:
        processAppointmentNotOfd(
            statusList,
            now,
            now.withZone(DateTimeZone.forID(IST_TIME_ZONE_ID)).plusDays(1).withMillisOfDay(0));
        break;
      default:
        log.info("This notificationType is not handled in this consumer");
        break;
    }
  }

  private void processAppointmentNotOfd(
      List<ConsignmentStatus> statusList, DateTime start, DateTime end) {
    statusList.add(ConsignmentStatus.OUT_FOR_DELIVERY);
    List<AppointmentNotification> notificationList =
        processAppointmentMissed(start, end, statusList);
    Map<Long, List<AppointmentNotification>> notificationMap =
        notificationList
            .stream()
            .collect(
                Collectors.groupingBy(
                    notification -> notification.getResponsibleLocation().getId()));
    notificationMap
        .keySet()
        .forEach(
            list ->
                sendNotificationList(
                    notificationMap.get(list),
                    ZoomPropertyName.APPOINTMENT_NOT_OFD_EMAIL,
                    ZoomPropertyName.APPOINTMENT_NOT_OFD_SUBJECT));
  }

  private List<AppointmentNotification> processAppointmentMissed(
      final DateTime start, final DateTime end, List<ConsignmentStatus> statusList) {
    List<ConsignmentAppointmentRecord> consignmentAppointmentRecordList =
        consignmentAppointmentRepository.findByIsActiveAndAppointmentTimeBetween(
            Boolean.TRUE, start, end);
    List<Long> consignmentIdList =
        consignmentAppointmentRecordList
            .stream()
            .map(ConsignmentAppointmentRecord::getConsignmentId)
            .collect(Collectors.toList());
    List<Consignment> consignments =
        consignmentService.findByIdInAndStatusNotInAndDeliveryHandoverIsNull(
            consignmentIdList, statusList);
    Map<Long, List<ConsignmentSchedule>> cnToScheduleMap =
        consignmentScheduleService.getActivePlansMapByIds(consignmentIdList);

    Map<Long, Location> locationMap = locationService.getLocationMap();

    Set<String> bccList = emailService.getEmails(EmailDlName.APPOINTMENT_NOTIFICATION);
    Set<String> defaultCcList = emailService.getEmails(EmailDlName.APPOINTMENT_NOTIFICATION_CC);

    return consignments
        .stream()
        .map(
            consignment ->
                processAppointmentMissedConsignment(
                    consignment,
                    cnToScheduleMap.get(consignment.getId()),
                    locationMap,
                    defaultCcList,
                    bccList))
        .collect(Collectors.toList());
  }

  private AppointmentNotification processAppointmentMissedConsignment(
      Consignment consignment,
      List<ConsignmentSchedule> consignmentScheduleList,
      Map<Long, Location> locationMap,
      Set<String> defaultCcList,
      Set<String> bccList) {
    AppointmentNotification appointmentNotification = new AppointmentNotification();
    appointmentNotification.setConsignmentId(consignment.getId());
    Location loc = locationMap.get(getCurrentSchedule(consignmentScheduleList).getLocationId());
    appointmentNotification.setResponsibleLocation(getLocationDto(loc));
    appointmentNotification.setCnote(consignment.getCnote());
    updateStakeHolders(appointmentNotification, loc, defaultCcList, bccList);
    return appointmentNotification;
  }

  private ConsignmentSchedule getCurrentSchedule(
      List<ConsignmentSchedule> consignmentScheduleList) {
    Optional<ConsignmentSchedule> present =
        consignmentScheduleList
            .stream()
            .filter(
                consignmentSchedule ->
                    consignmentSchedule.getPlanStatus() != ConsignmentLocationStatus.LEFT)
            .findFirst();

    if (present.isPresent()) {
      return present.get();
    }
    throw new ZoomException(
        "Error in consignment schedule: consignment is not left from any location ");
  }

  private void processLateDelivery(AppointmentNotificationDTO dto) {
    ConsignmentHistory cnHistory =
        consignmentService.getLastScanByCnId(
            dto.getConsignmentId(), Arrays.asList(ConsignmentStatus.OUT_FOR_DELIVERY.name()));
    AppointmentNotification appointmentNotification = new AppointmentNotification();
    appointmentNotification.setConsignmentId(cnHistory.getConsignmentId());
    Location loc = locationService.getLocationById(cnHistory.getLocationId());
    appointmentNotification.setResponsibleLocation(getLocationDto(loc));
    appointmentNotification.setCnote(
        consignmentService.getCnoteByIdAndIsActive(cnHistory.getConsignmentId()));
    User user = userMasterService.getById(cnHistory.getCreatedById());
    appointmentNotification.setResponsiblePerson(getUserDto(user));
    appointmentNotification.getEmailIdList().add(user.getEmail());
    Set<String> bccList = emailService.getEmails(EmailDlName.APPOINTMENT_NOTIFICATION);
    Set<String> defaultCcList = emailService.getEmails(EmailDlName.APPOINTMENT_NOTIFICATION_CC);
    updateStakeHolders(appointmentNotification, loc, defaultCcList, bccList);
    DateTime deliveryTime =
        (new DateTime(dto.getDeliveryTime())).withZone(DateTimeZone.forID(IST_TIME_ZONE_ID));
    DateTime appointmentTime =
        (new DateTime(dto.getAppoitnmentTime())).withZone(DateTimeZone.forID(IST_TIME_ZONE_ID));
    if (Days.daysBetween(deliveryTime.toLocalDate(), appointmentTime.toLocalDate()).getDays()
        == 0) {
      sendNotifications(
          appointmentNotification,
          ZoomPropertyName.APPOINTMENT_DELIVERED_LATE_SAME_DAY_EMAIL,
          ZoomPropertyName.APPOINTMENT_DELIVERED_LATE_SAME_DAY_SUBJECT);
    } else {
      sendNotifications(
          appointmentNotification,
          ZoomPropertyName.APPOINTMENT_DELIVERED_LATE_DIFFERENT_DAY_EMAIL,
          ZoomPropertyName.APPOINTMENT_DELIVERED_LATE_DIFFERENT_DAY_SUBJECT);
    }
  }

  private void processFakeUndeliveryReason(AppointmentNotificationDTO dto) {
    AppointmentNotification appointmentNotification = new AppointmentNotification();
    appointmentNotification.setConsignmentId(dto.getConsignmentId());
    Location loc = locationService.getLocationById(dto.getResponsibleLocationId());
    appointmentNotification.setResponsibleLocation(getLocationDto(loc));
    appointmentNotification.setCnote(
        consignmentService.getCnoteByIdAndIsActive(dto.getConsignmentId()));
    User user = userMasterService.getById(dto.getResponsibleUserId());
    appointmentNotification.setResponsiblePerson(getUserDto(user));
    appointmentNotification.getEmailIdList().add(user.getEmail());
    Set<String> bccList = emailService.getEmails(EmailDlName.APPOINTMENT_NOTIFICATION);
    Set<String> defaultCcList = emailService.getEmails(EmailDlName.APPOINTMENT_NOTIFICATION_CC);
    updateStakeHolders(appointmentNotification, loc, defaultCcList, bccList);
    sendNotifications(
        appointmentNotification,
        ZoomPropertyName.APPOINTMENT_WRONG_UNDELIVERED_MARKED_EMAIL,
        ZoomPropertyName.APPOINTMENT_WRONG_UNDELIVERED_MARKED_SUBJECT);
  }

  private void sendNotifications(
      AppointmentNotification notification,
      ZoomPropertyName emailPropertyName,
      ZoomPropertyName subjectPropertyName) {
    String templateString = zoomPropertyService.getString(emailPropertyName);
    Boolean isEmailEnabled =
        zoomPropertyService.getBoolean(ZoomPropertyName.APPOINTMENT_NOTIFICATION_ENABLED, false);
    String subjectTemplate =
        zoomPropertyService.getString(subjectPropertyName); // get from zoom property
    if (templateString != null && isEmailEnabled && subjectTemplate != null) {
      String body = designEmailTemplate(notification, templateString);
      String subject = designEmailTemplate(notification, subjectTemplate);
      emailService.sendEmail(
          EmailConstant.APPOINTMENT_EMAIL_ID,
          notification.getEmailIdList(),
          notification.getCcList(),
          notification.getBccList(),
          subject,
          body,
          null);
    }
  }

  private void sendNotificationList(
      List<AppointmentNotification> notificationList,
      ZoomPropertyName emailPropertyName,
      ZoomPropertyName subjectPropertyName) {
    if (CollectionUtils.isEmpty(notificationList)) {
      return;
    }
    String templateString = zoomPropertyService.getString(emailPropertyName);
    Boolean isEmailEnabled =
        zoomPropertyService.getBoolean(ZoomPropertyName.APPOINTMENT_NOTIFICATION_ENABLED, false);
    String subjectTemplate =
        zoomPropertyService.getString(subjectPropertyName); // get from zoom property
    if (templateString != null && isEmailEnabled) {
      String body = designEmailTemplate(notificationList.get(0), templateString);
      String subject = designEmailTemplate(notificationList.get(0), subjectTemplate);
      try (SXSSFWorkbook wb = new SXSSFWorkbook(100)) {
        Sheet sheet = wb.createSheet("Consignments");
        Row headerRow = sheet.createRow(0);
        Cell headerCell = headerRow.createCell(0);
        headerCell.setCellValue("Cnote");
        int[] counter = {1};
        notificationList.forEach(
            p -> {
              Row row = sheet.createRow(counter[0]++);
              Cell cell = row.createCell(0);
              cell.setCellValue(p.getCnote());
            });
        File file = new File("Missed_Appointment_Delivery.xlsx");
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        wb.write(output);
        output.close();
        wb.dispose();
        byte[] contents = output.toByteArray();
        FileUtils.writeByteArrayToFile(file, contents);
        emailService.sendEmail(
            EmailConstant.APPOINTMENT_EMAIL_ID,
            notificationList.get(0).getEmailIdList(),
            notificationList.get(0).getCcList(),
            notificationList.get(0).getBccList(),
            subject,
            body,
            file);
      } catch (IOException e) {
        throw new ZoomException("IOException while writing to file");
      }
    }
  }

  private void updateStakeHolders(
      AppointmentNotification appointmentNotification,
      Location loc,
      Set<String> defaultCcList,
      Set<String> bccList) {
    if (appointmentNotification.getEmailIdList().isEmpty()) {
      appointmentNotification.getEmailIdList().addAll(getCcList(loc));
      appointmentNotification.getEmailIdList().addAll(defaultCcList);
    } else {
      appointmentNotification.getCcList().addAll(getCcList(loc));
      appointmentNotification.getCcList().addAll(defaultCcList);
    }
    appointmentNotification.getBccList().addAll(bccList);
  }

  private UserBasicDTO getUserDto(User user) {
    return new UserBasicDTO(
        user.getId(), user.getName(), user.getEmail(), user.getOrganizationId());
  }

  private LocationBasicDTO getLocationDto(Location location) {
    return new LocationBasicDTO(
        location.getId(),
        location.getName(),
        location.getCode(),
        location.getLocationType().name());
  }

  private String designEmailTemplate(
      AppointmentNotification appointmentNotification, String template) {
    Map<String, String> valuesMap = new HashMap<>();
    valuesMap.put("cnote", appointmentNotification.getCnote());
    valuesMap.put(
        "user",
        appointmentNotification.getResponsiblePerson() == null
            ? "-"
            : appointmentNotification.getResponsiblePerson().getName());
    valuesMap.put(
        "locationCode",
        appointmentNotification.getResponsibleLocation() == null
            ? "-"
            : appointmentNotification.getResponsibleLocation().getCode());
    StrSubstitutor sub = new StrSubstitutor(valuesMap);
    return sub.replace(template);
  }

  private Set<String> getCcList(Location loc) {
    Set<String> ccList = new HashSet<>();
    List<Long> locIds =
        locationService
            .getAllClusterSiblingsOfLocation(loc.getCode())
            .stream()
            .map(Location::getId)
            .collect(Collectors.toList());

    Location pc = locationService.getPcOrReportingPc(loc);

    ccList.addAll(
        zoomUserMasterService
            .getActiveZoomUsersByLocationInAndZoomUserType(
                locIds, ZoomUserType.ZOOM_CLM.name(), ZoomUserType.ZOOM_TECH_SUPPORT.name())
            .stream()
            .map(ZoomUser::getEmail)
            .collect(Collectors.toList()));
    ccList.addAll(
        zoomUserMasterService
            .getActiveZoomUsersByLocationInAndZoomUserType(
                locIds, ZoomUserType.ZOOM_RM.name(), ZoomUserType.ZOOM_TECH_SUPPORT.name())
            .stream()
            .map(ZoomUser::getEmail)
            .collect(Collectors.toList()));
    ccList.addAll(
        zoomUserMasterService
            .getActiveZoomUsersByLocationAndZoomUserType(
                pc.getId(), ZoomUserType.ZOOM_PCM.name(), ZoomUserType.ZOOM_TECH_SUPPORT.name())
            .stream()
            .map(ZoomUser::getEmail)
            .collect(Collectors.toList()));
    return ccList;
  }
}
