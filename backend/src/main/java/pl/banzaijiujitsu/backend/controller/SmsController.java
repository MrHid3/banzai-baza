package pl.banzaijiujitsu.backend.controller;

import jakarta.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.TaskScheduler;
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

import java.time.*;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

@RestController
@RequestMapping("/api/sms")
public class SmsController {

    private final SmsService smsService;

    private final PaymentService paymentService;
    private final MemberService memberService;

    private final TaskScheduler scheduler;

    public SmsController(SmsService smsService, PaymentService paymentService, MemberService memberService, TaskScheduler scheduler) {
        this.smsService = smsService;
        this.paymentService = paymentService;
        this.memberService = memberService;
        this.scheduler = scheduler;
    }

    @PostMapping("/send")
    public ResponseEntity<?> send(@RequestBody SendSmsRequest req){
        List<UUID> uuids = req.memberUuids.stream().map(UUID::fromString).toList();
        if(req.scheduleDate == null || req.scheduleTime == null){
            smsService.sendSmsToMany(memberService.findByUuidsIn(uuids), req.message);
        } else {
            System.out.println("scheduled");
            scheduler.schedule(() -> {smsService.sendSmsToMany(memberService.findByUuidsIn(uuids), req.message);},
                    Instant.from(LocalDateTime.of(LocalDate.parse(req.scheduleDate), LocalTime.parse(req.scheduleTime)).atZone(ZoneId.of("Europe/Warsaw"))));
        }
        return ResponseEntity.ok().build();
    }

    public record SendSmsRequest(
            @NotNull List<String> memberUuids,
            @NotNull String message,
            String scheduleDate,
            String scheduleTime
    ){}
}
