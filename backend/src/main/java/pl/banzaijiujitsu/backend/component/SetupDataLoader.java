package pl.banzaijiujitsu.backend.component;


import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;
import pl.banzaijiujitsu.backend.exception.InvalidPasswordException;
import pl.banzaijiujitsu.backend.model.*;
import pl.banzaijiujitsu.backend.service.*;

import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;

@Component
public class SetupDataLoader implements ApplicationListener<ContextRefreshedEvent> {

    boolean alreadySetup = false;

    @Autowired
    private RoleService roleService;

    @Autowired
    private PrivilegeService privilegeService;

    @Autowired
    private PaymentMethodService paymentMethodService;

    @Value("${backend.admin-email}")
    private String adminEmail;

    @Value("${backend.admin-password}")
    private String adminPassword;

    @Autowired
    private AppUserService appUserService;
    @Autowired
    private PaymentTypeService paymentTypeService;

    @Override
    @Transactional
    public void onApplicationEvent(ContextRefreshedEvent event) {

        if (alreadySetup){
            return;
        }

        Privilege manageAllMembers = createPrivilegeIfNotFound("MANAGE_ALL_MEMBERS");
        Privilege manageOwnMembers = createPrivilegeIfNotFound("MANAGE_OWN_MEMBERS");
        Privilege manageUsers = createPrivilegeIfNotFound("MANAGE_USERS");

        Role adminRole = createRoleIfNotFound("ROLE_ADMIN", Arrays.asList(manageAllMembers, manageUsers));
        createRoleIfNotFound("ROLE_COACH", Arrays.asList(manageOwnMembers));

        try {
            createAppUserIfNotFound(adminEmail, adminPassword, Arrays.asList(adminRole));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        createPaymentMethodIfNotFound("CASH");
        createPaymentMethodIfNotFound("DEBIT");

        createPaymentTypeIfNotFound("MONTHLY_FEE");
        createPaymentTypeIfNotFound("STARTING_FEE");

        alreadySetup = true;
    }

    @Transactional
    Privilege createPrivilegeIfNotFound(String name) {

        Optional<Privilege> optionalPrivilege = privilegeService.findByName(name);
        Privilege privilege;
        if (optionalPrivilege.isEmpty()) {
            privilege = new Privilege(name);
            privilegeService.save(privilege);
        }else{
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
        }else{
            role = optionalRole.get();
        }
        return role;
    }

    @Transactional
    PaymentMethod createPaymentMethodIfNotFound(String name) {

        PaymentMethod paymentMethod;
        Optional<PaymentMethod> optionalPaymentMethod = paymentMethodService.findByName(name);

        if(optionalPaymentMethod.isEmpty()){
            paymentMethod = new PaymentMethod(name);
            paymentMethodService.save(paymentMethod);
        }else{
            paymentMethod =  optionalPaymentMethod.get();
        }

        return paymentMethod;
    }

    @Transactional
    AppUser createAppUserIfNotFound(String email, String password, Collection<Role> roles) throws InvalidPasswordException {

        AppUser appUser;
        Optional<AppUser> optionalAppUser = appUserService.findByEmail(email);

        if (optionalAppUser.isEmpty()) {
            appUser = new AppUser(email, password, roles);
            appUserService.save(appUser);
        }else{
            appUser = optionalAppUser.get();
        }

        return appUser;
    }

    @Transactional
    PaymentType createPaymentTypeIfNotFound(String name) {
        PaymentType paymentType;
        Optional<PaymentType> optionalPaymentType = paymentTypeService.findByName(name);

        if(optionalPaymentType.isEmpty()){
            paymentType = new PaymentType(name);
            paymentTypeService.save(paymentType);
        }else{
            paymentType = optionalPaymentType.get();
        }

        return paymentType;
    }


}
