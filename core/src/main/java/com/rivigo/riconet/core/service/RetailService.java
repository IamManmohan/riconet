package com.rivigo.riconet.core.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rivigo.riconet.core.enums.ZoomPropertyName;
import com.rivigo.zoom.common.dto.PickupNotificationDTO;
import com.rivigo.zoom.common.dto.RetailNotificationDTO;
import com.rivigo.zoom.common.dto.SmsDTO;
import com.rivigo.zoom.common.enums.OperationalStatus;
import com.rivigo.zoom.common.enums.PaymentMode;
import com.rivigo.zoom.common.enums.PickupNotificationType;
import com.rivigo.zoom.common.enums.PickupStatus;
import com.rivigo.zoom.common.enums.RetailNotificationType;
import com.rivigo.zoom.common.enums.StockAccumulatorRole;
import com.rivigo.zoom.common.enums.ZoomUserType;
import com.rivigo.zoom.common.model.Client;
import com.rivigo.zoom.common.model.PaymentDetailV2History;
import com.rivigo.zoom.common.model.Pickup;
import com.rivigo.zoom.common.model.StockAccumulator;
import com.rivigo.zoom.common.model.TransportationPartnerMapping;
import com.rivigo.zoom.common.model.User;
import com.rivigo.zoom.common.model.ZoomUser;
import com.rivigo.zoom.common.model.mongo.PickupNotification;
import com.rivigo.zoom.common.model.mongo.RetailNotification;
import com.rivigo.zoom.common.model.neo4j.Location;
import com.rivigo.zoom.common.repository.mongo.PickupNotificationRepository;
import com.rivigo.zoom.common.repository.mysql.PickupRepository;
import com.rivigo.zoom.exceptions.ZoomException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.text.StrSubstitutor;
import org.dozer.Mapper;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
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

    public void processRetailNotificationDTOList(List<RetailNotificationDTO> retailNotificationDTOList){
        if(CollectionUtils.isEmpty(retailNotificationDTOList)){
            return;
        }
        if(RetailNotificationType.DRS_DISPATCH.equals(retailNotificationDTOList.get(0).getNotificationType())){
            processDRSdispatch(retailNotificationDTOList);
            return;
        }
        retailNotificationDTOList.forEach(retailNotificationDTO -> processSingleNotification(retailNotificationDTO));
    }

    private void processDRSdispatch(List<RetailNotificationDTO> retailNotificationDTOList){
        StringBuilder sb=new StringBuilder();
        TransportationPartnerMapping tpm=transportationPartnerMappingService.getByDRSId(retailNotificationDTOList.get(0).getDrsId());
        User user=userMasterService.getById(tpm.getId());
        sb.append("DRS ")
                .append(retailNotificationDTOList.get(0).getDrsId())
                .append(" with ")
                .append(retailNotificationDTOList.get(0).getTotalCnCount())
                .append(" CNs is assigned to you. You need to collect Rs. ")
                .append(retailNotificationDTOList.stream().mapToDouble(RetailNotificationDTO::getTotalAmount).sum())
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
        ZoomUser zoomUser=zoomUserMasterService.getByUserId(notification.getUserId());
        if(zoomUser!=null){
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
        StockAccumulator bpAdmin=stockAccumulatorService.getByStockAccumulatorRoleAndAccumulationPartnerId(StockAccumulatorRole.STOCK_ACCUMULATOR_ADMIN,
                captain.getAccumulationPartnerId().getId()).get(0);
        String smsTemplate;
        if(notification.getNotificationType().equals(RetailNotificationType.CN_COLLECTION)){
            smsTemplate=zoomPropertyService.getString(ZoomPropertyName.RETAIL_COLLECTION_CREATION_BP_SMS_STRING);
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
        DateTimeFormatter formatter = DateTimeFormat.forPattern("dd-MM-yyyy").withZone(DateTimeZone.forID("Asia/Kolkata"));
        String dateStr =retailNotification.getEdd()!=null?
                formatter.print(retailNotification.getEdd()):"-";
        retailNotification.setEddString(dateStr);
        Map<String, String> valuesMap = objectMapper.convertValue(retailNotification,Map.class);
        StrSubstitutor sub=new StrSubstitutor(valuesMap);
        return sub.replace(template);
    }
}