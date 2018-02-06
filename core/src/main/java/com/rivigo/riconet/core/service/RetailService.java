package com.rivigo.riconet.core.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rivigo.riconet.core.enums.ZoomPropertyName;
import com.rivigo.zoom.common.dto.RetailNotificationDTO;
import com.rivigo.zoom.common.dto.SmsDTO;
import com.rivigo.zoom.common.dto.zoombook.TransactionModelDTO;
import com.rivigo.zoom.common.enums.PaymentMode;
import com.rivigo.zoom.common.enums.PaymentType;
import com.rivigo.zoom.common.enums.RetailNotificationType;
import com.rivigo.zoom.common.enums.StockAccumulatorRole;
import com.rivigo.zoom.common.enums.zoombook.ZoomBookFunctionType;
import com.rivigo.zoom.common.enums.zoombook.ZoomBookTenantType;
import com.rivigo.zoom.common.enums.zoombook.ZoomBookTransactionSubHeader;
import com.rivigo.zoom.common.enums.zoombook.ZoomBookTransactionType;
import com.rivigo.zoom.common.model.PaymentDetailV2;
import com.rivigo.zoom.common.model.StockAccumulator;
import com.rivigo.zoom.common.model.TransportationPartnerMapping;
import com.rivigo.zoom.common.model.User;
import com.rivigo.zoom.common.model.ZoomUser;
import com.rivigo.zoom.common.model.mongo.RetailNotification;
import com.rivigo.zoom.common.model.neo4j.Location;
import com.rivigo.zoom.common.repository.mysql.PaymentDetailV2Repository;
import com.rivigo.zoom.common.repository.neo4j.AdministrativeEntityRepository;
import com.rivigo.zoom.exceptions.ZoomException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.text.StrSubstitutor;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
public class RetailService {

    @Autowired
    private StockAccumulatorService stockAccumulatorService;

    @Autowired
    private ZoomUserMasterService zoomUserMasterService;

    @Autowired
    private ZoomPropertyService zoomPropertyService;

    @Autowired
    private LocationService locationService;

    @Autowired
    private TransportationPartnerMappingService transportationPartnerMappingService;

    @Autowired
    private SmsService smsService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserMasterService userMasterService;

    @Autowired
    private AdministrativeEntityRepository administrativeEntityRepository;

    @Autowired
    private PaymentDetailV2Repository paymentDetailV2Repository;

    @Autowired
    private ZoomBookAPIClientService zoomBookAPIClientService;

    private static final DateTimeZone IST=DateTimeZone.forID("Asia/Kolkata");

    public void processRetailNotificationDTOList(List<RetailNotificationDTO> retailNotificationDTOList){
        if(CollectionUtils.isEmpty(retailNotificationDTOList)){
            return;
        }
        if(RetailNotificationType.DRS_DISPATCH.equals(retailNotificationDTOList.get(0).getNotificationType())){
            processDRSdispatch(retailNotificationDTOList);
            return;
        }
        for(RetailNotificationDTO dto:retailNotificationDTOList){
            processSingleNotification(dto);
        }
    }

    private void processDRSdispatch(List<RetailNotificationDTO> retailNotificationDTOList){
        StringBuilder sb=new StringBuilder();
        TransportationPartnerMapping tpm=transportationPartnerMappingService.getByDRSId(retailNotificationDTOList.get(0).getDrsId());
        User user=userMasterService.getById(tpm.getUserId());
        sb.append("DRS ")
                .append(retailNotificationDTOList.get(0).getDrsId())
                .append(" with ")
                .append(retailNotificationDTOList.get(0).getTotalCnCount())
                .append(" CNs is assigned to you. You need to collect Rs. ")
                .append(retailNotificationDTOList.stream().map(RetailNotificationDTO::getTotalAmount).reduce((x,y)-> x.add(y)).get())
                .append(" for ")
                .append(retailNotificationDTOList.size())
                .append(" To-Pay CNs: ");
        retailNotificationDTOList.forEach(retailNotificationDTO -> {
            retailNotificationDTO.setDrsUserId(user.getId());
            retailNotificationDTO.setDrsUserName(user.getName());
            retailNotificationDTO.setDrsUserMobile(user.getMobileNo());
            processSingleNotification(retailNotificationDTO);
            sb.append(retailNotificationDTO.getCnote())
                    .append(" - Rs. ")
                    .append(retailNotificationDTO.getTotalAmount())
                    .append(" ");
        });
        sb.append("Please do not deliver the shipment without To-Pay amount collection.");
        smsService.sendSms(retailNotificationDTOList.get(0).getDrsUserMobile(),sb.toString());
    }

