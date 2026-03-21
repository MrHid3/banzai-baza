package pl.banzaijiujitsu.backend.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import pl.banzaijiujitsu.backend.exception.InvalidEmailException;
import pl.banzaijiujitsu.backend.exception.InvalidUuidException;
import pl.banzaijiujitsu.backend.model.AppUser;
import pl.banzaijiujitsu.backend.model.Localization;
import pl.banzaijiujitsu.backend.model.Member;
import pl.banzaijiujitsu.backend.model.MemberRequest;
import pl.banzaijiujitsu.backend.repository.InvalidLocalizationException;
import pl.banzaijiujitsu.backend.service.AppUserService;
import pl.banzaijiujitsu.backend.service.JwtService;
import pl.banzaijiujitsu.backend.service.LocalizationService;
import pl.banzaijiujitsu.backend.service.MemberService;

import java.util.Collection;
import java.util.List;
import java.util.Map;
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
    private LocalizationService localizationService;

    @PostMapping("/add")
    public ResponseEntity<String> addMember(@RequestBody MemberRequest memberRequest, HttpServletResponse response){

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        try {

            assert auth != null;
            Collection<Localization> allowed_localizations = appUserService
                    .findByEmail(auth.getName())
                    .orElseThrow(InvalidUuidException::new)
                    .getLocalizations();

            Localization memberLocalization = localizationService.findById(
                            memberRequest.getLocalization_id())
                    .orElseThrow(() -> new InvalidLocalizationException("Invalid localization id"));

            if (allowed_localizations.contains(memberLocalization)) {
                throw new InvalidLocalizationException("User doesn't have access to this localization");
            }

            Member member = new Member(memberRequest.getEmail(), memberRequest.getName(), memberRequest.getSurname(), memberLocalization);

            memberService.save(member);
        }catch (InvalidLocalizationException | InvalidEmailException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }

        return ResponseEntity.ok("Member added");
    }

    @GetMapping("/all")
    public ResponseEntity<List<Member>> member(HttpServletResponse response){

        return ResponseEntity.ok(memberService.findAll());
    }
}
