package pl.banzaijiujitsu.backend.component;


import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;
import pl.banzaijiujitsu.backend.exception.InvalidPasswordException;
import pl.banzaijiujitsu.backend.model.AppUser;
import pl.banzaijiujitsu.backend.model.Location;
import pl.banzaijiujitsu.backend.model.Privilege;
import pl.banzaijiujitsu.backend.model.Role;
import pl.banzaijiujitsu.backend.service.*;

import java.util.*;

@Component
public class SetupDataLoader implements ApplicationListener<ContextRefreshedEvent> {

    boolean alreadySetup = false;

    @Autowired
    private RoleService roleService;

    @Autowired
    private PrivilegeService privilegeService;

    @Value("${backend.admin-email}")
    private String adminEmail;

    @Value("${backend.admin-password}")
    private String adminPassword;

    @Autowired
    private AppUserService appUserService;

    @Autowired
    private LocationService locationService;
    @Autowired
    private EncodingService encodingService;

    @Override
    @Transactional
    public void onApplicationEvent(ContextRefreshedEvent event) {

        if (alreadySetup) {
            return;
        }

        Privilege manageAllMembers = createPrivilegeIfNotFound("MANAGE_ALL_MEMBERS");
        Privilege manageOwnMembers = createPrivilegeIfNotFound("MANAGE_OWN_MEMBERS");
        Privilege manageUsers = createPrivilegeIfNotFound("MANAGE_USERS");

        Role adminRole = createRoleIfNotFound("ROLE_ADMIN", Arrays.asList(manageAllMembers, manageUsers));
        createRoleIfNotFound("ROLE_COACH", Collections.singletonList(manageOwnMembers));
        createRoleIfNotFound("ROLE_MOBILE", List.of());

        try {
            createAppUserIfNotFound(adminEmail, adminPassword, adminRole);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        createLocationIfNotFound("Szkoła Podstawowa nr 24", "SP24");
        createLocationIfNotFound("Szkoła Podstawowa nr 111", "SP111");

        alreadySetup = true;
    }

    @Transactional
    Privilege createPrivilegeIfNotFound(String name) {

        Optional<Privilege> optionalPrivilege = privilegeService.findByName(name);
        Privilege privilege;
        if (optionalPrivilege.isEmpty()) {
            privilege = new Privilege(name);
            privilegeService.save(privilege);
        } else {
            privilege = optionalPrivilege.get();
        }
        return privilege;
    }

    @Transactional
    Role createRoleIfNotFound(String name, Collection<Privilege> privileges) {

        Role role;
        Optional<Role> optionalRole = roleService.findByName(name);
        if (optionalRole.isEmpty()) {
            role = new Role(name);
            role.setPrivileges(privileges);
            roleService.save(role);
        } else {
            role = optionalRole.get();
        }
        return role;
    }

    AppUser createAppUserIfNotFound(String email, String password, Role role) throws InvalidPasswordException {

        AppUser appUser;
        Optional<AppUser> optionalAppUser = appUserService.findByEmail(email);

        if (optionalAppUser.isEmpty()) {
            appUser = new AppUser(email, password, role, encodingService);
            appUser.setStatus(AppUser.AppUserStatus.ACTIVE);
//            if(uuid != null){
//                appUser.setUuid(uuid);
//            }
            appUserService.save(appUser);
        } else {
            appUser = optionalAppUser.get();
        }

        return appUser;
    }

    @Transactional
    Location createLocationIfNotFound(String name, String shortname) {
        Location location;
        List<Location> optionalLocaltion = locationService.findByName(name);

        if (optionalLocaltion.isEmpty()) {
            location = new Location(name, shortname);
            locationService.save(location);
        } else {
            location = optionalLocaltion.get(0);
        }

        return location;
    }

}
