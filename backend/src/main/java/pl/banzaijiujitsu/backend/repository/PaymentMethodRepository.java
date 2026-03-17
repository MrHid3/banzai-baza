package pl.banzaijiujitsu.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.banzaijiujitsu.backend.model.PaymentMethod;

import java.util.Optional;

@Repository
public interface PaymentMethodRepository extends JpaRepository<PaymentMethod, Long> {

    Optional<PaymentMethod> findById(Long id);

    Optional<PaymentMethod> findByName(String name);
}
