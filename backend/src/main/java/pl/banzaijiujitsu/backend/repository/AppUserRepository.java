package pl.banzaijiujitsu.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.banzaijiujitsu.backend.model.AppUser;
import pl.banzaijiujitsu.backend.model.Location;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AppUserRepository extends JpaRepository<AppUser, UUID> {

    List<AppUser> findByUsername(String username);

    Optional<AppUser> findByEmail(String email);

    Optional<AppUser> findByUuid(UUID uuid);

    Optional<AppUser> findByPhoneNumber(String phoneNumber);

    Boolean existsByEmail(String email);

    void deleteByUuid(UUID uuid);

    List<AppUser> findByLocationsContains(Location location);

    List<AppUser> findAllByOrderByEmail();
}
