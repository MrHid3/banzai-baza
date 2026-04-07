package pl.banzaijiujitsu.backend.controller;

import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import pl.banzaijiujitsu.backend.exception.InvalidUuidException;
import pl.banzaijiujitsu.backend.model.*;
import pl.banzaijiujitsu.backend.service.AppUserService;
import pl.banzaijiujitsu.backend.service.MemberService;
import pl.banzaijiujitsu.backend.service.PaymentService;

import javax.naming.AuthenticationException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/api/payment")
public class PaymentController {

    @Autowired
    private final PaymentService paymentService;

    @Autowired
    private final MemberService memberService;
    @Autowired
    private AppUserService appUserService;

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

        payment.setAmount(req.amount);
        payment.setPaymentMethod(req.paymentMethod);
        payment.setPaymentType(req.paymentType);
        payment.setPayer(member);
        payment.setPayerIn(appUser);
        payment.setComment(req.comment);

        paymentService.save(payment);

        return ResponseEntity.ok().build();
    }

    public record AddPaymentRequest(
            @NotNull BigDecimal amount,
            @NotNull PaymentMethod paymentMethod,
            @NotNull PaymentType paymentType,
            @NotNull String payerUuid,
            String comment
    ){}
}
