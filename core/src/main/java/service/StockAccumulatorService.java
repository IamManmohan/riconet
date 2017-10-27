package service;

import com.rivigo.zoom.common.enums.OperationalStatus;
import com.rivigo.zoom.common.enums.StockAccumulatorRole;
import com.rivigo.zoom.common.model.StockAccumulator;
import com.rivigo.zoom.common.repository.mysql.StockAccumulatorRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class StockAccumulatorService {

    @Autowired
    StockAccumulatorRepository stockAccumulatorRepo;

    public List<StockAccumulator> getByStockAccumulatorRoleAndAccumulationPartnerId(StockAccumulatorRole role, Long partnerId) {
		return stockAccumulatorRepo.findByStockAccumulatorRoleAndAccumulationPartnerId(role.toString(), partnerId);
	}

    public List<StockAccumulator> getByStockAccumulatorRoleAndAccumulationPartnerIdAndStatus(StockAccumulatorRole role,
                                                                                             Long partnerId, OperationalStatus status) {
        return stockAccumulatorRepo.findByStockAccumulatorRoleAndAccumulationPartnerIdAndStatus(role.toString(), partnerId,status.name());
    }

}
