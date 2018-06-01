package com.rivigo.riconet.core.dto.zoomticketing;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;
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
public class GroupDTO {

  private Long id;

  private String name;

  private String email;

  private Long groupTypeId;

  private GroupTypeDTO groupTypeDTO;

  private Long groupLeadId;

  private UserDTO groupLead;

  private Long locationId;

  private String locationCode;

  private List<Long> userIdList;

  private List<UserDTO> userDTOList;

  private Boolean isActive;
}
