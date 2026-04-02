package pl.banzaijiujitsu.backend.controller;

import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pl.banzaijiujitsu.backend.exception.InvalidLocationException;
import pl.banzaijiujitsu.backend.exception.InvalidUuidException;
import pl.banzaijiujitsu.backend.model.AppUser;
import pl.banzaijiujitsu.backend.model.Location;
import pl.banzaijiujitsu.backend.repository.AppUserRepository;
import pl.banzaijiujitsu.backend.service.AppUserService;
import pl.banzaijiujitsu.backend.service.LocationService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/appUser")
@PreAuthorize("hasRole('ADMIN')")
public class AppUserManagmentController{

    @Autowired
    private AppUserService appUserService;

    @Autowired
    private LocationService locationService;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<AppUser>> getAppUsers() {

        return ResponseEntity.ok(appUserService.findAll());
    }

    @DeleteMapping
    @Transactional
    public ResponseEntity<?> deleteAppUser(@RequestParam String uuid) {

        appUserService.deleteByUuid(UUID.fromString(uuid));

        return ResponseEntity.ok().build();
    }

    @PostMapping("/localization")
    @Transactional
    public ResponseEntity<?> addLocalization(@RequestParam LocationRequest req) {

        AppUser appUser = appUserService.findByUuid(UUID.fromString(req.userUuid())).
                orElseThrow(InvalidUuidException::new);

        Location location = locationService.findById(req.locationId()).
                orElseThrow(InvalidLocationException::new);

        appUserService.addLocation(appUser, location);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/localization")
    @Transactional
    public ResponseEntity<?> deleteLocalization(@RequestParam LocationRequest req) {


        AppUser appUser = appUserService.findByUuid(UUID.fromString(req.userUuid())).
                orElseThrow(InvalidUuidException::new);

        Location location = locationService.findById(req.locationId()).
                orElseThrow(InvalidLocationException::new);

        appUserService.deleteLocation(appUser, location);
        return ResponseEntity.ok().build();
    }

    public record LocationRequest(
            @NotNull String userUuid,
            @NotNull Long locationId
    ){}
}
