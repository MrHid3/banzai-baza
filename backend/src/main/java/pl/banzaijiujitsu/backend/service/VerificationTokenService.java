package pl.banzaijiujitsu.backend.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import pl.banzaijiujitsu.backend.exception.InvalidTokenException;
import pl.banzaijiujitsu.backend.model.AppUser;
import pl.banzaijiujitsu.backend.model.VerificationToken;
import pl.banzaijiujitsu.backend.repository.VerificationTokenRepository;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class VerificationTokenService {

    private final VerificationTokenRepository verificationTokenRepository;

    @Value("${app.magic-link.expiry-hours}")
    private int expiryHours;

    public VerificationToken createToken(AppUser user, VerificationToken.TokenType type){

        verificationTokenRepository.deleteByAppUserAndType(user, type);

        VerificationToken token = new VerificationToken();
        token.setToken(UUID.randomUUID().toString());
        token.setAppUser(user);
        token.setType(type);
        token.setExpiresAt(LocalDateTime.now().plusHours(expiryHours));
        return verificationTokenRepository.save(token);
    }

    public VerificationToken validateToken(String raw, VerificationToken.TokenType expectedType){
        VerificationToken token = verificationTokenRepository.findByToken(raw)
                .orElseThrow(InvalidTokenException::new);

        if(token.getType() != expectedType){
            throw new InvalidTokenException("INVALID_TOKEN_TYPE");
        }
        if (token.getUsed()){
            throw new InvalidTokenException("TOKEN_ALREADY_USED");
        }
        if(token.getExpiresAt().isBefore(LocalDateTime.now())){
            throw new InvalidTokenException("TOKEN_EXPIRED");
        }
        return token;
    }

    public void consumeToken(VerificationToken token){
        token.setUsed(true);
        verificationTokenRepository.save(token);
    }

    @Scheduled(cron = "0 0 3 * * *")
    @Transactional
    public void purgeExpiredToken() {
        verificationTokenRepository.deleteByExpiresAtBeforeAndUsedFalse(LocalDateTime.now());
    }
}