    private void processCnCreateUpdateNotification(RetailNotification notification, String consigneeSmsTemplate, String consignorSmsTemplate){
        DateTimeFormatter formatter1 = DateTimeFormat.forPattern("dd-MM-yyyy ").withZone(IST);
        String dateStr = formatter1.print(notification.getEdd());
        notification.setEddString(dateStr);
        notification.setFromOuCluster(administrativeEntityRepository.findParentCluster(notification.getFromOuId()).getName());
        notification.setToOuCluster(administrativeEntityRepository.findParentCluster(notification.getToOuId()).getName());
        SmsDTO consignorSmsDTO=new SmsDTO();
        consignorSmsDTO.setMobileNumber(notification.getConsignorPhone());
        consignorSmsDTO.setSmsString(designSms(notification,consignorSmsTemplate));
        SmsDTO consigneeSmsDTO=new SmsDTO();
        consigneeSmsDTO.setMobileNumber(notification.getConsigneePhone());
        consigneeSmsDTO.setSmsString(designSms(notification,consigneeSmsTemplate));
        notification.setSmsList(Arrays.asList(consigneeSmsDTO,consignorSmsDTO));
        smsService.sendSms(consigneeSmsDTO.getMobileNumber(),consigneeSmsDTO.getSmsString());
        smsService.sendSms(consignorSmsDTO.getMobileNumber(),consignorSmsDTO.getSmsString());
    }

    private void processCollectionAndHandoverNotifications(RetailNotification notification){
        User user=userMasterService.getById(notification.getUserId());
        notification.setUserMobile(user.getMobileNo());
        notification.setUserName(user.getName());
        Location location=locationService.getLocationById(notification.getOuId());
        notification.setOuCode(location.getCode());
        if(notification.getNotificationType().equals(RetailNotificationType.HANDOVER)){
            DateTimeFormatter formatter = DateTimeFormat.forPattern("dd-MM-yyyy").withZone(IST);
            notification.setHandoveredDateString(formatter.print(DateTime.now()));
        }
        ZoomUser zoomUser=zoomUserMasterService.getByUserId(notification.getUserId());
        DateTime fromDate= DateTime.now().withZone(IST).withMillisOfDay(0);
        if(zoomUser!=null){
            getPendingHandoverConsignments(notification,
                    zoomBookAPIClientService.getEntityCollectionsSummary(notification.getUserId(),
                            ZoomBookFunctionType.USER_CASHBOOK.name(),
                            ZoomBookTenantType.RETAIL.name(), fromDate.getMillis(), fromDate.plusDays(1).getMillis(), true));
            String smsTemplate;
            if(notification.getNotificationType().equals(RetailNotificationType.CN_COLLECTION)){
                smsTemplate=zoomPropertyService.getString(ZoomPropertyName.RETAIL_COLLECTION_CREATION_USER_SMS_STRING);
            }else {
                smsTemplate=zoomPropertyService.getString(ZoomPropertyName.RETAIL_HANDOVER_USER_SMS_STRING);
            }
            String smsString=designSms(notification,smsTemplate);
            smsService.sendSms(notification.getUserMobile(),smsString);
            notification.setOuId(zoomUser.getLocationId());
            notification.setOuCode(locationService.getLocationById(zoomUser.getLocationId()).getCode());
            return;
        }
        StockAccumulator captain=stockAccumulatorService.getByUserId(notification.getUserId());
        if(captain==null){
            return;
        }
        getPendingHandoverConsignments(notification,
                zoomBookAPIClientService.getEntityCollectionsSummary(captain.getAccumulationPartnerId().getId(),
                        ZoomBookFunctionType.BP_CASHBOOK.name(),
                        ZoomBookTenantType.RETAIL.name(), fromDate.getMillis(), fromDate.plusDays(1).getMillis(), true));
        StockAccumulator bpAdmin=stockAccumulatorService.getByStockAccumulatorRoleAndAccumulationPartnerId(StockAccumulatorRole.STOCK_ACCUMULATOR_ADMIN,
                captain.getAccumulationPartnerId().getId()).get(0);
        String smsTemplate;
        if(notification.getNotificationType().equals(RetailNotificationType.CN_COLLECTION)){
            smsTemplate=zoomPropertyService.getString(ZoomPropertyName.RETAIL_COLLECTION_CREATION_BP_SMS_STRING);
            if(captain.getStockAccumulatorRole().equals(StockAccumulatorRole.STOCK_ACCUMULATOR_USER)){
                String template=zoomPropertyService.getString(ZoomPropertyName.RETAIL_COLLECTION_CREATION_BP_CAPTAIN_SMS_STRING);
                String sms=designSms(notification,template);
                smsService.sendSms(bpAdmin.getUser().getMobileNo(),sms);
            }
        }else {
            smsTemplate=zoomPropertyService.getString(ZoomPropertyName.RETAIL_HANDOVER_BP_SMS_STRING);
        }
        String smsString=designSms(notification,smsTemplate);
        smsService.sendSms(bpAdmin.getUser().getMobileNo(),smsString);
        notification.setOuId(captain.getZones().get(0).getZone().getLocationId());
        notification.setOuCode(locationService.getLocationById(captain.getZones().get(0).getZone().getLocationId()).getCode());
    }

