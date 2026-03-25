package pl.banzaijiujitsu.backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
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
    private Integer amount;

    private PaymentMethod paymentMethod;

    private PaymentType paymentType;

    @JoinColumn(nullable = false)
    private Date paymentDate;

    @ManyToOne
    @JoinColumn(nullable = false)
    private AppUser payerIn;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Member payer;

    @Column(nullable = false)
    private boolean amountOverwritten;

    private String comment;
}
