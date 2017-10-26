package service;

import com.rivigo.zoom.common.dto.DEPSNotificationContext;
import com.rivigo.zoom.common.dto.DEPSNotificationDTO;
import com.rivigo.zoom.common.enums.ConsignmentLocationStatus;
import com.rivigo.zoom.common.enums.DEPSLocationType;
import com.rivigo.zoom.common.enums.DEPSScenario;
import com.rivigo.zoom.common.enums.DEPSType;
import com.rivigo.zoom.common.enums.EmailDlName;
import com.rivigo.zoom.common.enums.PartnerType;
import com.rivigo.zoom.common.enums.StockAccumulatorRole;
import com.rivigo.zoom.common.enums.ZoomTripType;
import com.rivigo.zoom.common.model.BusinessPartner;
import com.rivigo.zoom.common.model.Consignment;
import com.rivigo.zoom.common.model.ConsignmentHistory;
import com.rivigo.zoom.common.model.ConsignmentSchedule;
import com.rivigo.zoom.common.model.FeederVendor;
import com.rivigo.zoom.common.model.Organization;
import com.rivigo.zoom.common.model.PickupRunSheet;
import com.rivigo.zoom.common.model.StockAccumulator;
import com.rivigo.zoom.common.model.TransportationPartnerMapping;
import com.rivigo.zoom.common.model.Trip;
import com.rivigo.zoom.common.model.User;
import com.rivigo.zoom.common.model.ZoomUser;
import com.rivigo.zoom.common.model.mongo.DEPSNotification;
import com.rivigo.zoom.common.model.neo4j.Location;
import com.rivigo.zoom.common.repository.mongo.DEPSNotificationRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.text.StrSubstitutor;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.rivigo.zoom.common.enums.ConsignmentStatus.DELIVERY_LOADED;
import static com.rivigo.zoom.common.enums.ConsignmentStatus.EXCESS;
import static com.rivigo.zoom.common.enums.ConsignmentStatus.LOADED;
import static com.rivigo.zoom.common.enums.ConsignmentStatus.RECEIVED_AT_OU;
import static com.rivigo.zoom.common.enums.ConsignmentStatus.STOCK_CHECK_DONE;
import static com.rivigo.zoom.common.enums.ZoomTripType.BF;

@Slf4j
@Service
public class DEPSRecordService {

    public static final String SECONDARY_CNOTE_SEPARATOR="-";

    public static final Long RIVIGO_ORGANIZATION_ID=1l;

    @Autowired
    TripService tripService;

    @Autowired
    PRSService prsService;

    @Autowired
    ConsignmentService consignmentService;

    @Autowired
    EmailService emailService;

    @Autowired
    UserMasterService userMasterService;

    @Autowired
    LocationService locationServiceV2;

    @Autowired
    ZoomUserMasterService zoomUserMasterService;

    @Autowired
    TransportationPartnerMappingService transportationPartnerMappingService;

    @Autowired
    StockAccumulatorService stockAccumulatorService;

    @Autowired
    FeederVendorService feederVendorService;

    @Autowired
    OrganizationService organizationService;

    @Autowired
    ZoomPropertyService zoomPropertyService;

    @Autowired
    DEPSNotificationRepository depsNotificationRepository;

    @Autowired
    ConsignmentScheduleService consignmentScheduleService;

