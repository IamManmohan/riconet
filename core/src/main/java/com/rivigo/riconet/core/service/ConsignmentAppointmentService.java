package com.rivigo.riconet.core.service;

import com.rivigo.common.report.impl.GenericReportGeneratorImpl;
import com.rivigo.riconet.core.enums.ZoomPropertyName;
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
import com.rivigo.zoom.common.model.mongo.AppointmentNotification;
import com.rivigo.zoom.common.model.neo4j.Location;
import com.rivigo.zoom.common.repository.mysql.ConsignmentAppointmentRepository;
import com.rivigo.zoom.exceptions.ZoomException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;


@Slf4j
@Service
public class ConsignmentAppointmentService {

    @Autowired
    private ConsignmentAppointmentRepository consignmentAppointmentRepository;

    @Autowired
    private ConsignmentService consignmentService;

    @Autowired
    private LocationService locationService;

    @Autowired
    private UserMasterService userMasterService;

    @Autowired
    private ZoomUserMasterService zoomUserMasterService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private ZoomPropertyService zoomPropertyService;

    @Autowired
    private ConsignmentScheduleService consignmentScheduleService;

    private static final String IST_TIME_ZONE_ID="Asia/Kolkata";

    public void processAppointmentNotification(AppointmentNotificationDTO dto){
        if(dto==null){
            return;
        }
        List<AppointmentNotification> notificationList;
        Map<Long,List<AppointmentNotification>> notificationMap;
        List<ConsignmentStatus> statusList=new ArrayList(Arrays.asList(ConsignmentStatus.DELIVERED,ConsignmentStatus.DELIVERED_POD_PENDING));
        DateTime now=DateTime.now();
        switch (dto.getNotificationType()){
            case APPOINTMENT_DELIVERED_LATE:
                processLateDelivery(dto);
                return;
            case APPOINTMENT_MISSED:
                notificationList= processAppointmentMissed(new DateTime(dto.getLastExecutionTime()),now,statusList);
                log.info(notificationList.stream().map(p->p.getCnote()).collect(Collectors.joining(", ")));
                notificationList.forEach(notification ->
                        sendNotifications(notification,ZoomPropertyName.APPOINTMENT_MISSED_EMAIL,ZoomPropertyName.APPOINTMENT_MISSED_SUBJECT)
                );
                return;
            case APPOINTMENT_WRONG_UNDELIVERED_MARKED:
                processFakeUndeliveryReason(dto);
                return;
            case APPOINTMENT_MISSED_SUMMARY:
                notificationList= processAppointmentMissed(new DateTime(dto.getLastExecutionTime()),now,statusList);
                log.info(notificationList.stream().map(p->p.getCnote()).collect(Collectors.joining(", ")));
                notificationMap=notificationList.stream()
                        .collect(Collectors.groupingBy(notification-> notification.getResponsibleLocation().getId()));
                notificationMap.keySet().forEach(list->
                    sendNotificationList(notificationMap.get(list),ZoomPropertyName.APPOINTMENT_MISSED_SUMMARY_EMAIL,
                            ZoomPropertyName.APPOINTMENT_MISSED_SUMMARY_SUBJECT)
                );
                return;
            case APPOINTMENT_NOT_OFD:
                statusList.add(ConsignmentStatus.OUT_FOR_DELIVERY);
                notificationList= processAppointmentMissed(now,now.withZone(DateTimeZone.forID(IST_TIME_ZONE_ID)).plusDays(1).withMillisOfDay(0),
                        statusList);
                log.info(notificationList.stream().map(p->p.getCnote()).collect(Collectors.joining(", ")));
                notificationMap=notificationList.stream()
                        .collect(Collectors.groupingBy(notification-> notification.getResponsibleLocation().getId()));
                notificationMap.keySet().forEach(list->
                    sendNotificationList(notificationMap.get(list),ZoomPropertyName.APPOINTMENT_NOT_OFD_EMAIL,
                            ZoomPropertyName.APPOINTMENT_NOT_OFD_SUBJECT)
                );
                return;
            default:
                break;
        }
    }

