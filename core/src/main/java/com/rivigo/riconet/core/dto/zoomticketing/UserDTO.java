package com.rivigo.riconet.core.dto.zoomticketing;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.rivigo.riconet.core.enums.zoomticketing.UserType;
import com.rivigo.zoom.common.enums.OperationalStatus;
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
public class UserDTO {

    private Long id;

    private Long zoomUserId;

    private String email;

    private String mobileNo;

    private String name;

    private OperationalStatus status;

    private Long organizationId;

    private UserType userType;

    private  String locationCode;
}