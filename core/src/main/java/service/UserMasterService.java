package service;

import com.rivigo.zoom.common.model.User;
import com.rivigo.zoom.common.repository.mysql.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class UserMasterService {

    @Autowired
    private UserRepository userRepository;

    public User getById(Long id) {
        return userRepository.findById(id);
    }

}
