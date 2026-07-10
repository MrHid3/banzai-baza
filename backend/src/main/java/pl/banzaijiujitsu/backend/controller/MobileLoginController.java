package pl.banzaijiujitsu.backend.controller;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.banzaijiujitsu.backend.exception.InvalidRefreshTokenException;
import pl.banzaijiujitsu.backend.exception.OTPException;
import pl.banzaijiujitsu.backend.model.MobileRefreshToken;
import pl.banzaijiujitsu.backend.model.OTP;
import pl.banzaijiujitsu.backend.service.JwtService;
import pl.banzaijiujitsu.backend.service.MobileRefreshTokenService;
import pl.banzaijiujitsu.backend.service.OTPService;
import pl.banzaijiujitsu.backend.service.SmsService;

import java.util.Map;

@RestController
@RequestMapping("/api/mobile/auth")
public class MobileLoginController {

    private final OTPService otpService;
    private final SmsService smsService;
    private final JwtService jwtService;
    private final MobileRefreshTokenService mobileRefreshTokenService;

    @Autowired
    public MobileLoginController(OTPService otpService, OTPService otpService1, SmsService smsService, JwtService jwtService, MobileRefreshTokenService mobileRefreshTokenService) {
        this.otpService = otpService1;
        this.smsService = smsService;
        this.jwtService = jwtService;
        this.mobileRefreshTokenService = mobileRefreshTokenService;
    }

    @PostMapping("/requestOTP")
    public ResponseEntity<?> createOtp(@RequestBody requestOTP r) {
        String pn = r.phoneNumber.replace(" ", "");
        if(!pn.startsWith("+")) {
            pn = "+48"+pn;
        }
        if(!pn.matches(".*[\\d+].*")){
            throw new OTPException("INVALID_PHONE_NUMBER");
        }
        OTP otp = this.otpService.create(r.phoneNumber);
        this.smsService.sendSms(pn, "Twój kod Banzai to " + otp.getCode() + ". Będzie ważny przez 15 minut");
        return ResponseEntity.ok().build();
    }

    @PostMapping("/verifyOTP")
    public ResponseEntity<?> verifyOtp(@RequestBody verifyOTPRequest r) {
        if(otpService.verifyCode(r.phoneNumber, r.code)){
            String accessToken = jwtService.generateMobileAccessToken(r.phoneNumber);
            MobileRefreshToken refreshToken = mobileRefreshTokenService.createRefreshToken(r.phoneNumber);
            return ResponseEntity.ok(Map.of(
                    "accessToken", accessToken,
                    "refreshToken", refreshToken.getToken()
            ));
        }
        throw new OTPException("INVALID_OTP");
    }

    @PostMapping("/refresh")
    public ResponseEntity<String> refresh(
            @CookieValue(name = "refreshToken", required = false) String refreshToken,
            @RequestBody(required = false) AppUserLoginController.RefreshRequest bodyRefreshToken,
            HttpServletResponse response) {

        String token = refreshToken;

        if (token == null) {
            token = bodyRefreshToken.refreshToken();
        }

        if (token == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("NO_REFRESH_TOKEN");
        }

        return mobileRefreshTokenService.findValidToken(token)
                .map(rt -> {
                    String newAccessToken = jwtService.generateMobileAccessToken(
                            rt.getPhoneNumber());
                    return ResponseEntity.ok(newAccessToken);
                }).orElseThrow(InvalidRefreshTokenException::new);
    }

    public record requestOTP(
            @NotNull String phoneNumber
    ){}

    public record verifyOTPRequest(
            @NotNull String phoneNumber,
            @NotNull String code
    ){}
}
