package service;

import com.rivigo.zoom.common.enums.ZoomTripType;
import com.rivigo.zoom.common.model.TransportationPartnerMapping;
import com.rivigo.zoom.common.repository.mysql.TransportationPartnerMappingRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class TransportationPartnerMappingService {

    @Autowired
    TransportationPartnerMappingRepository transportationPartnerMappingRepository;

    public TransportationPartnerMapping getByDRSId(Long drsId){
        return transportationPartnerMappingRepository.findByTransportationTypeAndTransportationId(ZoomTripType.DRS,drsId);
    }
}
