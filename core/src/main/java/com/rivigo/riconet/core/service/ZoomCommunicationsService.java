package com.rivigo.riconet.core.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rivigo.riconet.core.dto.ZoomCommunicationsSMSDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * Created by aditya on 22/2/18.
 */
@Slf4j
@Service
public class ZoomCommunicationsService {

    @Autowired
    SmsService smsService;

    @Autowired
    private ObjectMapper objectMapper ;

    public void processNotificationMessage(ZoomCommunicationsSMSDTO zoomCommunicationsSMSDTO) {

        log.info("ZoomCommunicationsService is in action");
//        log.info(str);
        if (null == zoomCommunicationsSMSDTO) {
            return;
        }
        log.info("Sending  msg");
        log.info(zoomCommunicationsSMSDTO.getMessage());
        log.info("");
        log.info(zoomCommunicationsSMSDTO.getMessage());
        log.info(zoomCommunicationsSMSDTO.getPhoneNumbers().get(0));
        log.info(zoomCommunicationsSMSDTO.getConfidential().toString());
        if (null == zoomCommunicationsSMSDTO.getPhoneNumbers() || zoomCommunicationsSMSDTO.getPhoneNumbers().isEmpty()) {
            return;
        }
        String return_value;
        //TODO: Change
//        zoomCommunicationsSMSDTO.getPhoneNumbers().forEach(
//            number -> {
//                smsService.sendSms("7795569771", zoomCommunicationsSMSDTO.getMessage());
//            }
//        );
        return_value = smsService.sendSms("7795569771", zoomCommunicationsSMSDTO.getMessage());
        log.info(return_value);
        log.info("Sent Message");
    }

}
