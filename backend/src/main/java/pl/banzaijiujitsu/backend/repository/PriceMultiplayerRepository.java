package pl.banzaijiujitsu.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.banzaijiujitsu.backend.model.Location;
import pl.banzaijiujitsu.backend.model.PriceMultiplier;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface PriceMultiplayerRepository extends JpaRepository<PriceMultiplier, Long> {

    List<PriceMultiplier> findByLocationIsInAndMonthGreaterThanEqualAndMonthLessThanEqualOrderById(
            List<Location> locations,
            LocalDate from,
            LocalDate to
    );

    Optional<PriceMultiplier> findByLocationAndMonth(Location location, LocalDate month);
}
