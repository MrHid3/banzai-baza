package pl.banzaijiujitsu.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.banzaijiujitsu.backend.model.PaymentType;

import java.util.Optional;

@Repository
public interface PaymentTypeRepository extends JpaRepository<PaymentType, Long> {

    Optional<PaymentType> findByName(String name);

    Optional<PaymentType> findById(Long id);
}
