package service;

import com.rivigo.zoom.common.model.PickupRunSheet;
import com.rivigo.zoom.common.repository.mysql.PRSRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
public class PRSService {

    @Autowired
    PRSRepository prsRepository;

    public Map<Long, PickupRunSheet> getPrsMapByPRSIdIn(List<Long> prsTripIdList) {
        return ((List<PickupRunSheet>)prsRepository.findAll(prsTripIdList)).stream().collect(Collectors.toMap(PickupRunSheet::getId, Function.identity()));
    }
}


