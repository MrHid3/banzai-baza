package pl.banzaijiujitsu.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.banzaijiujitsu.backend.model.Privilege;
import pl.banzaijiujitsu.backend.repository.PrivilegeRepository;

import java.util.Optional;

@Service
public class PrivilegeService {

    @Autowired
    private PrivilegeRepository privilegeRepository;

    public Optional<Privilege> findByName(String name) {
        return privilegeRepository.findByName(name);
    }

    public Privilege save(Privilege privilege) {
        return privilegeRepository.save(privilege);
    }
}