    public List<DEPSNotification> createNotificationData(DEPSNotificationContext depsNotificationContext) {
        List<DEPSNotification> dtoList = new ArrayList<>();

        Set<Long> shortageConsignmentIds = depsNotificationContext.getNewDEPSRecordList().stream()
                .filter(depsRecord -> depsRecord.getDepsType()== DEPSType.SHORTAGE)
                .map(DEPSNotificationDTO::getConsignmentId).collect(Collectors.toSet());

        if(CollectionUtils.isEmpty(shortageConsignmentIds)) {
            log.info("No shortage tickets found ");
            return Collections.emptyList();
        }
        Map<Long, List<DEPSNotificationDTO>> consignmentIdToDepsRecordMap = depsNotificationContext.getNewDEPSRecordList().stream().collect(Collectors.groupingBy(DEPSNotificationDTO::getConsignmentId));
        List<Long> tripIdList = getTripIdList(depsNotificationContext, ZoomTripType.TRIP);
        Map<Long, Trip> tripIdToTripMap = tripService.getTripsMapByIdIn(tripIdList);
        List<Long> prsIdList = getTripIdList(depsNotificationContext, ZoomTripType.PRS);
        Map<Long, PickupRunSheet> prsIdToPRSMap = prsService.getPrsMapByPRSIdIn(prsIdList);

        Map<Long, ConsignmentHistory> consignmentIdToLatestHistoryMap = consignmentService.getLastScanByCnIdIn(new ArrayList<>(shortageConsignmentIds), Arrays.asList(RECEIVED_AT_OU.name(),
                STOCK_CHECK_DONE.name(), LOADED.name(), DELIVERY_LOADED.name(), EXCESS.name()));

        if(CollectionUtils.isEmpty(depsNotificationContext.getNewDEPSRecordList())){
            return dtoList;
        }

        Set<String> reportingCcList = getCcList(depsNotificationContext.getNewDEPSRecordList().get(0).getInboundLocationId());

        Set<String> bccList = emailService.getEmails(EmailDlName.DEPS_NOTIFICATION);

        shortageConsignmentIds.forEach(consignmentId -> {
            DEPSNotification dto = new DEPSNotification();
            dto.setId(consignmentId+"_"+depsNotificationContext.getNewDEPSRecordList().get(0).getTaskId()+"_"+ DateTime.now().getMillis());
            dto.setTaskId(depsNotificationContext.getNewDEPSRecordList().get(0).getTaskId());
            updateCnoteMetadata(dto, consignmentId, consignmentIdToDepsRecordMap, depsNotificationContext);
            updateResponsiblePersonAndLocation(dto, consignmentId, consignmentIdToDepsRecordMap, depsNotificationContext, tripIdToTripMap, prsIdToPRSMap, consignmentIdToLatestHistoryMap);
            updateStakeHolders(dto, consignmentId, consignmentIdToDepsRecordMap, depsNotificationContext,reportingCcList,bccList);
            updateLastScanDetails(dto, consignmentId, consignmentIdToDepsRecordMap, depsNotificationContext, consignmentIdToLatestHistoryMap);
            dtoList.add(dto);
        });
        depsNotificationRepository.save(dtoList);
        return dtoList;
    }

    private void updateLastScanDetails(DEPSNotification dto, Long consignmentId, Map<Long, List<DEPSNotificationDTO>> consignmentIdToDepsRecordMap, DEPSNotificationContext depsNotificationContext, Map<Long, ConsignmentHistory> consignmentIdToLatestHistoryMap) {
        ConsignmentHistory history = consignmentIdToLatestHistoryMap.get(consignmentId);
        if(history==null)
            return;
        addDEPSUserDTO(dto, userMasterService.getById(history.getCreatedById()), DEPSLocationType.LAST_SCAN);
        addDEPSLocationDTO(dto, locationServiceV2.getLocationById(history.getLocationId()), DEPSLocationType.LAST_SCAN);
    }

    private void addDEPSLocationDTO(DEPSNotification dto, Location location, DEPSLocationType locationType) {
        DEPSNotification.DEPSLocationDTO locationDTO = new DEPSNotification.DEPSLocationDTO();
        locationDTO.setId(location.getId());
        locationDTO.setName(location.getName());
        locationDTO.setCode(location.getCode());
        locationDTO.setType(location.getLocationType().name());
        if(locationType== DEPSLocationType.LAST_SCAN) {
            dto.setLastScannedAtLocation(locationDTO);
        } else if(locationType==DEPSLocationType.INBOUND) {
            dto.setReporterLocation(locationDTO);
        } else if(locationType==DEPSLocationType.OUTBOUND) {
            dto.setReporteeLocation(locationDTO);
        }
    }