    private List<AppointmentNotification> processAppointmentMissed(DateTime start, DateTime end, List<ConsignmentStatus> statusList){
        List<ConsignmentAppointmentRecord> consignmentAppointmentRecordList=consignmentAppointmentRepository.
                findByIsActiveAndAppointmentTimeBetween(Boolean.TRUE, start, end);
        log.info(start.getMillis()+"------"+end.getMillis());
        log.info(consignmentAppointmentRecordList.stream().map(p->p.getConsignmentId().toString()).collect(Collectors.joining(", ")));
        List<Long> consignmentIdList=consignmentAppointmentRecordList.stream()
                .map(ConsignmentAppointmentRecord::getConsignmentId)
                .collect(Collectors.toList());
        List<Consignment> consignments = consignmentService.findByIdInAndStatusNotInAndDeliveryHandoverIsNull(consignmentIdList,
                statusList);
        log.info(consignments.stream().map(p->p.getCnote()).collect(Collectors.joining(", ")));

        Map<Long,List<ConsignmentSchedule>> cnToScheduleMap = consignmentScheduleService.getActivePlansMapByIds(consignmentIdList);

        return consignments.stream()
        .map(consignment->
            processAppointmentMissedConsignment(consignment,cnToScheduleMap.get(consignment.getId()))
        ).collect(Collectors.toList());

    }

    private AppointmentNotification processAppointmentMissedConsignment(Consignment consignment, List<ConsignmentSchedule> consignmentScheduleList){
        AppointmentNotification appointmentNotification=new AppointmentNotification();
        appointmentNotification.setConsignmentId(consignment.getId());
        Location loc=locationService.getLocationById(getCurrentSchedule(consignmentScheduleList).getLocationId());
        appointmentNotification.setResponsibleLocation(getLocationDto(loc));
        appointmentNotification.setCnote(consignment.getCnote());
        log.info("888888888888888");
        updateStakeHolders(appointmentNotification);
        return appointmentNotification;

    }

    private ConsignmentSchedule getCurrentSchedule(List<ConsignmentSchedule> consignmentScheduleList) {
        Collections.sort(consignmentScheduleList);
        Optional<ConsignmentSchedule> present=consignmentScheduleList.stream().filter(consignmentSchedule ->
            consignmentSchedule.getPlanStatus() != ConsignmentLocationStatus.LEFT
        ).findFirst();

        if(present.isPresent()){
            return present.get();
        }
        throw  new ZoomException("Error in consignment schedule ");
    }

    private void processLateDelivery(AppointmentNotificationDTO dto){
        ConsignmentHistory cnHistory=consignmentService.getLastScanByCnId(dto.getConsignmentId(),
                Arrays.asList(ConsignmentStatus.OUT_FOR_DELIVERY.toString()));
        AppointmentNotification appointmentNotification=new AppointmentNotification();
        appointmentNotification.setConsignmentId(cnHistory.getConsignmentId());
        Location loc=locationService.getLocationById(cnHistory.getLocationId());
        appointmentNotification.setResponsibleLocation(getLocationDto(loc));
        appointmentNotification.setCnote(consignmentService.getCnoteByIdAndIsActive(cnHistory.getConsignmentId()));
        User user=userMasterService.getById(cnHistory.getCreatedById());
        appointmentNotification.setResponsiblePerson(getUserDto(user));
        appointmentNotification.getEmailIdList().add(user.getEmail());
        updateStakeHolders(appointmentNotification);
        DateTime deliveryTime= (new DateTime(dto.getDeliveryTime())).withZone(DateTimeZone.forID(IST_TIME_ZONE_ID));
        DateTime appointmentTime= (new DateTime(dto.getAppoitnmentTime())).withZone(DateTimeZone.forID(IST_TIME_ZONE_ID));
        if(Days.daysBetween(deliveryTime.toLocalDate(), appointmentTime.toLocalDate()).getDays()==0){
            sendNotifications(appointmentNotification,ZoomPropertyName.APPOINTMENT_DELIVERED_LATE_SAME_DAY_EMAIL,
                    ZoomPropertyName.APPOINTMENT_DELIVERED_LATE_SAME_DAY_SUBJECT);
        }else {
            sendNotifications(appointmentNotification,ZoomPropertyName.APPOINTMENT_DELIVERED_LATE_DIFFERENT_DAY_EMAIL,
                    ZoomPropertyName.APPOINTMENT_DELIVERED_LATE_DIFFERENT_DAY_SUBJECT);
        }
    }

