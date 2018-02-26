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

    public void processNotificationMessage(String str) throws IOException {

        log.info("ZoomCommunicationsService is in action");
        log.info(str);
        ZoomCommunicationsSMSDTO zoomCommunicationsSMSDTO;
        try {
            zoomCommunicationsSMSDTO = objectMapper.readValue(str, ZoomCommunicationsSMSDTO.class);
        } catch (Exception e ) {
            log.error("failed", e);
            return;
        }

        if (null != zoomCommunicationsSMSDTO) {
            log.info(zoomCommunicationsSMSDTO.getMessage());
            log.info(zoomCommunicationsSMSDTO.getPhoneNumbers().get(0));
            log.info(zoomCommunicationsSMSDTO.getConfidential().toString());
            String return_value = smsService.sendSms("7503810874", zoomCommunicationsSMSDTO.getMessage());
            log.info(return_value);
        }
        log.info("Done");
    }

}
