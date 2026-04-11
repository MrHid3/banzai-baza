package pl.banzaijiujitsu.backend.controller;

import io.micrometer.core.instrument.config.validate.Validated;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import pl.banzaijiujitsu.backend.exception.InvalidPaymentException;
import pl.banzaijiujitsu.backend.exception.InvalidUuidException;
import pl.banzaijiujitsu.backend.model.*;
import pl.banzaijiujitsu.backend.service.AppUserService;
import pl.banzaijiujitsu.backend.service.LocationService;
import pl.banzaijiujitsu.backend.service.MemberService;
import pl.banzaijiujitsu.backend.service.PaymentService;

import javax.naming.AuthenticationException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.YearMonth;
import java.time.temporal.TemporalAccessor;
import java.util.*;

@RestController
@RequestMapping("/api/payment")
public class PaymentController {

    @Autowired
    private final PaymentService paymentService;

    @Autowired
    private final MemberService memberService;

    @Autowired
    private AppUserService appUserService;

    @Autowired
    private LocationService locationService;

    public PaymentController(PaymentService paymentService, MemberService memberService) {
        this.paymentService = paymentService;
        this.memberService = memberService;
    }

    @GetMapping
    ResponseEntity<?> getAll() {
        return ResponseEntity.ok(paymentService.findAll());
    }

    @PostMapping
    ResponseEntity<?> add(@RequestBody AddPaymentRequest req) throws AuthenticationException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        AppUser appUser = appUserService.findByEmail(auth.getName())
                .orElseThrow(AuthenticationException::new);

        Member member = memberService.findByUuid(UUID.fromString(req.payerUuid)).
                orElseThrow(InvalidUuidException::new);

        if(!appUser.getLocations().contains(member.getLocation())){
            throw new AuthenticationException("NO_ACCESS_TO_MEMBER");
        }

        Payment payment = new Payment();

        if(req.year != null && req.month != null){
            YearMonth paymentMonth = YearMonth.of(req.year, req.month);
            Optional<Payment> optionalPayment = paymentService.findByMonthAndMember(paymentMonth, member);
            if(optionalPayment.isPresent()){
                payment = optionalPayment.get();
                if(paymentMonth.isBefore(YearMonth.now().minusMonths(3))){
                    throw new InvalidPaymentException("TOO_EARLY");
                }
                payment.setTime(YearMonth.of(req.year, req.month));
            }
        }

        payment.setAmount(req.amount);
        payment.setPaymentMethod(req.paymentMethod);
        payment.setPaymentType(req.paymentType);
        payment.setPayer(member);
        payment.setPayerIn(appUser);
        payment.setComment(req.comment);

        paymentService.save(payment);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/recent")
    ResponseEntity<?> getRecent(){

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Collection<Location> allowed_locations;
        if (auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
            allowed_locations = locationService.findAllActive();
        } else {
            allowed_locations = appUserService
                    .findByEmail(auth.getName())
                    .orElseThrow(InvalidUuidException::new)
                    .getLocations();
        }

        List list = new ArrayList<Location>(allowed_locations);

        return ResponseEntity.ok(paymentService.findByTimeAndLocations(YearMonth.now().minusMonths(3), list));
    }

    public record AddPaymentRequest(
            @NotNull BigDecimal amount,
            @NotNull PaymentMethod paymentMethod,
            @NotNull PaymentType paymentType,
            @NotNull String payerUuid,
            Integer year,
            Month month,
            String comment
    ){}
}
