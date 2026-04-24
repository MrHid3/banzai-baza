package pl.banzaijiujitsu.backend.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import pl.banzaijiujitsu.backend.exception.*;
import pl.banzaijiujitsu.backend.model.*;
import pl.banzaijiujitsu.backend.repository.RoleRepository;
import pl.banzaijiujitsu.backend.service.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AppUserLoginController {

    private final AppUserService appUserService;
    private final CustomAuthenticationProvider customAuthenticationProvider;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final RoleRepository roleRepository;
    private final EmailService emailService;
    private final VerificationTokenService verificationTokenService;

    @Autowired
    public AppUserLoginController(AppUserService appUserService, CustomAuthenticationProvider customAuthenticationProvider, JwtService jwtService, RefreshTokenService refreshTokenService, RoleRepository roleRepository, EmailService emailService, VerificationTokenService verificationTokenService) {
        this.appUserService = appUserService;
        this.customAuthenticationProvider = customAuthenticationProvider;
        this.jwtService = jwtService;
        this.refreshTokenService = refreshTokenService;
        this.roleRepository = roleRepository;
        this.emailService = emailService;
        this.verificationTokenService = verificationTokenService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        Authentication authentication = customAuthenticationProvider.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.email(),
                        loginRequest.password()));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        AppUser appUser = appUserService.findByEmail(loginRequest.email())
                .orElseThrow(() -> new UsernameNotFoundException("USER_NOT_FOUND"));

        if(appUser.getStatus() != AppUser.AppUserStatus.ACTIVE){
            throw new UserNotActiveException();
        }

        String accessToken = jwtService.generateAccessToken(appUser);

        RefreshToken refreshToken = refreshTokenService.createRefreshToken(appUser);

        return ResponseEntity.ok(Map.of(
                "accessToken", accessToken,
                "refreshToken", refreshToken.getToken()
        ));
    }

    @PostMapping("/refresh")
    public ResponseEntity<String> refresh(
            @CookieValue(name = "refreshToken", required = false) String refreshToken,
            @RequestBody(required = false) RefreshRequest bodyRefreshToken,
            HttpServletResponse response) {

        String token = refreshToken;

        if(token == null){
            token = bodyRefreshToken.refreshToken();
        }

        if (token == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("NO_REFRESH_TOKEN");
        }

        return refreshTokenService.findValidToken(token)
                .map(rt -> {
                    String newAccessToken = jwtService.generateAccessToken(
                            rt.getAppUser());
                    return ResponseEntity.ok(newAccessToken);
                }).orElseThrow(InvalidRefreshTokenException::new);
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(@CookieValue(name = "refreshToken", required = false) String refreshToken, HttpServletResponse response) {

        if (refreshToken != null) {
            refreshTokenService.findValidToken(refreshToken)
                    .ifPresent(rt -> refreshTokenService.deleteByToken(
                            rt.getToken()));
        }

        Cookie expiredCookie = new Cookie("refreshToken", "");
        expiredCookie.setHttpOnly(true);
        expiredCookie.setSecure(true);
        expiredCookie.setPath("/api/auth/refresh");
        expiredCookie.setMaxAge(0);
        response.addCookie(expiredCookie);

        return ResponseEntity.ok("Logged out successfully");
    }

    @PostMapping("/reset-password/request")
    public ResponseEntity<String> resetPassword(@RequestBody ResetRequest req){

        AppUser appUser = appUserService.findByEmail(req.email).orElseThrow(() -> new UsernameNotFoundException("USER_NOT_FOUND"));

        VerificationToken token = verificationTokenService.createToken(appUser, VerificationToken.TokenType.PASSWORD_RESET);
        emailService.sendResetLink(appUser, token.getToken());
        return ResponseEntity.accepted().build();
    }

    @GetMapping("/reset-password/validate")
    public ResponseEntity<AppUserRegistrationController.ValidateResponse> validate(@RequestParam String token) {
        VerificationToken vt = verificationTokenService.validateToken(token, VerificationToken.TokenType.PASSWORD_RESET);
        return ResponseEntity.ok(new AppUserRegistrationController.ValidateResponse(vt.getAppUser().getEmail()));
    }

    @PostMapping("/reset-password/confirm")
    public ResponseEntity<AppUserRegistrationController.ValidateResponse> confirm(@RequestBody @Valid AppUserRegistrationController.ConfirmRequest confirmRequest) throws InvalidPasswordException {
        VerificationToken token = verificationTokenService.validateToken(confirmRequest.token(), VerificationToken.TokenType.PASSWORD_RESET);
        appUserService.changePassword(token.getAppUser(), confirmRequest.password());
        verificationTokenService.consumeToken(token);
        return ResponseEntity.ok().build();
    }

    public record LoginRequest(
            @NotNull @Email String email,
            @NotNull String password
    ) { }

    public record RefreshRequest(
            String refreshToken
    ){}

    public record ResetRequest(
            @NotNull @Email String email
    ){}
}