    private void processFakeUndeliveryReason(AppointmentNotificationDTO dto){
        ConsignmentHistory cnHistory=consignmentService.getLastScanByCnId(dto.getConsignmentId(),
                Arrays.asList(ConsignmentStatus.UNDELIVERED.toString()));
        AppointmentNotification appointmentNotification=new AppointmentNotification();
        appointmentNotification.setConsignmentId(cnHistory.getConsignmentId());
        Location loc=locationService.getLocationById(cnHistory.getLocationId());
        appointmentNotification.setResponsibleLocation(getLocationDto(loc));
        appointmentNotification.setCnote(consignmentService.getCnoteByIdAndIsActive(cnHistory.getConsignmentId()));
        User user=userMasterService.getById(cnHistory.getCreatedById());
        appointmentNotification.setResponsiblePerson(getUserDto(user));
        appointmentNotification.getEmailIdList().add(user.getEmail());
        updateStakeHolders(appointmentNotification);
        sendNotifications(appointmentNotification,ZoomPropertyName.APPOINTMENT_WRONG_UNDELIVERED_MARKED_EMAIL,
                    ZoomPropertyName.APPOINTMENT_WRONG_UNDELIVERED_MARKED_SUBJECT);
    }

    private void sendNotifications(AppointmentNotification notification, ZoomPropertyName emailPropertyName, ZoomPropertyName subjectPropertyName) {
        String templateString= zoomPropertyService.getString(emailPropertyName);
        Boolean isEmailEnabled = zoomPropertyService.getBoolean(ZoomPropertyName.APPOINTMENT_NOTIFICATION_ENABLED, false);
        String subjectTemplate = zoomPropertyService.getString(subjectPropertyName);//get from zoom property
        log.info(templateString);
        if(templateString != null && isEmailEnabled){
            String body = designEmailTemplate(notification,templateString);
            String subject = designEmailTemplate(notification,subjectTemplate);
            emailService.sendDocumentIssueEmail(notification.getEmailIdList(), notification.getCcList(), notification.getBccList(), subject, body, null);
        }
    }



    private void sendNotificationList(List<AppointmentNotification> notificationList, ZoomPropertyName emailPropertyName, ZoomPropertyName subjectPropertyName){
        String body= zoomPropertyService.getString(emailPropertyName);
        Boolean isEmailEnabled = zoomPropertyService.getBoolean(ZoomPropertyName.APPOINTMENT_NOTIFICATION_ENABLED, false);
        String subject = zoomPropertyService.getString(subjectPropertyName);//get from zoom property
        log.info(body);
        if(body != null && isEmailEnabled){
            SXSSFWorkbook wb = new SXSSFWorkbook(100);
            Sheet sheet=wb.createSheet("Consignments");
            Row headerRow = sheet.createRow(0);
            Cell headerCell = headerRow.createCell(0);
            headerCell.setCellValue("Cnote");
            int  counter[]={1};
            notificationList.forEach(p ->{
                Row row = sheet.createRow(counter[0]++);
                Cell cell = row.createCell(0);
                cell.setCellValue(p.getCnote());
            });
            File file = new File("Missed_Appointment_Delivery.xlsx");
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            try{
                wb.write(output);
                output.close();
                wb.dispose();
                wb.close();
                byte[] contents = output.toByteArray();
                FileUtils.writeByteArrayToFile(file, contents);
                emailService.sendDocumentIssueEmail(notificationList.get(0).getEmailIdList(),
                        notificationList.get(0).getCcList(), notificationList.get(0).getBccList(), subject, body, file);
            }catch (IOException e){
                throw new ZoomException("IOException while writing to file");
            }
        }
    }

