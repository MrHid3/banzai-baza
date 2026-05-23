package pl.banzaijiujitsu.backend.controller;

import jakarta.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.banzaijiujitsu.backend.model.AppUser;
import pl.banzaijiujitsu.backend.model.Member;
import pl.banzaijiujitsu.backend.model.Payment;
import pl.banzaijiujitsu.backend.service.MemberService;
import pl.banzaijiujitsu.backend.service.PaymentService;
import pl.banzaijiujitsu.backend.service.SmsService;

import java.time.LocalDate;
import java.time.Month;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/sms")
public class SmsController {

    private final SmsService smsService;

    private final PaymentService paymentService;
    private final MemberService memberService;

    public SmsController(SmsService smsService, PaymentService paymentService, MemberService memberService) {
        this.smsService = smsService;
        this.paymentService = paymentService;
        this.memberService = memberService;
    }

    @PostMapping("/send")
    public ResponseEntity<?> send(@RequestBody SendSmsRequest req){
        List<UUID> uuids = req.memberUuids.stream().map(UUID::fromString).toList();
        smsService.sendSmsToMany(memberService.findByUuidsIn(uuids), req.message);
        return ResponseEntity.ok().build();
    }

    public record SendSmsRequest(
            @NotNull List<String> memberUuids,
            @NotNull String message
    ){}
}
