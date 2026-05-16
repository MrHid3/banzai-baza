package pl.banzaijiujitsu.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.banzaijiujitsu.backend.model.MemberCategory;

import java.util.List;
import java.util.Optional;

@Repository
public interface MemberCategoryRepository extends JpaRepository<MemberCategory, Long> {

    Optional<MemberCategory> findById(Long id);

    Optional<MemberCategory> findByName(String name);

    Optional<MemberCategory> findByShortname(String shortname);

    List<MemberCategory> findAllByIdIsIn(List<Long> id);
}
