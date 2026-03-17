package pl.banzaijiujitsu.backend.component;


import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;
import pl.banzaijiujitsu.backend.model.Privilege;
import pl.banzaijiujitsu.backend.model.Role;
import pl.banzaijiujitsu.backend.repository.AppUserRepository;
import pl.banzaijiujitsu.backend.repository.PrivilegeRepository;
import pl.banzaijiujitsu.backend.repository.RoleRepository;
import pl.banzaijiujitsu.backend.service.EncodingService;

import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;

@Component
public class SetupDataLoader implements ApplicationListener<ContextRefreshedEvent> {

    boolean alreadySetup = false;

    @Autowired
    private AppUserRepository appUserRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PrivilegeRepository privilegeRepository;

    @Autowired
    private EncodingService encodingService;

    @Override
    @Transactional
    public void onApplicationEvent(ContextRefreshedEvent event) {

        if (alreadySetup){
            return;
        }

        Privilege readAllPrivilege = createPrivilegeIfNotFound("READ_ALL_MEMBERS_PRIVILAGE");
        Privilege readOwnMembers = createPrivilegeIfNotFound("READ_OWN_MEMBERS_PRIVILAGE");
        Privilege addMembers = createPrivilegeIfNotFound("CREATE_MEMBERS");
        createRoleIfNotFound("ROLE_ADMIN", Arrays.asList(readAllPrivilege, addMembers));
        createRoleIfNotFound("ROLE_COACH", Arrays.asList(readOwnMembers, addMembers));
    }

    @Transactional
    Privilege createPrivilegeIfNotFound(String name) {

        Optional<Privilege> optionalPrivilege = privilegeRepository.findByName(name);
        Privilege privilege;
        if (optionalPrivilege.isEmpty()) {
            privilege = new Privilege(name);
            privilegeRepository.save(privilege);
        }else{
            privilege = optionalPrivilege.get();
        }
        return privilege;
    }

    @Transactional
    Role createRoleIfNotFound(String name, Collection<Privilege> privileges) {

        Role role;
        Optional<Role> optionalRole = roleRepository.findByName(name);
        if (optionalRole.isEmpty()) {
            role = new Role(name);
            role.setPrivileges(privileges);
            roleRepository.save(role);
        }else{
            role = optionalRole.get();
        }
        return role;
    }

}
