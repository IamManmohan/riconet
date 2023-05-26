package com.rivigo.riconet.core.dto.logifreight;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class BulkUpdateTransporterStatusDTO extends VyomBaseResponseDto {
  List<EWayBillStatusDto> ewaybillStatus;
}
