package pl.banzaijiujitsu.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.banzaijiujitsu.backend.model.Localization;
import pl.banzaijiujitsu.backend.model.Payment;
import pl.banzaijiujitsu.backend.repository.PaymentRepository;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    public Optional<Payment> findByUuid(UUID uuid) {
        return paymentRepository.findByUuid(uuid);
    }

    public List<Payment> findAll() {
        return paymentRepository.findAll();
    }

    public List<Payment> findByPayerLocalization(Localization localization) {
        return  paymentRepository.findByPayerLocalization(localization);
    }

    public List<Payment> findByPaymentDate(Date date) {
        return paymentRepository.findByPaymentDate(date);
    }

    public List<Payment> findByPayerInUuid(UUID uuid) {
        return paymentRepository.findByPayerInUuid(uuid);
    }

    public List<Payment> findByPayerUuid(UUID uuid) {
        return paymentRepository.findByPayerUuid(uuid);
    }

    public Payment save(Payment payment) {
        return paymentRepository.save(payment);
    }
}
