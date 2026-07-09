package pl.banzaijiujitsu.backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Random;
import java.util.UUID;

@Entity
@Getter
@Setter
public class OTP {

    public OTP() {
        this.code = this.generateCode();
        this.expiresAt = LocalDateTime.now().plusMinutes(15);
    }

    public OTP(String phoneNumber){
        this.phoneNumber = phoneNumber;
        this.code = this.generateCode();
        this.expiresAt = LocalDateTime.now().plusMinutes(15);
    }

    public String generateCode() {
        StringBuilder code = new StringBuilder();
        Random r = new Random();
        for (int i = 0; i < 6; i++) {
            code.append(r.nextInt(10));
        }
        return code.toString();
    }

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID uuid;

    @Column(nullable = false, unique = true)
    private String phoneNumber;

    @Column(nullable = false, unique = true)
    private String code;

    @Column(nullable = false)
    private LocalDateTime expiresAt;
}
