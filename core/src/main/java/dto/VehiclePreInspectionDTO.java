package dto;


import com.rivigo.zoom.common.enums.ZoomTripType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.joda.time.DateTime;

/**
 * Created by chirag on 31/3/17.
 */

@Getter
@Setter
@ToString
@NoArgsConstructor
public class VehiclePreInspectionDTO {

    private Long id;
    private ZoomTripType tripType;
    private Long tripId;
    private Boolean isContainerSealed;
    private Boolean hiddenLockPresent;
    private Boolean tarpaulinPresent;
    private Boolean cargonetHooksPresent;
    private DateTime createdAt;
    private DateTime lastUpdatedAt;
    private Long createdById;
    private Long lastUpdatedById;

}
