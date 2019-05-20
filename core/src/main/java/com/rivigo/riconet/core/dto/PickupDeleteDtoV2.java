package com.rivigo.riconet.core.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class PickupDeleteDtoV2 {
  Long id;
  Long failureReasonId;
  Long parentPRQId; // recurring prq  if null then its not a child prq
  Boolean deleteParentPRQ; // its original recurring prq
}
