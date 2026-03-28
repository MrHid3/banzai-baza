package pl.banzaijiujitsu.backend.service;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.banzaijiujitsu.backend.exception.InvalidEmailException;
import pl.banzaijiujitsu.backend.exception.InvalidPasswordException;
import pl.banzaijiujitsu.backend.model.Role;
import pl.banzaijiujitsu.backend.repository.AppUserRepository;
import pl.banzaijiujitsu.backend.model.AppUser;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class AppUserService {

    private final EncodingService encodingService;
    private final AppUserRepository appUserRepository;

    @Autowired
    public AppUserService(AppUserRepository appUserRepository, EncodingService encodingService) {
        this.appUserRepository = appUserRepository;
        this.encodingService = encodingService;
    }

    public List<AppUser> findByUsername(String username) {
        return appUserRepository.findByUsername(username);
    }

    public Optional<AppUser> findByEmail(String email) {
        return appUserRepository.findByEmail(email);
    }

    public Optional<AppUser> findByUuid(UUID uuid) {
        return appUserRepository.findByUuid(uuid);
    }

    public Optional<AppUser> findByPhoneNumber(String phooneNumber) {
        return appUserRepository.findByPhoneNumber(phooneNumber);
    }

    public AppUser save(AppUser appUser) throws InvalidEmailException, InvalidPasswordException {
        if(!appUserRepository.findByUsername(appUser.getEmail()).isEmpty()){
            throw new InvalidEmailException("Email already exists");
        }
        appUser.hashPassword(encodingService);
        return appUserRepository.save(appUser);
    }

    @Transactional
    public AppUser createPending(String email, Role role) {
        if (appUserRepository.existsByEmail(email)){
            throw new InvalidEmailException("Email already in use");
        }
        AppUser appUser = new AppUser();
        appUser.setEmail(email);
        appUser.setRole(role);
        appUser.setStatus(AppUser.AppUserStatus.PENDING);
        return appUserRepository.save(appUser);
    }

    @Transactional
    public void activateAppUser(AppUser appUser, String password) throws InvalidPasswordException {
        appUser.setPassword(password);
        appUser.hashPassword(encodingService);
        appUser.setStatus(AppUser.AppUserStatus.ACTIVE);
        appUserRepository.save(appUser);
    }
}
