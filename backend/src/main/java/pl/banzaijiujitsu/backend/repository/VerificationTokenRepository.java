package pl.banzaijiujitsu.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.banzaijiujitsu.backend.model.AppUser;
import pl.banzaijiujitsu.backend.model.VerificationToken;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface VerificationTokenRepository extends JpaRepository<VerificationToken, UUID> {

    Optional<VerificationToken> findByToken(String token);

    void deleteByAppUserAndType(AppUser user, VerificationToken.TokenType type);

    void deleteByExpiresAtBeforeAndUsedFalse(LocalDateTime cutoff);
}