    private void processSingleNotification(RetailNotificationDTO retailNotificationDTO){
        RetailNotification notification = objectMapper.convertValue(retailNotificationDTO, RetailNotification.class);
        notification.setPaymentModeString(notification.getPaymentMode()==null?"-":notification.getPaymentMode().displayName());
        switch (notification.getNotificationType()){
            case CN_CREATION:
                if(notification.getPaymentMode().equals(PaymentMode.COD)){
                    String consignorSmsTemplate=zoomPropertyService.getString(ZoomPropertyName.RETAIL_COD_CN_CREATION_CONSIGNOR_SMS_STRING);
                    String consigneeSmsTemplate=zoomPropertyService.getString(ZoomPropertyName.RETAIL_COD_CN_CREATION_CONSIGNEE_SMS_STRING);
                    processCnCreateUpdateNotification(notification,consigneeSmsTemplate,consignorSmsTemplate);
                }else {

                    String consignorSmsTemplate=zoomPropertyService.getString(ZoomPropertyName.RETAIL_PREPAID_CN_CREATION_CONSIGNOR_SMS_STRING);
                    String consigneeSmsTemplate=zoomPropertyService.getString(ZoomPropertyName.RETAIL_PREPAID_CN_CREATION_CONSIGNEE_SMS_STRING);
                    processCnCreateUpdateNotification(notification,consigneeSmsTemplate,consignorSmsTemplate);
                }
                break;
            case CN_UPDATE:
                if(notification.getPaymentMode().equals(PaymentMode.COD)){
                    String consignorSmsTemplate=zoomPropertyService.getString(ZoomPropertyName.RETAIL_COD_CN_UPDATE_CONSIGNOR_SMS_STRING);
                    String consigneeSmsTemplate=zoomPropertyService.getString(ZoomPropertyName.RETAIL_COD_CN_UPDATE_CONSIGNEE_SMS_STRING);
                   processCnCreateUpdateNotification(notification,consigneeSmsTemplate,consignorSmsTemplate);
                }else {
                    String consignorSmsTemplate=zoomPropertyService.getString(ZoomPropertyName.RETAIL_PREPAID_CN_UPDATE_CONSIGNOR_SMS_STRING);
                    String consigneeSmsTemplate=zoomPropertyService.getString(ZoomPropertyName.RETAIL_PREPAID_CN_UPDATE_CONSIGNEE_SMS_STRING);
                    processCnCreateUpdateNotification(notification,consigneeSmsTemplate,consignorSmsTemplate);
                }
                break;
            case DRS_DISPATCH:
                if(notification.getPaymentMode().equals(PaymentMode.COD)){
                    String consigneeSmsTemplate=zoomPropertyService.getString(ZoomPropertyName.RETAIL_COD_DRS_DISPATCH_CONSIGNEE_SMS_STRING);
                    String smsString=designSms(notification,consigneeSmsTemplate);
                    smsService.sendSms(notification.getConsigneePhone(),smsString);
                }
                break;
            case CN_COLLECTION:
            case HANDOVER:
                processCollectionAndHandoverNotifications(notification);
                break;
            default:
                break;
        }
    }

    private String designSms(RetailNotification retailNotification, String template) {
        if(objectMapper==null){
            objectMapper=new ObjectMapper();
        }
        DateTimeFormatter formatter = DateTimeFormat.forPattern("dd-MM-yyyy").withZone(IST);
        String dateStr =retailNotification.getEdd()!=null?
                formatter.print(retailNotification.getEdd()):"-";
        retailNotification.setEddString(dateStr);
        Map<String, String> valuesMap = objectMapper.convertValue(retailNotification,Map.class);
        StrSubstitutor sub=new StrSubstitutor(valuesMap);
        String sms= sub.replace(template);
        valuesMap.put("paymentType","-");
        StrSubstitutor sub2=new StrSubstitutor(valuesMap);
        return sub2.replace(sms);
    }

