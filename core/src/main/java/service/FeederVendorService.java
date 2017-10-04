package service;

import com.rivigo.zoom.common.model.FeederVendor;
import com.rivigo.zoom.common.repository.mysql.FeederVendorRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class FeederVendorService {

    @Autowired
    FeederVendorRepository feederVendorRepository;

    public FeederVendor getFeederVendorById(Long id) {
        return (feederVendorRepository.findById(id));
    }
}
