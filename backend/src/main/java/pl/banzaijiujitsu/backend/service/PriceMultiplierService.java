package pl.banzaijiujitsu.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.banzaijiujitsu.backend.model.Location;
import pl.banzaijiujitsu.backend.model.PriceMultiplier;
import pl.banzaijiujitsu.backend.repository.PriceMultiplayerRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class PriceMultiplierService {

    @Autowired
    private PriceMultiplayerRepository priceMultiplayerRepository;

    public List<PriceMultiplier> findByLocationIsIn(List<Location> locations) {
        return priceMultiplayerRepository.findByLocationIsInAndMonthGreaterThanEqualAndMonthLessThanEqual(
                locations,
                LocalDate.now().withMonth(1).withDayOfMonth(11),
                LocalDate.now().plusYears(1).withMonth(1).withDayOfMonth(11)
        );
    }

    public Optional<PriceMultiplier> findByLocationAndMonth(Location location, LocalDate month) {
        return priceMultiplayerRepository.findByLocationAndMonth(location, month);
    }

    public void save(PriceMultiplier pm) {
        priceMultiplayerRepository.save(pm);
    }
}
