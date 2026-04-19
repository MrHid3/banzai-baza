package pl.banzaijiujitsu.backend.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID uuid;

    @Column(nullable = false)
    private BigDecimal amount;

    private PaymentMethod paymentMethod;

    private PaymentType paymentType;

    private LocalDate month;

    @Column(nullable = false)
    private LocalDateTime timeStamp;

    @ManyToOne
    @JoinColumn(nullable = false)
    private AppUser payerIn;

    @ManyToOne
    @JoinColumn(nullable = false)
    @JsonBackReference
    private Member payer;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Location location;

//    private String comment;
}
