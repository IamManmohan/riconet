package com.rivigo.riconet.core.dto.zoomticketing;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.rivigo.riconet.core.enums.zoomticketing.AssigneeType;
import com.rivigo.riconet.core.enums.zoomticketing.EscalationLevel;
import com.rivigo.riconet.core.enums.zoomticketing.TicketEntityType;
import com.rivigo.riconet.core.enums.zoomticketing.TicketPriority;
import com.rivigo.riconet.core.enums.zoomticketing.TicketSource;
import com.rivigo.riconet.core.enums.zoomticketing.TicketStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author ramesh
 * @date 27-Feb-2018
 */

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class TicketDTO {

    private Long id;
    private Long parentId;
    private TicketPriority priority;
    private Long typeId;
    private String typeName;
    private TicketTypeDTO ticketTypeDTO; // ticket type info
    private TicketSource source;
    private TicketStatus status;
    private TicketStatus nextPossibleStatus;
    private Long ownerId;
    private UserDTO owner; // owner
    private String clientCode;
    private String clientName;
    private String entityId;
    private TicketEntityType entityType;
    private Long interactionCount;
    private Long assigneeId;
    private AssigneeType assigneeType;
    private String assigneeName;
    private Long assignorId;
    private Long requestorId;
    private UserDTO requestor;
    private String title;
    private String subject;
    private EmailDTO email;
    private String reasonOfClosure;
    private EscalationLevel escalationLevel;
    private String slaName;
    private Long createdAt;
    private Long escalateAt;

}
