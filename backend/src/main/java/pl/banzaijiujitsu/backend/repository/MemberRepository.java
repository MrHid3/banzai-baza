package pl.banzaijiujitsu.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pl.banzaijiujitsu.backend.model.Location;
import pl.banzaijiujitsu.backend.model.Member;

import java.sql.Date;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface MemberRepository extends JpaRepository<Member, UUID> {

    Optional<Member> findByUuid(UUID uuid);

    List<Member> findByEmail(String email);

    List<Member> findByLocation(Location location);

    Collection<Member> findByLocationIsIn(Collection<Location> locations);

    Collection<Member> findByIsActiveTrueAndLocationIsIn(Collection<Location> locations);

    Collection<Member> findAllByIsActiveTrue();

//    @Query("""
//    SELECT DISTINCT m FROM Member m
//    LEFT JOIN FETCH m.payments p
//    WHERE m.isActive = true
//    AND m.location IN :locations
//    AND (p IS NULL OR p.month >= :threeMonthsAgo OR p.month IS NULL)
//    ORDER BY m.uuid
//""")
//    List<Member> findActiveMembersInLocationsWithRecentPayments(
//            @Param("locations") Collection<Location> locations,
//            @Param("threeMonthsAgo") LocalDate threeMonthsAgo
//    );

    @Query("SELECT m FROM Member m WHERE m.isActive = true AND m.location IN :locations ORDER BY m.uuid")
    List<Member> findActiveByLocations(@Param("locations") Collection<Location> locations);
}
