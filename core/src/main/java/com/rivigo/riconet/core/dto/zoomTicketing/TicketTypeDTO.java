package com.rivigo.riconet.core.dto.zoomTicketing;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.rivigo.riconet.core.enums.zoomTicketing.AutoAssignSuggestion;
import com.rivigo.riconet.core.enums.zoomTicketing.AutoClosureTrigger;
import com.rivigo.riconet.core.enums.zoomTicketing.TicketEntityType;
import com.rivigo.riconet.core.enums.zoomTicketing.TicketFieldType;
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
public class TicketTypeDTO {

    private Long id;

    private String name;

    private TicketFieldType ticketFieldType;

    private TicketEntityType entityType;

    private AutoClosureTrigger autoClosureTrigger;

    private AutoAssignSuggestion autoAssignSuggestion;

    private Boolean isActive;

}
