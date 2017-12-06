package com.rivigo.riconet.core.service;


import com.rivigo.common.util.MailUtils;
import com.rivigo.zoom.common.enums.EmailDlName;
import com.rivigo.zoom.common.model.EmailDL;
import com.rivigo.zoom.common.pojo.AbstractMailNotificationEntity;
import com.rivigo.zoom.common.repository.mysql.EmailDLRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.AbstractEnvironment;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@Service
@Slf4j
public class EmailService {

    public static final String SHORTAGE_EMAIL_ID = "shortage.desk@rivigo.com";
    public static final String SHORTAGE_EMAIL_PASS = "deps@1122";

    public static final String DOCUMENT_EMAIL_ID = "document.desk@rivigo.com";
    public static final String DOCUMENT_EMAIL_PASS = "deps@1122";

    @Autowired
    private EmailDLRepository emailDLRepository;


    public void sendShortageEmail(Collection<String> toRecipients, Collection<String> ccRecipients, Collection<String> bccRecipients, String subject, String body, File file) {
        MailUtils.sendEmail(SHORTAGE_EMAIL_ID, SHORTAGE_EMAIL_PASS, new ArrayList<>(toRecipients), new ArrayList<>(ccRecipients), new ArrayList<>(bccRecipients), subject, body, file);
    }

    public Set<String> getEmails(EmailDlName dl){
        return  emailDLRepository.findByDlAndIsActive(dl, 1).stream().map(EmailDL::getEmail).collect(Collectors.toSet());
    }

    public void sendDocumentIssueEmail(Collection<String> toRecipients, Collection<String> ccRecipients, Collection<String> bccRecipients, String subject, String body, File file) {
        MailUtils.sendEmail(DOCUMENT_EMAIL_ID, DOCUMENT_EMAIL_PASS, new ArrayList<>(toRecipients), new ArrayList<>(ccRecipients), new ArrayList<>(bccRecipients), subject, body, file);
    }

    public void  filterEmails(AbstractMailNotificationEntity dto, Set<String> bccList, boolean isTesting){
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
}
