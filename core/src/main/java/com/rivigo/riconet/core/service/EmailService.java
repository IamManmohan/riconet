package com.rivigo.riconet.core.service;


import com.rivigo.common.util.MailUtils;
import com.rivigo.zoom.common.enums.EmailDlName;
import com.rivigo.zoom.common.model.EmailDL;
import com.rivigo.zoom.common.repository.mysql.EmailDLRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;


@Service
@Slf4j
public class EmailService {

    public static final String EMAIL_ID = "no-reply-zoom@rivigo.com";
    public static final String EMAIL_PASS = "2oom1g0R0ck$";

    public static final String DOCUMENT_EMAIL_ID = "document.desk@rivigo.com";
    public static final String DOCUMENT_EMAIL_PASS = "deps@1122";

    @Autowired
    private EmailDLRepository emailDLRepository;


    public void sendEmail(Collection<String> toRecipients, Collection<String> ccRecipients, Collection<String> bccRecipients, String subject, String body, File file) {
        MailUtils.sendEmail(EMAIL_ID, EMAIL_PASS, new ArrayList<>(toRecipients), new ArrayList<>(ccRecipients), new ArrayList<>(bccRecipients), subject, body, file);
    }

    public Set<String> getEmails(EmailDlName dl){
        return  emailDLRepository.findByDlAndIsActive(dl, 1).stream().map(EmailDL::getEmail).collect(Collectors.toSet());
    }

    public void sendDocumentIssueEmail(Collection<String> toRecipients, Collection<String> ccRecipients, Collection<String> bccRecipients, String subject, String body, File file) {
        MailUtils.sendEmail(DOCUMENT_EMAIL_ID, DOCUMENT_EMAIL_PASS, new ArrayList<>(toRecipients), new ArrayList<>(ccRecipients), new ArrayList<>(bccRecipients), subject, body, file);
    }
}