    private Set<String> getCcList(Long locationId){
        Set<String> ccList=new HashSet<>();
        Location loc=locationServiceV2.getLocationById(locationId);
        List<Long> locIds=locationServiceV2.getAllClusterSiblingsOfLocation(loc.getCode())
                .stream().map(Location::getId).collect(Collectors.toList());

        List<Long> regionLocIds=locationServiceV2.getAllClusterSiblingsOfLocation(loc.getCode())
                .stream().map(Location::getId).collect(Collectors.toList());

        Location pc=locationServiceV2.getPcOrReportingPc(loc);
        List<ZoomUser> rmList=zoomUserMasterService.getActiveZoomUsersByLocationInAndZoomUserType(regionLocIds,
                "ZOOM_RM","ZOOM_TECH_SUPPORT");
        List<ZoomUser> clmList=zoomUserMasterService.getActiveZoomUsersByLocationInAndZoomUserType(locIds,
                "ZOOM_CLM","ZOOM_TECH_SUPPORT");
        List<ZoomUser> pceList=zoomUserMasterService.getActiveZoomUsersByLocationAndZoomUserType(pc.getId(),
                "ZOOM_PCE","ZOOM_TECH_SUPPORT");
        List<ZoomUser> boPceList=zoomUserMasterService.getActiveZoomUsersByLocationAndZoomUserType(locationId,
                "ZOOM_BO_PCE","ZOOM_TECH_SUPPORT");
        List<ZoomUser> pcmList=zoomUserMasterService.getActiveZoomUsersByLocationAndZoomUserType(pc.getId(),
                "ZOOM_BO_PCM","ZOOM_TECH_SUPPORT");
        ccList.addAll(rmList.stream().map(e->e.getEmail()).collect(Collectors.toList()));
        ccList.addAll(clmList.stream().map(e->e.getEmail()).collect(Collectors.toList()));
        ccList.addAll(pceList.stream().map(e->e.getEmail()).collect(Collectors.toList()));
        ccList.addAll(boPceList.stream().map(e->e.getEmail()).collect(Collectors.toList()));
        ccList.addAll(pcmList.stream().map(e->e.getEmail()).collect(Collectors.toList()));
        return ccList;
    }

    private void  filterEmails(DEPSNotification dto,Set<String> bccList){
        dto.getBccList().addAll(bccList);
        if("production".equalsIgnoreCase(System.getProperty("spring.profiles.active"))) {
            return;
        }
        List<String> dummyEmailList = new ArrayList<>();
        dto.getEmailIdList().forEach(email->{
            dummyEmailList.add(email.split("@")[0]+"@rivigodummy.com");
        });
        dto.getEmailIdList().clear();
        dto.getEmailIdList().addAll(dummyEmailList);

        List<String> dummyCcList = new ArrayList<>();
        dto.getCcList().forEach(email->{
            dummyCcList.add(email.split("@")[0]+"@rivigodummy.com");
        });
        dto.getCcList().clear();
        dto.getCcList().addAll(dummyCcList);

    }

    private void updateStakeHolders(DEPSNotification dto, Long consignmentId, Map<Long, List<DEPSNotificationDTO>> consignmentIdToDepsRecordMap,
                                    DEPSNotificationContext depsNotificationContext, Set<String> reportingCcList, Set<String> bccList) {
        dto.getCcList().addAll(reportingCcList);
        switch (dto.getScenario()) {
            case BFTRIP:
                dto.getEmailIdList().add(dto.getReportee().getEmail());
                break;
            case INBOUND:
                dto.getCcList().addAll(getCcList(dto.getReporteeLocation().getId()));
                dto.getEmailIdList().add(dto.getReportee().getEmail());
                dto.getEmailIdList().add(dto.getReporter().getEmail());
                break;
            case WITHINPC:
                dto.getEmailIdList().add(dto.getReportee().getEmail());
                dto.getEmailIdList().add(dto.getReporter().getEmail());
                break;
            case RETURN_SCAN:
                if(!dto.getReporterLocation().getId().equals(dto.getReporteeLocation().getId())){
                    dto.getCcList().addAll(getCcList(dto.getReporteeLocation().getId()));
                }
                dto.getEmailIdList().add(dto.getReportee().getEmail());
                break;
            case PICKUP:
                dto.getEmailIdList().add(dto.getReportee().getEmail());
                break;
            default:
                break;
        }
        filterEmails(dto,bccList);
    }

