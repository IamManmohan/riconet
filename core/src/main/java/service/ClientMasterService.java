package service;

import com.rivigo.zoom.common.model.Client;
import com.rivigo.zoom.common.repository.mysql.ClientRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ClientMasterService {

    @Autowired
    ClientRepository clientRepository;

    public Client getClientById(Long id) {
        return clientRepository.findOne(id);
    }
}
