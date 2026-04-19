package pl.banzaijiujitsu.backend.controller;

import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import pl.banzaijiujitsu.backend.exception.InvalidLocationException;
import pl.banzaijiujitsu.backend.exception.InvalidUuidException;
import pl.banzaijiujitsu.backend.model.Location;
import pl.banzaijiujitsu.backend.model.Member;
import pl.banzaijiujitsu.backend.model.PriceMultiplier;
import pl.banzaijiujitsu.backend.service.AppUserService;
import pl.banzaijiujitsu.backend.service.LocationService;
import pl.banzaijiujitsu.backend.service.PriceMultiplierService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.TemporalAccessor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/multiplier")
public class PriceMultiplierController {

    @Autowired
    private PriceMultiplierService priceMultiplierService;

    @Autowired
    private LocationService locationService;

    @Autowired
    private AppUserService appUserService;

    @PostMapping
    public ResponseEntity<?> add(@RequestBody CreateMultiplierRequest req) {

        LocalDate date = LocalDate.of(req.year, req.month, 11);
        Location location = locationService.findById(req.locationId).orElseThrow(InvalidLocationException::new);

        PriceMultiplier pm = priceMultiplierService.findByLocationAndMonth(location, date).orElse(new PriceMultiplier());

        pm.setMonth(date);
        pm.setLocation(location);
        pm.setMultiplier(req.multiplier);

        priceMultiplierService.save(pm);

        return ResponseEntity.ok().build();
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> get() {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        Collection<PriceMultiplier> pms = Collections.emptyList();
        if (auth == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        if (auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
            pms = priceMultiplierService.findByLocationIsIn(locationService.findAll());
        } else {
            List<Location> allowed_locations = new ArrayList<>(appUserService
                    .findByEmail(auth.getName())
                    .orElseThrow(InvalidUuidException::new)
                    .getLocations());

            pms = priceMultiplierService.findByLocationIsIn(allowed_locations);
        }

        return ResponseEntity.ok(pms);
    }

    public record CreateMultiplierRequest(
            @NotNull Integer year,
            @NotNull Integer month,
            @NotNull Long locationId,
            @NotNull BigDecimal multiplier
    ) {
    }
}