    private void updateResponsiblePersonAndLocation(DEPSNotification dto, Long consignmentId,
                                                    Map<Long, List<DEPSNotificationDTO>> consignmentIdToDepsRecordMap,
                                                    DEPSNotificationContext depsNotificationContext,
                                                    Map<Long, Trip> tripIdToTripMap,
                                                    Map<Long, PickupRunSheet> prsIdToPRSMap,
                                                    Map<Long, ConsignmentHistory> consignmentIdToLatestHistoryMap) {
        List<DEPSNotificationDTO> depsRecordList = consignmentIdToDepsRecordMap.get(consignmentId);
        ConsignmentSchedule previousSchedule = getPreviousSchedule(depsNotificationContext, consignmentId);
        DEPSNotificationDTO depsRecord = depsRecordList.get(0);
        dto.setDepsTaskType(depsRecord.getDepsTaskType());
        addDEPSUserDTO(dto, userMasterService.getById(depsRecord.getReportedById()), DEPSLocationType.INBOUND);
        addDEPSLocationDTO(dto, locationServiceV2.getLocationById(depsRecord.getInboundLocationId()), DEPSLocationType.INBOUND);
        ConsignmentHistory latestHistory;
        switch (depsRecord.getDepsTaskType()) {
            case UNLOADING:
                switch (depsRecord.getTripType()) {
                    case PRS:
                        dto.setScenario(DEPSScenario.PICKUP);
                        getDEPSReporteePRSDTO(dto, prsIdToPRSMap.get(depsRecord.getTripId()));
                        break;
                    case TRIP:
                        if(tripIdToTripMap.containsKey(depsRecord.getTripId()) && tripIdToTripMap.get(depsRecord.getTripId()).getType()==BF)
                            dto.setScenario(DEPSScenario.BFTRIP);
                        else {
                            dto.setScenario(DEPSScenario.INBOUND);
                        }
                        if(previousSchedule!=null && previousSchedule.getLoadedById()!=null) {
                            addDEPSUserDTO(dto, userMasterService.getById(previousSchedule.getLoadedById()), DEPSLocationType.OUTBOUND);
                            addDEPSLocationDTO(dto, locationServiceV2.getLocationById(previousSchedule.getLocationId()), DEPSLocationType.OUTBOUND);
                        }
                        break;
                }
                break;
            case STOCK_CHECK:
            case LOADING:
            case HANDOVER:
                dto.setScenario(DEPSScenario.WITHINPC);
                latestHistory = consignmentIdToLatestHistoryMap.get(consignmentId);
                if(latestHistory!=null) {
                    addDEPSUserDTO(dto, userMasterService.getById(latestHistory.getCreatedById()), DEPSLocationType.OUTBOUND);
                    addDEPSLocationDTO(dto, locationServiceV2.getLocationById(latestHistory.getLocationId()), DEPSLocationType.OUTBOUND);
                }
                break;
            case RETURN_SCAN:
                dto.setScenario(DEPSScenario.RETURN_SCAN);
                TransportationPartnerMapping transportationPartnerMapping = transportationPartnerMappingService.getByDRSId(depsRecord.getTripId());
                latestHistory = consignmentIdToLatestHistoryMap.get(consignmentId);
                if(transportationPartnerMapping!=null) {
                    getDEPSReporteeDRSUserDTO(dto, transportationPartnerMapping, DEPSLocationType.OUTBOUND);
                    addDEPSLocationDTO(dto, locationServiceV2.getLocationById(latestHistory.getLocationId()), DEPSLocationType.OUTBOUND);
                }
                break;
            default:
                break;
        }
    }

