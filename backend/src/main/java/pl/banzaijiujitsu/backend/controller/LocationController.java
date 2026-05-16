package pl.banzaijiujitsu.backend.controller;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import pl.banzaijiujitsu.backend.exception.InvalidLocationException;
import pl.banzaijiujitsu.backend.exception.InvalidUuidException;
import pl.banzaijiujitsu.backend.model.AppUser;
import pl.banzaijiujitsu.backend.model.Location;
import pl.banzaijiujitsu.backend.model.Member;
import pl.banzaijiujitsu.backend.service.AppUserService;
import pl.banzaijiujitsu.backend.service.LocationService;
import pl.banzaijiujitsu.backend.service.MemberService;

import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/api/location")
public class LocationController {

    @Autowired
    private final LocationService locationService;

    @Autowired
    private final AppUserService appUserService;

    @Autowired
    private final MemberService memberService;

    @Autowired
    public LocationController(LocationService locationService, AppUserService appUserService, MemberService memberService) {
        this.locationService = locationService;
        this.appUserService = appUserService;
        this.memberService = memberService;
    }

    @GetMapping(value = "/all", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<Collection<Location>> getAll(HttpServletResponse httpServletResponse) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Collection<Location> allowed_locations;
        if (auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
            allowed_locations = locationService.findAllActive();
        } else {
            allowed_locations = appUserService
                    .findByEmail(auth.getName())
                    .orElseThrow(InvalidUuidException::new)
                    .getLocations();
        }

        return ResponseEntity.ok(allowed_locations);
    }

    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Transactional
    public ResponseEntity<?> addLocation(@RequestBody LocationCreationRequest req) {

        Location location = new Location(req.name, req.shortname);

        locationService.save(location);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Transactional
    public ResponseEntity<?> deleteLocation(@RequestParam Long locationId) {

        Location location = locationService.findByIdAndIsActive(locationId).orElseThrow(InvalidLocationException::new);
        List<Member> member = memberService.findByLocation(location);

        if (!member.isEmpty()) {
            throw new InvalidLocationException("MEMBER_HAS_LOCATION");
        }

        List<AppUser> appUsers = appUserService.findByLocationsContains(location);

        for (AppUser appUser : appUsers) {
            appUserService.deleteLocation(appUser, location);
        }

        locationService.deleteById(locationId);

        return ResponseEntity.ok().build();
    }

    public record LocationCreationRequest(
            @NotNull String name,
            @NotNull String shortname
    ) {
    }

    public record DeleteLocationRequest(
            @NotNull Long locationId
    ) {
    }
}
