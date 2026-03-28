package pl.banzaijiujitsu.backend.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import pl.banzaijiujitsu.backend.exception.*;
import pl.banzaijiujitsu.backend.model.*;
import pl.banzaijiujitsu.backend.repository.RoleRepository;
import pl.banzaijiujitsu.backend.service.AppUserService;
import pl.banzaijiujitsu.backend.service.CustomAuthenticationProvider;
import pl.banzaijiujitsu.backend.service.JwtService;
import pl.banzaijiujitsu.backend.service.RefreshTokenService;

import javax.management.relation.RoleNotFoundException;
import java.util.Arrays;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AppUserLoginController {

    private final AppUserService appUserService;
    private final CustomAuthenticationProvider customAuthenticationProvider;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final RoleRepository roleRepository;

    @Autowired
    public AppUserLoginController(AppUserService appUserService, CustomAuthenticationProvider customAuthenticationProvider, JwtService jwtService, RefreshTokenService refreshTokenService, RoleRepository roleRepository) {
        this.appUserService = appUserService;
        this.customAuthenticationProvider = customAuthenticationProvider;
        this.jwtService = jwtService;
        this.refreshTokenService = refreshTokenService;
        this.roleRepository = roleRepository;
    }

//    @PostMapping("/register")
//    public ResponseEntity<String> registration(@RequestBody RegisterRequest registerRequest){
//        try{
//            AppUser appUser = new AppUser(registerRequest.getEmail(), registerRequest.getPassword());
//            Role role = roleRepository.findByName(registerRequest.getRole())
//                    .orElseThrow(() -> new RoleNotFoundException("Invalid role"));
//            appUser.setRoles(Arrays.asList(role));
//            appUserService.save(appUser);
//            return ResponseEntity.ok("User registered succesfully");
//        }catch (RoleNotFoundException e){
//            return ResponseEntity.badRequest().body("Invalid role");
//        }catch (InvalidPasswordException e){
//            return ResponseEntity.badRequest().body("Invalid password");
//        }catch (InvalidEmailException e){
//            return ResponseEntity.badRequest().body("Invalid email");
//        }
//    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        Authentication authentication = customAuthenticationProvider.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.email(),
                        loginRequest.password()));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        AppUser appUser = appUserService.findByEmail(loginRequest.email())
                .orElseThrow(() -> new UsernameNotFoundException("USER_NOT_FOUND"));

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
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No refresh token");
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
                    .ifPresent(rt -> refreshTokenService.deleteByAppUser(
                            rt.getAppUser()));

        }

        Cookie expiredCookie = new Cookie("refreshToken", "");
        expiredCookie.setHttpOnly(true);
        expiredCookie.setSecure(true);
        expiredCookie.setPath("/api/auth/refresh");
        expiredCookie.setMaxAge(0);
        response.addCookie(expiredCookie);

        return ResponseEntity.ok("Logged out successfully");
    }

    public record LoginRequest(
            @NotNull @Email String email,
            @NotNull String password
    ) { }

    public record RefreshRequest(
            String refreshToken
    ){}
}
