package pl.banzaijiujitsu.backend.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pl.banzaijiujitsu.backend.exception.InvalidPasswordException;
import pl.banzaijiujitsu.backend.exception.InvalidRoleException;
import pl.banzaijiujitsu.backend.model.AppUser;
import pl.banzaijiujitsu.backend.model.VerificationToken;
import pl.banzaijiujitsu.backend.service.AppUserService;
import pl.banzaijiujitsu.backend.service.EmailService;
import pl.banzaijiujitsu.backend.service.RoleService;
import pl.banzaijiujitsu.backend.service.VerificationTokenService;

@RestController
@RequestMapping("/api/auth/registration")
@RequiredArgsConstructor
public class AppUserRegistrationController {

    private final AppUserService appUserService;
    private final EmailService emailService;
    private final VerificationTokenService verificationTokenService;
    private final RoleService roleService;

    @PostMapping("/invite")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> invite (@RequestBody @Valid InviteRequest inviteRequest){
        AppUser appUser = appUserService.createPending(inviteRequest.email(), roleService.findByName(inviteRequest.role()).orElseThrow(
                InvalidRoleException::new
        ));
        VerificationToken token = verificationTokenService.createToken(appUser, VerificationToken.TokenType.REGISTRATION);
        emailService.sendMagicLink(appUser, token.getToken());
        return ResponseEntity.accepted().build();
    }

    @GetMapping("/validate")
    public ResponseEntity<ValidateResponse> validate(@RequestParam String token) {
        VerificationToken vt = verificationTokenService.validateToken(token, VerificationToken.TokenType.REGISTRATION);
        return ResponseEntity.ok(new ValidateResponse(vt.getAppUser().getEmail()));
    }

    @PostMapping("/confirm")
    public ResponseEntity<ValidateResponse> confirm(@RequestBody @Valid ConfirmRequest confirmRequest) throws InvalidPasswordException {
        VerificationToken token = verificationTokenService.validateToken(confirmRequest.token(), VerificationToken.TokenType.REGISTRATION);
        appUserService.activateAppUser(token.getAppUser(), confirmRequest.password());
        verificationTokenService.consumeToken(token);
        return ResponseEntity.ok().build();
    }

    public record InviteRequest(
            @NotBlank @Email String email,
            @NotNull String role
    ){}

    public record ConfirmRequest(
            @NotBlank String token,
            @NotBlank @Size(min=8) String password
    ){}

    public record ValidateResponse(String email){}
}