    private void getDEPSReporteeDRSUserDTO(DEPSNotification dto, TransportationPartnerMapping transportationPartnerMapping, DEPSLocationType outbound) {
        User user = null;
        FeederVendor feederVendor = null;
        DEPSNotification.DEPSUserDTO userDTO = new DEPSNotification.DEPSUserDTO();
        switch (transportationPartnerMapping.getPartnerType()) {
            case RIVIGO_CAPTAIN:
                user = userMasterService.getById(transportationPartnerMapping.getUserId());
                userDTO.setType(PartnerType.RIVIGO_CAPTAIN.name());
                break;
            case BUSINESS_PARTNER:
                List<StockAccumulator> accumulatorList = stockAccumulatorService.getByStockAccumulatorRoleAndAccumulationPartnerId(StockAccumulatorRole.STOCK_ACCUMULATOR_ADMIN, transportationPartnerMapping.getPartnerId());
                if(!CollectionUtils.isEmpty(accumulatorList)) {
                    StockAccumulator accumulator = accumulatorList.get(0);
                    user = accumulator.getUser();
                    userDTO.setType(PartnerType.BUSINESS_PARTNER.name());
                }
                break;
            case MARKET:
                user = userMasterService.getById(transportationPartnerMapping.getUserId());
                userDTO.setType(PartnerType.MARKET.name());
                break;
            case VENDOR:
                user = userMasterService.getById(transportationPartnerMapping.getUserId());
                feederVendor = feederVendorService.getFeederVendorById(transportationPartnerMapping.getPartnerId());
                userDTO.setType(PartnerType.VENDOR.name());
                break;
        }
        if(feederVendor!=null) {
            DEPSNotification.DEPSUserDTO.setFeederVendorDetails(dto, userDTO, feederVendor);
        }
        DEPSNotification.DEPSUserDTO.setUserDetails(dto, userDTO, user);
        dto.setReportee(userDTO);
    }

    private DEPSNotification.DEPSUserDTO getDEPSReporteePRSDTO(DEPSNotification dto, PickupRunSheet pickupRunSheet) {
        DEPSNotification.DEPSUserDTO userDTO = new DEPSNotification.DEPSUserDTO();
        User user = pickupRunSheet.getUser();
        BusinessPartner businessPartner = pickupRunSheet.getBusinessPartner();
        boolean isBpUser = false;
        Organization organization=null;
        if(businessPartner!=null) {
            List<StockAccumulator> accumulatorList = stockAccumulatorService.getByStockAccumulatorRoleAndAccumulationPartnerId(StockAccumulatorRole.STOCK_ACCUMULATOR_ADMIN, businessPartner.getId());
            if(!CollectionUtils.isEmpty(accumulatorList)) {
                StockAccumulator accumulator = accumulatorList.get(0);
                user = accumulator.getUser();
                isBpUser = true;
            }
        }else if(!user.getOrganizationId().equals(RIVIGO_ORGANIZATION_ID)){
            organization=organizationService.getById(user.getOrganizationId());
        }

        userDTO.setId(user.getId());
        if(organization == null){
            userDTO.setEmail(user.getEmail());
        }else{
            userDTO.setEmail(organization.getEmail());
        }
        userDTO.setLocationType(DEPSLocationType.OUTBOUND);
        userDTO.setName(user.getName());
        userDTO.setOrgId(user.getOrganizationId());
        if(isBpUser) {
            userDTO.setType(StockAccumulatorRole.STOCK_ACCUMULATOR_ADMIN.name());
        } else {
            ZoomUser zoomUser = zoomUserMasterService.getZoomUser(user.getEmail());
            if (zoomUser != null) {
                userDTO.setType(zoomUser.getZoomUserType());
            }
        }
        dto.setReportee(userDTO);
        return userDTO;
    }

    private DEPSNotification.DEPSUserDTO addDEPSUserDTO(DEPSNotification dto, User user, DEPSLocationType locationType) {
        DEPSNotification.DEPSUserDTO userDTO = new DEPSNotification.DEPSUserDTO();
        userDTO.setId(user.getId());
        userDTO.setEmail(user.getEmail());
        userDTO.setLocationType(locationType);
        userDTO.setName(user.getName());
        userDTO.setOrgId(user.getOrganizationId());
        if(user.getOrganizationId()!=1) {
            userDTO.setType(organizationService.getById(user.getOrganizationId()).getType().name());
        }

        try{
            ZoomUser zoomUser = zoomUserMasterService.getZoomUser(user.getEmail());
            userDTO.setType(zoomUser.getZoomUserType());
        }catch(Exception e){

        }
        if(locationType==DEPSLocationType.LAST_SCAN) {
            dto.setLastScannedByUser(userDTO);
        } else if (locationType == DEPSLocationType.INBOUND){
            dto.setReporter(userDTO);
        } else if(locationType==DEPSLocationType.OUTBOUND) {
            dto.setReportee(userDTO);
        }
        return userDTO;
    }


