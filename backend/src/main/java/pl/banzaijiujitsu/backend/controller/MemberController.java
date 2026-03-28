package pl.banzaijiujitsu.backend.controller;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import pl.banzaijiujitsu.backend.exception.InvalidEmailException;
import pl.banzaijiujitsu.backend.exception.InvalidUuidException;
import pl.banzaijiujitsu.backend.model.*;
import pl.banzaijiujitsu.backend.exception.InvalidLocationException;
import pl.banzaijiujitsu.backend.service.AppUserService;
import pl.banzaijiujitsu.backend.service.JwtService;
import pl.banzaijiujitsu.backend.service.LocationService;
import pl.banzaijiujitsu.backend.service.MemberService;

import java.util.*;

@RestController
public class MemberController {

    @Autowired
    private MemberService memberService;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private AppUserService appUserService;

    @Autowired
    private LocationService locationService;

    @PostMapping("/api/member")
    public ResponseEntity<String> addMember(@RequestBody CreateMemberRequest memberRequest, HttpServletResponse response) {

        System.out.println("asfsasf");
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        try {

            assert auth != null;
            Collection<Location> allowed_locations = appUserService
                    .findByEmail(auth.getName())
                    .orElseThrow(InvalidUuidException::new)
                    .getLocations();

            Location memberLocation = locationService.findById(
                            memberRequest.locationId())
                    .orElseThrow(() -> new InvalidLocationException("Invalid location id"));

            if (allowed_locations.contains(memberLocation)) {
                throw new InvalidLocationException("User doesn't have access to this location");
            }

            Member member = new Member(memberRequest.email());
            member.setName(memberRequest.name());
            member.setSurname(memberRequest.surname());
            member.setLocation(memberLocation);
            member.setComment(memberRequest.comment());
            member.setMonthlyFee(memberRequest.monthlyFee());
            member.setPhoneNumber(memberRequest.phoneNumber());

            memberService.save(member);
        } catch (InvalidLocationException | InvalidEmailException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

        return ResponseEntity.accepted().build();
    }

    //TODO: sprawdź czy to działa na użytkowników bez i z mniejszą niż wszystkie liczbą lokalizacji
    @GetMapping(path = "/api/member", produces = MediaType.APPLICATION_JSON_VALUE)
//    public ResponseEntity<Collection<Member>> member(){
    public ResponseEntity<?> member() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        Collection<Member> members = Collections.emptyList();
        if (auth == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        if (auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
            members = memberService.findAllByIsActiveTrue();
        } else {
            Collection<Location> allowed_locations = appUserService
                    .findByEmail(auth.getName())
                    .orElseThrow(InvalidUuidException::new)
                    .getLocations();

            members = memberService.findByIsActiveTrueAndLocationIsIn(allowed_locations);
        }
        return ResponseEntity.ok(members);
    }

    @DeleteMapping("/api/member")
    @Transactional
    public ResponseEntity<?> deleteMember(@RequestBody DeleteMemberRequest memberRequest) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        Member member = memberService.findByUuid(UUID.fromString(memberRequest.uuid())).orElseThrow(InvalidUuidException::new);

        Collection<Location> allowed_locations;
        if (auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
            allowed_locations = locationService.findAll();
        } else {
            allowed_locations = appUserService
                    .findByEmail(auth.getName())
                    .orElseThrow(InvalidUuidException::new)
                    .getLocations();
        }
        if (allowed_locations.contains(member.getLocation())) {
            member.setIsActive(false);
            memberService.save(member);
        } else {
            return ResponseEntity.badRequest().body("INVALID_LOCATION");
        }

        return ResponseEntity.ok().build();
    }

    public record CreateMemberRequest(
            @NotNull Long locationId,
            @NotNull @Email String email,
            String name,
            String surname,
            String comment,
            Integer monthlyFee,
            String phoneNumber
    ) {
    }

    public record DeleteMemberRequest(
            @NotNull String uuid
    ) {
    }
}