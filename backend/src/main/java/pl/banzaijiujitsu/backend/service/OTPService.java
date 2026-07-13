package pl.banzaijiujitsu.backend.service;

import jakarta.transaction.Transactional;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.banzaijiujitsu.backend.model.OTP;
import pl.banzaijiujitsu.backend.repository.OTPRepository;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class OTPService {

    private final OTPRepository OTPRepository;

    @Autowired
    public OTPService(OTPRepository OTPrepository) {
        this.OTPRepository = OTPrepository;
    }

    public Optional<OTP> findByCode(String code) {
        return OTPRepository.findByCode(code);
    }

    public Optional<OTP> findByPhoneNumber(String phoneNumber) {
        return OTPRepository.findByPhoneNumber(phoneNumber);
    }

    public void deleteByPhoneNumber(String phoneNumber) {
        OTPRepository.deleteByPhoneNumber(phoneNumber);
    }

    public OTP save(@NonNull OTP otp) {
        this.deleteByPhoneNumber(otp.getPhoneNumber());
        while (this.OTPRepository.findByCode(otp.getCode()).isPresent()) {
            otp.setCode(otp.generateCode());
        }
        return OTPRepository.save(otp);
    }

    @Transactional
    public OTP create(String phoneNumber) {
        return this.save(new OTP(phoneNumber));
    }

    public boolean verifyCode(String phoneNumber, String code) {
        Optional<OTP> optionalOTP = OTPRepository.findByCode(code);
        if (optionalOTP.isEmpty()) {
            return false;
        }
        OTP otp = optionalOTP.get();
        boolean verify = otp.getPhoneNumber().equals(phoneNumber) && LocalDateTime.now().isBefore(otp.getExpiresAt());
        if (verify) {
            OTPRepository.delete(otp);
        }
        return verify;
    }
}
