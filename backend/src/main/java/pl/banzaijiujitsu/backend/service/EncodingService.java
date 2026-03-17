package pl.banzaijiujitsu.backend.service;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class EncodingService {

    private final PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    public String encodePassword(String password){
        return this.passwordEncoder().encode(password);
    }

    public boolean passwordMatches(String rawPassword, String encodedPassword){
        return this.passwordEncoder().matches(rawPassword, encodedPassword);
    }
}
