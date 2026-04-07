package pl.banzaijiujitsu.backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Date;
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

    @JoinColumn(nullable = false)
    private LocalDateTime paymentDate;

    @ManyToOne
    @JoinColumn(nullable = false)
    private AppUser payerIn;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Member payer;

    private String comment;
}