    public void getPendingHandoverConsignments(RetailNotification notification, List<TransactionModelDTO> transactionModelDTOList){
        Map<String, List<TransactionModelDTO>> cnoteTransactionMap = transactionModelDTOList
                .stream()
                .collect(Collectors.groupingBy(TransactionModelDTO::getReference));
        BigDecimal pendingCash=BigDecimal.ZERO;
        BigDecimal pendingCheque=BigDecimal.ZERO;
        List<Long> consignmentIdList=new ArrayList<>();
        for (Map.Entry<String, List<TransactionModelDTO>> entry : cnoteTransactionMap.entrySet()) {
            try {
                JsonNode jsonNode=objectMapper.readTree(entry.getValue().get(0).getRemarks());
                consignmentIdList.add(jsonNode.findValue("consignmentId").asLong());
            } catch (IOException e) {
                log.error("Error while reading remarks {} from zoombook {}", entry.getValue().get(0).getRemarks(), e);
                throw new ZoomException("Error while reading remarks from zoombook");
            }
        }
        Map<Long,PaymentDetailV2> paymentDetailV2Map=getPaymentdetailsByConsignmentIdIn(consignmentIdList);
        for (Map.Entry<String, List<TransactionModelDTO>> entry : cnoteTransactionMap.entrySet()) {
            PaymentDetailV2 paymentDetailV2=null;
            try {
                JsonNode jsonNode=objectMapper.readTree(entry.getValue().get(0).getRemarks());
                paymentDetailV2=paymentDetailV2Map.get(jsonNode.findValue("consignmentId").asLong());
            } catch (IOException e) {
                log.error("Error while reading remarks {} from zoombook {}", entry.getValue().get(0).getRemarks(), e);
                throw new ZoomException("Error while reading remarks from zoombook");
            }
            if(paymentDetailV2==null){
                continue;
            }

            ImmutablePair<BigDecimal, BigDecimal> pair = getTotalAndHandoveredAmountsFromUserBookTransactions(entry.getValue());
            BigDecimal cnAmount = pair.getLeft();
            BigDecimal handoveredAmount = pair.getRight();
            if(cnAmount.subtract(handoveredAmount).compareTo(BigDecimal.ZERO)>0){
                if(paymentDetailV2.getPaymentType().equals(PaymentType.CHEQUE)){
                    pendingCheque=pendingCheque.add(cnAmount.subtract(handoveredAmount));
                }else if(paymentDetailV2.getPaymentType().equals(PaymentType.CASH)){
                    pendingCash=pendingCash.add(cnAmount.subtract(handoveredAmount));
                }
            }
        }
        notification.setCashPendingAmount(pendingCash);
        notification.setChequePendingAmount(pendingCheque);
    }

    private ImmutablePair<BigDecimal, BigDecimal> getTotalAndHandoveredAmountsFromUserBookTransactions(List<TransactionModelDTO> consignmentTransactions) {
        BigDecimal totalAmount = BigDecimal.ZERO;
        BigDecimal handoveredAmount = BigDecimal.ZERO;
        for (TransactionModelDTO transactionModelDTO : consignmentTransactions) {
            if (ZoomBookTransactionSubHeader.HANDOVER.equals(transactionModelDTO.getTransactionSubHeader())) {
                handoveredAmount = handoveredAmount.add(transactionModelDTO.getAmount());
            } else {
                if (ZoomBookTransactionType.CREDIT.equals(transactionModelDTO.getTransactionType())) {
                    totalAmount = totalAmount.add(transactionModelDTO.getAmount());
                } else {
                    totalAmount = totalAmount.subtract(transactionModelDTO.getAmount());
                }
            }
        }
        return new ImmutablePair<>(totalAmount, handoveredAmount);
    }

    public Map<Long,PaymentDetailV2> getPaymentdetailsByConsignmentIdIn(List<Long> consignmentIds) {
        return paymentDetailV2Repository.findByConsignmentIdInAndIsActive(consignmentIds, true)
                .stream()
                .collect(Collectors.toMap(PaymentDetailV2::getConsignmentId, Function.identity()));
    }
}