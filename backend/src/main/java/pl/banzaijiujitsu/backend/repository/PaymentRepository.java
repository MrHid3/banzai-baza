package pl.banzaijiujitsu.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.banzaijiujitsu.backend.model.Localization;
import pl.banzaijiujitsu.backend.model.Payment;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, UUID> {

    Optional<Payment> findByUuid(UUID uuid);

    List<Payment> findByPaymentDate(Date date);

    List<Payment> findByPayerLocalization(Localization localization);

    List<Payment> findByPayerUuid(UUID uuid);

    List<Payment> findByPayerInUuid(UUID uuid);
}
