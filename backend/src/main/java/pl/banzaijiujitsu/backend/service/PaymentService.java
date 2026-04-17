package pl.banzaijiujitsu.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.banzaijiujitsu.backend.exception.InvalidPaymentException;
import pl.banzaijiujitsu.backend.model.Location;
import pl.banzaijiujitsu.backend.model.Payment;
import pl.banzaijiujitsu.backend.repository.PaymentRepository;
import pl.banzaijiujitsu.backend.model.Member;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

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

    public List<Payment> findByPayerLocation(Location location) {
        return  paymentRepository.findByPayerLocation(location);
    }

    public Optional<Payment> findByMonthAndMember(LocalDate date, Member member) {
        return paymentRepository.findByMonthIsAndPayer(date, member);
    }

    public List<Payment> findByPayerInUuid(UUID uuid) {
        return paymentRepository.findByPayerInUuid(uuid);
    }

    public List<Payment> findByPayerUuid(UUID uuid) {
        return paymentRepository.findByPayerUuid(uuid);
    }

    public Payment save(Payment payment) throws InvalidPaymentException {
        payment.setTimeStamp(LocalDateTime.now());
        return paymentRepository.save(payment);
    }

    public List<Payment> findByTimeAndLocations(LocalDate date, List<Location> locations){
        return paymentRepository.findByMonthAfterAndPayerLocationIsIn(date, locations);
    }

    public void delete(Payment payment){
        paymentRepository.delete(payment);
    }
}
