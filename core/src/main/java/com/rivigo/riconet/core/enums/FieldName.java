package com.rivigo.riconet.core.enums;

/**
 * @author ramesh
 * @date 15-Aug-2018
 */
public enum FieldName {

  LAST_UPDATED_BY_ID;

  public enum Ticketing {
    ID,
    TICKET_ID,
    OLD_ASSIGNEE_ID,
    ASSIGNEE_ID,
    OLD_ASSIGNEE_TYPE,
    ASSIGNEE_TYPE,
    ESCALATION_LEVEL,
    CURRENT_ESCALATION_ID,
    ESCALATED_TO_ID,
    ESCALATED_TO_EMAIL,
    OLD_STATUS,
    STATUS,
    OLD_SEVERITY,
    SEVERITY,
    CREATED_BY_ID,
    CREATOR_EMAIL,
    CC_USER_EMAIL_LIST,
    ASSIGNEE_EMAIL_LIST,
    OWNER_EMAIL_LIST,
    OLD_ASSIGNEE_GROUP_NAME_OR_ASSIGNEE_EMAIL,
    ASSIGNEE_GROUP_NAME_OR_ASSIGNEE_EMAIL, // if assignee_type = 'GROUP' then groupName else
    // owner_email
    OWNER_GROUP_NAME_OR_OWNER_EMAIL, // if owner_type = 'GROUP' then groupName else owner_email
    S3URL,
    TICKET_TYPE,
    COMMENT_TEXT,
    COMMENTOR_EMAIL,
    SLA,
    ADDER_EMAIL,
    NEWLY_CCED_USER_ID,
    NEWLY_CCED_EMAIL,
    LAST_UPDATED_BY_EMAIL,
    COMMENT_HISTORY
  }
}