    private ConsignmentSchedule getPreviousSchedule(DEPSNotificationContext depsNotificationContext, Long consignmentId) {
        List<ConsignmentSchedule> consignmentSchedules = depsNotificationContext.getConsignmentIdToScheduleMap().get(consignmentId);
        Collections.sort(consignmentSchedules);
        final ConsignmentSchedule[] lastSchedule = new ConsignmentSchedule[1];
        consignmentSchedules.forEach(consignmentSchedule -> {
            if(consignmentSchedule.getPlanStatus()== ConsignmentLocationStatus.LEFT)
                lastSchedule[0] = consignmentSchedule;
        });

        return lastSchedule[0];
    }

    private List<Long> getTripIdList(DEPSNotificationContext depsNotificationContext, ZoomTripType tripType) {
        if(depsNotificationContext == null || CollectionUtils.isEmpty(depsNotificationContext.getNewDEPSRecordList()))
            return Collections.emptyList();
        return depsNotificationContext.getNewDEPSRecordList().stream()
                .filter(depsRecord -> depsRecord.getTripType()==tripType)
                .map(DEPSNotificationDTO::getTripId).collect(Collectors.toList());
    }

    private void updateCnoteMetadata(DEPSNotification dto, Long consignmentId, Map<Long, List<DEPSNotificationDTO>> consignmentIdToDepsRecordMap, DEPSNotificationContext depsNotificationContext) {
        dto.setConsignmentId(consignmentId);
        dto.setDepsBoxesCount(consignmentIdToDepsRecordMap.get(consignmentId).size());
        dto.setDepsIdList(consignmentIdToDepsRecordMap.get(consignmentId).stream().map(DEPSNotificationDTO::getId).collect(Collectors.toList()));
        Consignment consignment = depsNotificationContext.getConsignmentIdToConsignmentMap().get(consignmentId);
        dto.setCnote(consignment.getCnote());
        dto.setClientName(consignment.getClient().getName());
        dto.setBookingDateTime(consignment.getBookingDateTime().getMillis());
        if(consignment.getPromisedDeliveryDateTime()!=null) {
            dto.setClientPromisedDeliveryDateTime(consignment.getPromisedDeliveryDateTime().getMillis());
        }
        dto.setFromLocationId(consignment.getFromId());
        Location fromLocation = locationServiceV2.getLocationById(consignment.getFromId());
        if(fromLocation!=null) {
            dto.setFromLocationName(fromLocation.getName());
            dto.setFromLocationCode(fromLocation.getCode());
        }
        dto.setInvoiceValue(consignment.getValue());
        dto.setResolvedCount(0);
        if(dto.getCnote().contains(SECONDARY_CNOTE_SEPARATOR)){
            dto.setOriginalNumberOfBoxes(consignmentService.getOriginalNumberOfBoxesByCnote(dto.getCnote().split(SECONDARY_CNOTE_SEPARATOR)[0]));
        }else{
            dto.setOriginalNumberOfBoxes(consignment.getOriginalNoOfBoxes());
        }
        dto.setExpectedLoss(dto.getInvoiceValue()== null ?null :(dto.getInvoiceValue()*dto.getDepsBoxesCount())/dto.getOriginalNumberOfBoxes());
    }

    public void sendNotifications(List<DEPSNotification> depsNotificationList) {
        String templateString= zoomPropertyService.getString("SHORTAGE_NOTIFICATION_TEMPLATE");
        Boolean isEmailEnabled = zoomPropertyService.getBoolean("DEPS_EMAIL_ENABLED", false);
        if(templateString != null && isEmailEnabled){
            depsNotificationList.forEach(depsNotification -> {
                String body = designEmailTemplate(depsNotification,templateString);
                String subject = "";//get from zoom property
                emailService.sendEmail(depsNotification.getEmailIdList(), depsNotification.getCcList(), depsNotification.getBccList(), subject, body, null);
            });
        }
    }

