package pl.banzaijiujitsu.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.banzaijiujitsu.backend.model.Location;

import java.util.List;
import java.util.Optional;

@Repository
public interface LocationRepository extends JpaRepository<Location, Long> {

    @Override
    Optional<Location> findById(Long id);

    Optional<Location> findByIdAndIsActive(Long id, Boolean isActive);

    List<Location> findAllByName(String name);

    List<Location> findAll();

    List<Location> findByIsActive(Boolean isActive);

    void deleteById(Long id);
}
