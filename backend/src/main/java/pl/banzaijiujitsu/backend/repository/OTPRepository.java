package pl.banzaijiujitsu.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.banzaijiujitsu.backend.model.OTP;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface OTPRepository extends JpaRepository<OTP, UUID> {

    Optional<OTP> findByCode(String code);

    Optional<OTP> findByPhoneNumber(String phoneNumber);

    void deleteByCode(String code);

    void deleteByPhoneNumber(String phoneNumber);
}
