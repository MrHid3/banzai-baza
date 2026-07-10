package pl.banzaijiujitsu.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.banzaijiujitsu.backend.model.AppUser;
import pl.banzaijiujitsu.backend.model.MobileRefreshToken;
import pl.banzaijiujitsu.backend.model.RefreshToken;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface MobileRefreshTokenRepository extends JpaRepository<MobileRefreshToken, String> {

    Optional<MobileRefreshToken> findByToken(String token);

    void deleteByPhoneNumber(String phoneNumber);

    void deleteByToken(String token);
}
