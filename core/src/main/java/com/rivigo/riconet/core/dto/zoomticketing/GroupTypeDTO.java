package com.rivigo.riconet.core.dto.zoomticketing;

import com.rivigo.riconet.core.enums.zoomticketing.LocationType;
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
public class GroupTypeDTO {

  private Long id;

  private LocationType locationType;

  private String name;

  private Boolean isActive;

}
