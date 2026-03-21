package pl.banzaijiujitsu.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.banzaijiujitsu.backend.model.Localization;

import java.util.List;
import java.util.Optional;

@Repository
public interface LocalizationRepository extends JpaRepository<Localization, Long> {

    @Override
    Optional<Localization> findById(Long id);

    Optional<Localization> findByName(String name);

    List<Localization> findAll();
}