    private void updateStakeHolders(AppointmentNotification appointmentNotification){
        Set<String> bccList = emailService.getEmails(EmailDlName.APPOINTMENT_NOTIFICATION);
        Set<String> defaultCcList = emailService.getEmails(EmailDlName.APPOINTMENT_NOTIFICATION_CC);
        boolean isTesting = zoomPropertyService.getBoolean(ZoomPropertyName.APPOINTMENT_NOTIFICATION_TESTING, true);
        if(appointmentNotification.getEmailIdList().isEmpty()){
            appointmentNotification.getEmailIdList().addAll(getCcList(appointmentNotification.getResponsibleLocation().getId()));
            appointmentNotification.getEmailIdList().addAll(defaultCcList);
        }else{
            appointmentNotification.getCcList().addAll(getCcList(appointmentNotification.getResponsibleLocation().getId()));
            appointmentNotification.getCcList().addAll(defaultCcList);
        }
        emailService.filterEmails(appointmentNotification,bccList,isTesting);
    }

    private UserBasicDTO getUserDto(User user){
        return new UserBasicDTO(user.getId(),user.getName(),user.getEmail(),user.getOrganizationId());
    }

    private LocationBasicDTO getLocationDto(Location location){
        return new LocationBasicDTO(location.getId(),location.getName(),location.getCode(),
                location.getLocationType().name());
    }

    private String designEmailTemplate(AppointmentNotification appointmentNotification, String template) {
        Map<String, String> valuesMap = new HashMap<>();
        valuesMap.put("cnote",appointmentNotification.getCnote());
        valuesMap.put("user",appointmentNotification.getResponsiblePerson()==null?"-":appointmentNotification.getResponsiblePerson().getName());
        valuesMap.put("locationCode",appointmentNotification.getResponsibleLocation()==null?"-":appointmentNotification.getResponsibleLocation().getCode());
        StrSubstitutor sub=new StrSubstitutor(valuesMap);
        return sub.replace(template);
    }

    private Set<String> getCcList(Long locationId){
        Set<String> ccList=new HashSet<>();
        Location loc=locationService.getLocationById(locationId);
        List<Long> locIds=locationService.getAllClusterSiblingsOfLocation(loc.getCode())
                .stream().map(Location::getId).collect(Collectors.toList());

        Location pc=locationService.getPcOrReportingPc(loc);

        ccList.addAll(zoomUserMasterService.getActiveZoomUsersByLocationInAndZoomUserType(locIds,
                "ZOOM_CLM", ZoomUserType.ZOOM_TECH_SUPPORT.name()).stream().map(ZoomUser::getEmail).collect(Collectors.toList()));
        ccList.addAll(zoomUserMasterService.getActiveZoomUsersByLocationInAndZoomUserType(locIds,
                "ZOOM_RM", ZoomUserType.ZOOM_TECH_SUPPORT.name()).stream().map(ZoomUser::getEmail).collect(Collectors.toList()));
        ccList.addAll(zoomUserMasterService.getActiveZoomUsersByLocationAndZoomUserType(pc.getId(),
                "ZOOM_PCM",ZoomUserType.ZOOM_TECH_SUPPORT.name()).stream().map(ZoomUser::getEmail).collect(Collectors.toList()));
        return ccList;
    }
}
