package pl.banzaijiujitsu.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.banzaijiujitsu.backend.model.Localization;
import pl.banzaijiujitsu.backend.model.Member;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface MemberRepository extends JpaRepository<Member, UUID> {

    Optional<Member> findByUuid(UUID uuid);

    List<Member> findByEmail(String email);

    List<Member> findByLocalization(Localization localization);
}
