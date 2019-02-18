package com.rivigo.riconet.core.dto.qc;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Created by ashfakh on 11/02/19. */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class QcResponseDTO {
  Boolean decision;
  String disposition;
}