    private String designEmailTemplate(DEPSNotification depsNotification, String templateString) {
        Map<String, String> valuesMap = new HashMap<String, String>();
        valuesMap.put("cnote", depsNotification.getCnote());
        valuesMap.put("client_name", depsNotification.getClientName());
        valuesMap.put("shortage_count",Integer.toString( depsNotification.getDepsBoxesCount()));
        valuesMap.put("estimated_loss", depsNotification.getExpectedLoss().toString());
        valuesMap.put("last_scan_ou", depsNotification.getLastScannedAtLocation()==null ?"-":depsNotification.getLastScannedAtLocation().getName());
        valuesMap.put("last_scan_person", depsNotification.getLastScannedByUser() == null? "-":depsNotification.getLastScannedByUser().getName());
        valuesMap.put("reporting_ou", depsNotification.getReporterLocation().getName());
        valuesMap.put("reporting_person", depsNotification.getReporter().getName());

        switch (depsNotification.getScenario()) {
            case BFTRIP:
                valuesMap.put("scenario","Short at the time of pickup");
                valuesMap.put("responsible_ou","100 % "+ depsNotification.getReporterLocation().getName());
                valuesMap.put("responsible_person","100 % "+ depsNotification.getReportee().getName());
                valuesMap.put("dear",depsNotification.getReportee().getName());
                break;
            case INBOUND:
                valuesMap.put("scenario","Scan out -but not scan in");
                valuesMap.put("responsible_ou","100 % "+ depsNotification.getReporteeLocation().getName()+
                        " - 100 % "+ depsNotification.getReporterLocation().getName());
                valuesMap.put("responsible_person","100 % "+ depsNotification.getReportee().getName()+
                        " - 100 % "+ depsNotification.getReporter().getName());
                valuesMap.put("dear", depsNotification.getReportee().getName()+
                        " / "+ depsNotification.getReporter().getName());
                break;
            case WITHINPC:
                valuesMap.put("scenario","Within PC");
                valuesMap.put("responsible_ou","100 % "+ depsNotification.getReporterLocation().getName());
                valuesMap.put("responsible_person","OU = 1% each OA + 3% each TL + 10% BM / PCM + 20% Security + 30% Fauji contractor");
                valuesMap.put("dear", depsNotification.getReportee().getName()+
                        " / "+ depsNotification.getReporter().getName());
                break;
            case RETURN_SCAN:
                valuesMap.put("scenario","Short at time of delivery");
                valuesMap.put("responsible_ou","100 % "+ depsNotification.getReporteeLocation().getName()+
                        " - 100 % "+ depsNotification.getReporterLocation().getName());
                valuesMap.put("responsible_person","100 % "+ depsNotification.getReportee().getName());
                valuesMap.put("dear", depsNotification.getReportee().getName());
                break;
            case PICKUP:
                valuesMap.put("scenario","Short at the time of pickup");
                valuesMap.put("responsible_ou","100 % "+ depsNotification.getReporterLocation().getName());
                valuesMap.put("responsible_person","100 % "+ depsNotification.getReportee().getName());
                valuesMap.put("dear", depsNotification.getReportee().getName());
                break;
            default:
                break;
        }
        StrSubstitutor sub=new StrSubstitutor(valuesMap);
        return sub.replace(templateString);
    }


    public DEPSNotificationContext getNotificationContext(List<DEPSNotificationDTO> depsRecordList){

        DEPSNotificationContext context=new DEPSNotificationContext();

        List<Long> consignmentIdList=depsRecordList.stream().map(DEPSNotificationDTO::getConsignmentId).collect(Collectors.toList());

        List<Consignment> consignments = consignmentService.getConsignmentsByIds(consignmentIdList);

        Map<Long, Consignment> idToConsignmentMap = consignments.stream().collect(Collectors.toMap(Consignment::getId, Function.identity()));

        context.setConsignmentIdToConsignmentMap(idToConsignmentMap);

        Map<Long,List<ConsignmentSchedule>> cnToScheduleMap = consignmentScheduleService.getActivePlansMapByIds(consignmentIdList);

        context.setConsignmentIdToScheduleMap(cnToScheduleMap);

        context.setNewDEPSRecordList(depsRecordList);

        return context;

    }

}
