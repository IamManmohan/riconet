package com.rivigo.riconet.core.test;

import com.rivigo.riconet.core.enums.ZoomPropertyName;
import com.rivigo.riconet.core.service.ConsignmentScheduleService;
import com.rivigo.riconet.core.service.DocumentIssueNotificationService;
import com.rivigo.riconet.core.service.ZoomPropertyService;
import com.rivigo.zoom.common.enums.ConsignmentStatus;
import com.rivigo.zoom.common.model.ConsignmentSchedule;
import com.rivigo.zoom.common.model.mongo.DocumentIssueNotification;
import com.rivigo.zoom.common.repository.mysql.ConsignmentScheduleRepository;
import com.rivigo.zoom.exceptions.ZoomException;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;


/**
 * Created by ashfakh on 27/10/17.
 */

@Slf4j
public class DocIssueNotificationServiceTest extends TesterBase {

    @Autowired
    DocumentIssueNotificationService documentIssueNotificationService;

    @Autowired
    ConsignmentScheduleService consignmentScheduleService;

    @Autowired
    ZoomPropertyService zoomPropertyService;

    @Autowired
    ConsignmentScheduleRepository consignmentScheduleRepository;

    public String processMessage(String str){
        String[] split=str.split("\\|");
        if(split.length !=4){
            throw  new ZoomException("Error in message format");
        }
        Long consignmentId=Long.parseLong(split[0]);
        Long userId=Long.parseLong(split[1]);
        String subReason=split[2];
        ConsignmentStatus status=ConsignmentStatus.valueOf(split[3]);
        DocumentIssueNotification notification=documentIssueNotificationService.createNotificationData(consignmentId,userId,subReason,status);
        documentIssueNotificationService.sendNotifications(notification);
        return str;
    }

    @Test
    public void prsUnloadingNotification()
    {
        List<ConsignmentSchedule> consignmentSchedules = consignmentScheduleService.getActivePlan(8l);
        ConsignmentSchedule schedule=documentIssueNotificationService.getCurrentSchedule(consignmentSchedules);
        schedule.setArrivalTime(DateTime.now().getMillis());
        consignmentScheduleRepository.save(schedule);
        processMessage("8|1505|Invoice missing|RECEIVED_AT_OU");
    }

    @Test
    public void documentNonMissingNotification()
    {

        processMessage("8|1505|lol|RECEIVED_AT_OU");
    }

    @Test
    public void vehicleUnloadingWithinTwoHoursNotification()
    {
        List<ConsignmentSchedule> consignmentSchedules = consignmentScheduleService.getActivePlan(8l);
        ConsignmentSchedule schedule=documentIssueNotificationService.getCurrentSchedule(consignmentSchedules);
        schedule.setArrivalTime(DateTime.now().getMillis());
        consignmentScheduleRepository.save(schedule);
        processMessage("9|1505|lol|RECEIVED_AT_OU");
    }

    @Test
    public void vehicleUnloadingAfterTwoHoursNotification()
    {
        List<ConsignmentSchedule> consignmentSchedules = consignmentScheduleService.getActivePlan(8l);
        ConsignmentSchedule schedule=documentIssueNotificationService.getCurrentSchedule(consignmentSchedules);
        Integer bufferMinutes= zoomPropertyService.getInteger(ZoomPropertyName.DOCUMENT_ISSUE_BUFFER_MINUTES,120);
        schedule.setArrivalTime(DateTime.now().minusMinutes(bufferMinutes+1).getMillis());
        consignmentScheduleRepository.save(schedule);
        processMessage("9|1505|lol|RECEIVED_AT_OU");
    }

    @Test
    public void undeliveredNotification()
    {
        processMessage("10|1505|lol|UNDELIVERED");
    }


}
