package pl.banzaijiujitsu.backend.controller;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import pl.banzaijiujitsu.backend.exception.*;
import pl.banzaijiujitsu.backend.model.Location;
import pl.banzaijiujitsu.backend.model.Member;
import pl.banzaijiujitsu.backend.model.MemberCategory;
import pl.banzaijiujitsu.backend.service.*;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/member")
public class MemberController {

    @Autowired
    private MemberService memberService;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private AppUserService appUserService;

    @Autowired
    private LocationService locationService;
    @Autowired
    private MemberCategoryService memberCategoryService;

    @Autowired
    private SmsController smsController;

    @PostMapping
    public ResponseEntity<String> addMember(@RequestBody CreateMemberRequest memberRequest, HttpServletResponse response) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        try {

            assert auth != null;
            Collection<Location> allowed_locations = appUserService
                    .findByEmail(auth.getName())
                    .orElseThrow(InvalidUuidException::new)
                    .getLocations();

            Location memberLocation = locationService.findByIdAndIsActive(
                            memberRequest.locationId())
                    .orElseThrow(InvalidLocationException::new);

            if (allowed_locations.contains(memberLocation)) {
                throw new InvalidLocationException();
            }

            List<MemberCategory> memberCategories = memberCategoryService.findAllByIds(memberRequest.categories() == null ? Collections.emptyList() : memberRequest.categories());
            Member member = new Member(memberRequest.email());
            member.setName(memberRequest.name());
            member.setSurname(memberRequest.surname());
            member.setLocation(memberLocation);
            member.setComment(memberRequest.comment());
            member.setMonthlyFee(memberRequest.monthlyFee());
            member.setPhoneNumber(memberRequest.phoneNumber());
            member.setCategories(memberCategories);
            member.setCreatedAt(LocalDateTime.now());

            memberService.save(member);
        } catch (InvalidLocationException | InvalidEmailException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

        return ResponseEntity.accepted().build();
    }

    //TODO: sprawdź czy to działa na użytkowników bez i z mniejszą niż wszystkie liczbą lokalizacji
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
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

    @DeleteMapping
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
            throw new InvalidLocationException("NO_ACCESS_TO_LOCATION");
        }

        return ResponseEntity.ok().build();
    }

    @PostMapping("/undelete")
    @Transactional
    public ResponseEntity<?> undeleteMember(@RequestBody DeleteMemberRequest memberRequest) {

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
            member.setIsActive(true);
            memberService.save(member);
        } else {
            throw new InvalidLocationException("NO_ACCESS_TO_LOCATION");
        }

        return ResponseEntity.ok().build();
    }

    @PutMapping("/{uuid}")
    @Transactional
    public ResponseEntity<?> updateMember(@PathVariable String uuid, @RequestBody UpdateMemberRequest req) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        Member member = memberService.findByUuid(UUID.fromString(uuid)).orElseThrow(InvalidUuidException::new);

        Collection<Location> allowed_locations;
        if (auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
            allowed_locations = locationService.findAll();
        } else {
            allowed_locations = appUserService
                    .findByEmail(auth.getName())
                    .orElseThrow(InvalidUuidException::new)
                    .getLocations();
        }
        Location requestLocation = locationService.findById(req.locationId).orElseThrow(() -> new LocationException("LOCATION_NOT_FOUND"));
        if(!allowed_locations.contains(requestLocation)) {
            throw new LocationException("NO_ACCESS_TO_LOCATION");
        }
        List<MemberCategory> memberCategories = memberCategoryService.findAllByIds(req.categories() == null ? Collections.emptyList() : req.categories());

        memberService.update(member, req.name, req.surname, req.email, requestLocation, req.monthlyFee, req.phoneNumber, memberCategories, req.comment);

        return ResponseEntity.ok().build();
    }

    public record CreateMemberRequest(
            @NotNull Long locationId,
            @Email String email,
            String name,
            String surname,
            String comment,
            @NotNull Integer monthlyFee,
            @NotNull @NotEmpty String phoneNumber,
            List<Long> categories
    ) {
    }

    public record UpdateMemberRequest(
            @Email String email,
            @NotNull @NotEmpty Long locationId,
            String name,
            String surname,
            String comment,
            @NotNull Integer monthlyFee,
            @NotNull @NotEmpty String phoneNumber,
            List<Long> categories
    ) {
    }

    public record DeleteMemberRequest(
            @NotNull String uuid
    ) {
    }
}