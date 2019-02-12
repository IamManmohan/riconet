package com.rivigo.riconet.core.dto.qc;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Created by ashfakh on 12/02/19. */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class QcRequestDTO {
  Long consignmentId;
}
