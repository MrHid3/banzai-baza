package pl.banzaijiujitsu.backend.service;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Service;
import pl.banzaijiujitsu.backend.model.AppUser;
import pl.banzaijiujitsu.backend.model.MobileRefreshToken;
import pl.banzaijiujitsu.backend.model.RefreshToken;
import pl.banzaijiujitsu.backend.repository.MobileRefreshTokenRepository;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
public class MobileRefreshTokenService {

    @Value("${jwt.refresh-expiration}")
    private long refreshExpiration;

    @Autowired
    private MobileRefreshTokenRepository mobileRefreshTokenRepository;

    @Autowired
    private AppUserService appUserService;

    @Transactional
    public MobileRefreshToken createRefreshToken(String phoneNumber) {

        MobileRefreshToken refreshToken = new MobileRefreshToken();
        refreshToken.setPhoneNumber(phoneNumber);
        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken.setExpiryDate(Instant.now().plusMillis(refreshExpiration));

        return this.save(refreshToken);
    }

    public Optional<MobileRefreshToken> findValidToken(String token) {
        return mobileRefreshTokenRepository.findByToken(token)
                .filter(rt -> rt.getExpiryDate().isAfter(Instant.now()));
    }

    @Transactional
    @Modifying
    public void deleteByAppUser(String phoneNumber) {
        mobileRefreshTokenRepository.deleteByPhoneNumber(phoneNumber);
    }

    @Transactional
    @Modifying
    public void deleteByToken(String token) {
        mobileRefreshTokenRepository.deleteByToken(token);
    }

    public MobileRefreshToken save(MobileRefreshToken refreshToken) {
        return mobileRefreshTokenRepository.save(refreshToken);
    }
}
