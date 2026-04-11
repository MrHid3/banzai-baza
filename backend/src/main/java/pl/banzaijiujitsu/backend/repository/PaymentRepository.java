package pl.banzaijiujitsu.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pl.banzaijiujitsu.backend.model.Location;
import pl.banzaijiujitsu.backend.model.Member;
import pl.banzaijiujitsu.backend.model.Payment;

import java.time.YearMonth;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, UUID> {

    Optional<Payment> findByUuid(UUID uuid);

    Optional<Payment> findByMonthIsAndPayer(YearMonth month, Member member);

    List<Payment> findByPayerLocation(Location location);

    List<Payment> findByPayerUuid(UUID uuid);

    List<Payment> findByPayerInUuid(UUID uuid);

    List<Payment> findByMonthAfterAndPayerLocationIsIn(YearMonth month, List<Location> locations);
}
