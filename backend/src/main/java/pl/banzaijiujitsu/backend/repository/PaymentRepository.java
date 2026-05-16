package pl.banzaijiujitsu.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pl.banzaijiujitsu.backend.model.Location;
import pl.banzaijiujitsu.backend.model.Member;
import pl.banzaijiujitsu.backend.model.Payment;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, UUID> {

    Optional<Payment> findByUuid(UUID uuid);

    Optional<Payment> findByMonthIsAndPayer(LocalDate date, Member member);

    List<Payment> findByPayerLocation(Location location);

    List<Payment> findByPayerLocationIsIn(List<Location> locations);

    List<Payment> findByPayerUuid(UUID uuid);

    List<Payment> findByPayerInUuid(UUID uuid);

    List<Payment> findByMonthAfterAndPayerLocationIsIn(LocalDate date, List<Location> locations);

    @Query("SELECT p FROM Payment p WHERE p.payer IN :members AND (p.month >= :threeMonthsAgo OR p.month IS NULL)")
    List<Payment> findRecentByMembers(@Param("members") List<Member> members, @Param("threeMonthsAgo") LocalDate threeMonthsAgo);
}
