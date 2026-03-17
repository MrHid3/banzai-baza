package pl.banzaijiujitsu.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.banzaijiujitsu.backend.model.PaymentType;
import pl.banzaijiujitsu.backend.repository.PaymentTypeRepository;

import java.util.Optional;

@Service
public class PaymentTypeService {

    @Autowired
    private PaymentTypeRepository paymentTypeRepository;

    public Optional<PaymentType> findByName(String name) {
        return paymentTypeRepository.findByName(name);
    }

    public Optional<PaymentType> findById(Long id) {
        return paymentTypeRepository.findById(id);
    }

    public PaymentType save(PaymentType paymentType) {
        return paymentTypeRepository.save(paymentType);
    }
}
