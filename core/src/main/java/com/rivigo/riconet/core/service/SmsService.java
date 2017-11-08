package com.rivigo.riconet.core.service;

import com.rivigo.riconet.core.dto.SmsMessageDTO;
import com.rivigo.riconet.core.enums.ZoomPropertyName;
import com.rivigo.zoom.exceptions.ZoomException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class SmsService {

    @Value("${notification.root.url}")
    public String rootUrl;

    @Value("${notification.sms.api}")
    public String smsApi;

    @Value("${notification.sms.enable}")
    public Boolean smsEnable;

    @Autowired
    public ZoomPropertyService zoomPropertyService;

    private static  final  String SMS_DISABLED="sending sms is disabled";

    private static  final  String SMS_SERVER_URL_ABSENT="sms server url is absent";

    public String sendSms(String mobileNo, String message) {

        if(!smsEnable){
            return SMS_DISABLED;
        }
        List<String> phoneNumbers = new ArrayList<>();
        String smsString=message;
        if( "production".equalsIgnoreCase(System.getProperty("spring.profiles.active"))) {
            phoneNumbers.add(mobileNo);
        }else{
            String defaultPhone = zoomPropertyService.getString(ZoomPropertyName.DEFAULT_SMS_NUMBER);
            phoneNumbers.add(defaultPhone);
            smsString=mobileNo+" - "+smsString;
        }


        log.info(mobileNo+"-------"+smsString);

        URI uri = getURI(smsApi);
        if (uri != null) {
            RestTemplate restTemplate = new RestTemplate();
            SmsMessageDTO smsRequest = new SmsMessageDTO(phoneNumbers, smsString);
            String responseEng = restTemplate.postForObject(uri, smsRequest, String.class);
            if (responseEng == null) {
                throw new ZoomException("SMS is not sent properly");
            }
            return  responseEng;

        }
        return SMS_SERVER_URL_ABSENT;
    }

    private URI getURI(String str) {
        String url = rootUrl.concat(str);
        URI uri = null;
        try {
            uri = new URI(url);
        } catch (URISyntaxException e) {
            log.error("Error creating URI ", e);
        }
        return uri;
    }
}
