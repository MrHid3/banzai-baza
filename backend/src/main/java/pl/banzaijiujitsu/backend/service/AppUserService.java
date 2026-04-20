package pl.banzaijiujitsu.backend.service;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.banzaijiujitsu.backend.exception.InvalidEmailException;
import pl.banzaijiujitsu.backend.exception.InvalidLocationException;
import pl.banzaijiujitsu.backend.exception.InvalidPasswordException;
import pl.banzaijiujitsu.backend.model.Location;
import pl.banzaijiujitsu.backend.model.Role;
import pl.banzaijiujitsu.backend.repository.AppUserRepository;
import pl.banzaijiujitsu.backend.model.AppUser;

import java.util.*;

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
        appUser.setPassword(password, encodingService);
        appUser.setStatus(AppUser.AppUserStatus.ACTIVE);
        appUserRepository.save(appUser);
    }

    @Transactional
    public void changePassword(AppUser appUser, String password) throws InvalidPasswordException {
        appUser.setPassword(password, encodingService);
        appUserRepository.save(appUser);
    }

    @Transactional
    public void deleteByUuid(UUID uuid) {
        appUserRepository.deleteByUuid(uuid);
    }

    @Transactional
    public void addLocation(AppUser appUser, Location location){
        if(appUser.getLocations().contains(location)){
            throw new InvalidLocationException("LOCATION_ALREADY_EXISTS");
        }

        Collection<Location> newLocationList = appUser.getLocations();
        newLocationList.add(location);

        appUser.setLocations(newLocationList);

        appUserRepository.save(appUser);
    }

    @Transactional
    public void deleteLocation(AppUser appUser, Location location){
        if(!appUser.getLocations().contains(location)){
            throw new InvalidLocationException("LOCATION_NOT_EXISTS");
        }

        Collection<Location> newLocationList = appUser.getLocations();
        newLocationList.remove(location);

        appUser.setLocations(newLocationList);

        appUserRepository.save(appUser);
    }

    public List<AppUser> findAll() {
        return appUserRepository.findAll();
    }

    public List<AppUser> findByLocationsContains(Location location) {
        return appUserRepository.findByLocationsContains(location);
    }
}
