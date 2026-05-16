package pl.banzaijiujitsu.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.banzaijiujitsu.backend.exception.InvalidLocationException;
import pl.banzaijiujitsu.backend.model.Location;
import pl.banzaijiujitsu.backend.repository.LocationRepository;

import java.util.List;
import java.util.Optional;

@Service
public class LocationService {

    private final LocationRepository locationRepository;

    @Autowired
    public LocationService(LocationRepository locationRepository) {
        this.locationRepository = locationRepository;
    }

    public Optional<Location> findById(Long id) {
        return locationRepository.findById(id);
    }

    public Optional<Location> findByName(String name) {
        return locationRepository.findByName(name);
    }

    public List<Location> findAll() {
        return locationRepository.findAll();
    }

    public Location save(Location location) {
        return locationRepository.save(location);
    }

    public void deleteById(Long id) {
        Location location = locationRepository.findById(id)
                .orElseThrow(InvalidLocationException::new);

        location.setIsActive(false);
    }

    public List<Location> findAllActive() {
        return locationRepository.findByIsActive(true);
    }

    public Optional<Location> findByIdAndIsActive(Long id) {
        return locationRepository.findByIdAndIsActive(id, true);
    }
}
