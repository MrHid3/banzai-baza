package pl.banzaijiujitsu.backend.controller;

import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.banzaijiujitsu.backend.exception.OTPException;
import pl.banzaijiujitsu.backend.model.OTP;
import pl.banzaijiujitsu.backend.service.OTPService;
import pl.banzaijiujitsu.backend.service.SmsService;

@RestController
@RequestMapping("/api/mobile/otp")
public class OTPController {

    private final OTPService otpService;
    private final SmsService smsService;

    @Autowired
    public OTPController(OTPService otpService, OTPService otpService1, SmsService smsService) {
        this.otpService = otpService1;
        this.smsService = smsService;
    }

    @PostMapping
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

    public record requestOTP(
            @NotNull String phoneNumber
    ){}
}
