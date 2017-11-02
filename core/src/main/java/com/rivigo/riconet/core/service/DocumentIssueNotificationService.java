package com.rivigo.riconet.core.service;

import com.rivigo.riconet.core.constants.ConsignmentConstant;
import com.rivigo.riconet.core.enums.ZoomPropertyName;
import com.rivigo.riconet.core.utils.TimeUtilsZoom;
import com.rivigo.zoom.common.enums.ConsignmentLocationStatus;
import com.rivigo.zoom.common.enums.ConsignmentStatus;
import com.rivigo.zoom.common.enums.EmailDlName;
import com.rivigo.zoom.common.enums.LocationTypeV2;
import com.rivigo.zoom.common.enums.OperationalStatus;
import com.rivigo.zoom.common.enums.StockAccumulatorRole;
import com.rivigo.zoom.common.enums.ZoomUserType;
import com.rivigo.zoom.common.model.ConsignmentReadOnly;
import com.rivigo.zoom.common.model.ConsignmentSchedule;
import com.rivigo.zoom.common.model.Organization;
import com.rivigo.zoom.common.model.StockAccumulator;
import com.rivigo.zoom.common.model.TransportationPartnerMapping;
import com.rivigo.zoom.common.model.User;
import com.rivigo.zoom.common.model.ZoomUser;
import com.rivigo.zoom.common.model.mongo.DocumentIssueNotification;
import com.rivigo.zoom.common.model.neo4j.Location;
import com.rivigo.zoom.common.repository.mongo.DocumentIssueNotificationRepository;
import com.rivigo.zoom.exceptions.ZoomException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.text.StrSubstitutor;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.AbstractEnvironment;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class DocumentIssueNotificationService {

    @Autowired
    private ConsignmentService consignmentService;

    @Autowired
    private ZoomUserMasterService zoomUserMasterService;

    @Autowired
    private OrganizationService organizationService;

    @Autowired
    private UserMasterService userMasterService;

    @Autowired
    private LocationService locationServiceV2;

    @Autowired
    private EmailService emailService;

    @Autowired
    private ZoomPropertyService zoomPropertyService;

    @Autowired
    private TransportationPartnerMappingService transportationPartnerMappingService;

    @Autowired
    private ConsignmentScheduleService consignmentScheduleService;

    @Autowired
    private StockAccumulatorService stockAccumulatorService;

    @Autowired
    private ConsignmentReadOnlyService consignmentReadOnlyService;

    @Autowired
    private ClientMasterService clientMasterService;

    @Autowired
    private DocumentIssueNotificationRepository documentIssueNotificationRepository;


    public DocumentIssueNotification createNotificationData(Long consignmentId, Long userId, String subReason, ConsignmentStatus status) {
        ConsignmentReadOnly cn=consignmentReadOnlyService.findByConsignmentById(consignmentId);
        if(cn== null){
            throw new ZoomException("No consignment with this id exists");
        }
        User user=userMasterService.getById(userId);
        if(user == null){
            throw new ZoomException("No user with this userId exists");
        }
        ZoomUser zoomUser=null;
        if(userMasterService.canAdaptTo(user,ZoomUser.class)){
            zoomUser=userMasterService.adaptUserTo(user,ZoomUser.class);
        }
        DocumentIssueNotification notification=new DocumentIssueNotification();
        updateCnoteMetadata(notification,cn,subReason);
        updateResponsiblePersonAndLocation(notification,user,zoomUser==null?cn.getLocationId():zoomUser.getLocationId(),cn,status);
        updateStakeHolders(notification);
        documentIssueNotificationRepository.save(notification);
        return notification;
    }

    private DocumentIssueNotification.NotificationUserDTO getUserDTO(User user) {
        User responsibleUser=user;
        DocumentIssueNotification.NotificationUserDTO userDTO = new DocumentIssueNotification.NotificationUserDTO();
        boolean isBpUser = false;
        Organization organization=null;
        if(!responsibleUser.getOrganizationId().equals(ConsignmentConstant.RIVIGO_ORGANIZATION_ID)){
            organization=organizationService.getById(responsibleUser.getOrganizationId());
        }else if(userMasterService.canAdaptTo(responsibleUser, StockAccumulator.class)) {
            StockAccumulator stockAccumulator = userMasterService.adaptUserTo(responsibleUser,StockAccumulator.class);
            List<StockAccumulator> accumulatorList = stockAccumulatorService.getByStockAccumulatorRoleAndAccumulationPartnerIdAndStatus(
                    StockAccumulatorRole.STOCK_ACCUMULATOR_ADMIN, stockAccumulator.getAccumulationPartnerId().getId(), OperationalStatus.ACTIVE);
            if(!CollectionUtils.isEmpty(accumulatorList)) {
                StockAccumulator accumulator = accumulatorList.get(0);
                responsibleUser = accumulator.getUser();
                isBpUser = true;
            }
        }
        if(organization == null){
            userDTO.setEmail(responsibleUser.getEmail());
        }else{
            userDTO.setEmail(organization.getEmail());
        }
        userDTO.setId(responsibleUser.getId());
        userDTO.setEmail(responsibleUser.getEmail());
        userDTO.setName(responsibleUser.getName());
        userDTO.setOrgId(responsibleUser.getOrganizationId());
        if(responsibleUser.getOrganizationId()!=1) {
            userDTO.setType(organizationService.getById(responsibleUser.getOrganizationId()).getType().name());
        }else if(isBpUser) {
            userDTO.setType(StockAccumulatorRole.STOCK_ACCUMULATOR_ADMIN.name());
        } else {
            ZoomUser zoomUser = zoomUserMasterService.getZoomUser(responsibleUser.getEmail());
            if (zoomUser != null) {
                userDTO.setType(zoomUser.getZoomUserType());
            }
        }
        return userDTO;
    }

    private DocumentIssueNotification.NotificationLocationDTO getLocationDTO(Location location) {
        DocumentIssueNotification.NotificationLocationDTO locationDTO = new DocumentIssueNotification.NotificationLocationDTO();
        locationDTO.setId(location.getId());
        locationDTO.setName(location.getName());
        locationDTO.setCode(location.getCode());
        locationDTO.setType(location.getLocationType().name());
        return  locationDTO;
    }

    private Set<String> getCcList(Long locationId){
        Set<String> ccList=new HashSet<>();
        Location loc=locationServiceV2.getLocationById(locationId);
        List<Long> locIds=locationServiceV2.getAllClusterSiblingsOfLocation(loc.getCode())
                .stream().map(Location::getId).collect(Collectors.toList());

        Location pc=locationServiceV2.getPcOrReportingPc(loc);

        ccList.addAll(zoomUserMasterService.getActiveZoomUsersByLocationInAndZoomUserType(locIds,
                "ZOOM_CLM", ZoomUserType.ZOOM_TECH_SUPPORT.name()).stream().map(ZoomUser::getEmail).collect(Collectors.toList()));
        ccList.addAll(zoomUserMasterService.getActiveZoomUsersByLocationAndZoomUserType(locationId,
                "ZOOM_BO_PCE",ZoomUserType.ZOOM_TECH_SUPPORT.name()).stream().map(ZoomUser::getEmail).collect(Collectors.toList()));
        ccList.addAll(zoomUserMasterService.getActiveZoomUsersByLocationAndZoomUserType(pc.getId(),
                "ZOOM_PCM",ZoomUserType.ZOOM_TECH_SUPPORT.name()).stream().map(ZoomUser::getEmail).collect(Collectors.toList()));
        return ccList;
    }

    private void  filterEmails(DocumentIssueNotification dto,Set<String> bccList, boolean isTesting){
        dto.getBccList().addAll(bccList);
        if(!isTesting && "production".equalsIgnoreCase(System.getProperty(AbstractEnvironment.ACTIVE_PROFILES_PROPERTY_NAME))) {
            return;
        }
        List<String> dummyEmailList = new ArrayList<>();
        dto.getEmailIdList().forEach(email->
            dummyEmailList.add(email.split("@")[0]+"@rivigodummy.com"));
        dto.getEmailIdList().clear();
        dto.getEmailIdList().addAll(dummyEmailList);

        List<String> dummyCcList = new ArrayList<>();
        dto.getCcList().forEach(email->
            dummyCcList.add(email.split("@")[0]+"@rivigodummy.com"));
        dto.getCcList().clear();
        dto.getCcList().addAll(dummyCcList);

    }

    private void updateStakeHolders(DocumentIssueNotification notification) {
        Set<String> bccList = emailService.getEmails(EmailDlName.DOCUMENT_ISSUE_NOTIFICATION);

        Set<String> defaultCcList = emailService.getEmails(EmailDlName.DOCUMENT_ISSUE_NOTIFICATION_CC);

        boolean isTesting = zoomPropertyService.getBoolean(ZoomPropertyName.DOCUMENT_ISSUE_EMAIL_TESTING, true);

        notification.getCcList().addAll(getCcList(notification.getReporterLocation().getId()));
        notification.getCcList().addAll(defaultCcList);
        notification.getEmailIdList().add(notification.getReporter().getEmail());
        if(notification.getReportee() != null){
            notification.getEmailIdList().add(notification.getReportee().getEmail());
            notification.getCcList().addAll(getCcList(notification.getReporteeLocation().getId()));
        }
        filterEmails(notification,bccList, isTesting);
    }

    private void updateResponsiblePersonAndLocation(DocumentIssueNotification notification, User user, Long locationId,
                                                    ConsignmentReadOnly consignment, ConsignmentStatus status) {
        //fill scenario
        Integer bufferMinutes= zoomPropertyService.getInteger(ZoomPropertyName.DOCUMENT_ISSUE_BUFFER_MINUTES,120);
        User reporter=null;
        User reportee=null;
        Long reporterLocationId=null;
        Long reporteeLocationId=null;
        if(ConsignmentStatus.UNDELIVERED.equals(status)){
            notification.setScenario("Missed at the time of delivery");
            TransportationPartnerMapping tpm=transportationPartnerMappingService.getByDRSId(consignment.getDrsId());
            reporter=userMasterService.getById(tpm.getUserId());
            reporterLocationId=consignment.getLocationId();
        }else{
            List<ConsignmentSchedule> consignmentSchedules = consignmentScheduleService.getActivePlan(notification.getConsignmentId());
            ConsignmentSchedule schedule=getCurrentSchedule(consignmentSchedules);
            if(schedule==null){
                throw new ZoomException("This consignment with id "+consignment.getId()+" is not present at any location");
            }
            if(DateTime.now().getMillis() > schedule.getArrivalTime()+bufferMinutes* TimeUtilsZoom.MILLIS_IN_MINUTE){
                notification.setScenario("Within PC");
                reporter=user;
                reporterLocationId=locationId;
            }else{
                ConsignmentSchedule previousSchedule=getPreviousSchedule(consignmentSchedules);
                if(previousSchedule== null || previousSchedule.getLocationType().equals(LocationTypeV2.PINCODE)){
                    notification.setScenario("Missed during pickup");
                    reporter=userMasterService.getById(consignment.getCreatedById());
                    reporterLocationId=locationId;
                }else{
                    notification.setScenario("Scan out but not scan in");
                    reportee=userMasterService.getById(previousSchedule.getLoadedById());
                    reporteeLocationId=previousSchedule.getLocationId();
                    reporter=user;
                    reporterLocationId=locationId;
                }
            }
        }
        if(!notification.getSubReason().contains("missing")){
            reporter=user;
            reporterLocationId=locationId;
            reportee=null;
            reporteeLocationId=null;
        }

        notification.setReporter(getUserDTO(reporter));
        notification.setReporterLocation(getLocationDTO(locationServiceV2.getLocationById(reporterLocationId)));
        if(reportee != null){
            notification.setReportee(getUserDTO(reportee));
            notification.setReporteeLocation(getLocationDTO(locationServiceV2.getLocationById(reporteeLocationId)));
        }

    }

    public ConsignmentSchedule getPreviousSchedule( List<ConsignmentSchedule> consignmentSchedules) {
        Collections.sort(consignmentSchedules);
        final ConsignmentSchedule[] lastSchedule = new ConsignmentSchedule[1];
        consignmentSchedules.forEach(consignmentSchedule -> {
            if(consignmentSchedule.getPlanStatus()== ConsignmentLocationStatus.LEFT)
                lastSchedule[0] = consignmentSchedule;
        });

        return lastSchedule[0];
    }

    public ConsignmentSchedule getCurrentSchedule( List<ConsignmentSchedule> consignmentSchedules) {
        Collections.sort(consignmentSchedules);
        final ConsignmentSchedule[] lastSchedule = new ConsignmentSchedule[1];
        consignmentSchedules.forEach(consignmentSchedule -> {
            if(consignmentSchedule.getPlanStatus()== ConsignmentLocationStatus.REACHED)
                lastSchedule[0] = consignmentSchedule;
        });

        return lastSchedule[0];
    }


    private void updateCnoteMetadata(DocumentIssueNotification notification,ConsignmentReadOnly consignment, String subReason) {
        notification.setCnote(consignment.getCnote());
        notification.setConsignmentId(consignment.getId());
        notification.setSubReason(subReason);
        notification.setClientName(clientMasterService.getClientById(consignment.getClientId()).getName());
    }

    public void sendNotifications(DocumentIssueNotification notification) {
        String templateString= zoomPropertyService.getString(ZoomPropertyName.DOCUMENT_ISSUE_NOTIFICATION_TEMPLATE);
        Boolean isEmailEnabled = zoomPropertyService.getBoolean(ZoomPropertyName.DOCUMENT_ISSUE_EMAIL_ENABLED, false);
        String subjectTemplate = zoomPropertyService.getString(ZoomPropertyName.DOCUMENT_ISSUE_NOTIFICATION_SUBJECT);//get from zoom property
        if(templateString != null && isEmailEnabled){
            String body = designEmailTemplate(notification,templateString);
            String subject = designEmailTemplate(notification,subjectTemplate);
            emailService.sendDocumentIssueEmail(notification.getEmailIdList(), notification.getCcList(), notification.getBccList(), subject, body, null);
        }
    }

    private String designEmailTemplate(DocumentIssueNotification notification, String templateString) {
        Map<String, String> valuesMap = new HashMap<>();
        valuesMap.put("cnote", notification.getCnote());
        valuesMap.put("client_name", notification.getClientName());
        valuesMap.put("last_loading_ou", notification.getReporteeLocation()==null ?"-":notification.getReporteeLocation().getCode());
        valuesMap.put("last_loading_person", notification.getReportee() == null? "-":notification.getReportee().getName());
        valuesMap.put("reporting_ou", notification.getReporterLocation().getCode());
        valuesMap.put("reporting_person", notification.getReporter().getName());
        valuesMap.put("scenario",notification.getScenario());
        valuesMap.put("subReason", notification.getSubReason());
        if(notification.getReportee() != null){

            valuesMap.put("responsible_ou", notification.getReporteeLocation().getCode()+
                    " - "+ notification.getReporterLocation().getCode());
            valuesMap.put("responsible_person", notification.getReportee().getName()+
                    " - "+ notification.getReporter().getName());
            valuesMap.put("dear", notification.getReportee().getName()+
                    " / "+ notification.getReporter().getName());
        }else{
            valuesMap.put("responsible_ou", notification.getReporterLocation().getCode());
            valuesMap.put("responsible_person", notification.getReporter().getName());
            valuesMap.put("dear", notification.getReporter().getName());
        }

        StrSubstitutor sub=new StrSubstitutor(valuesMap);
        return sub.replace(templateString);
    }
}
