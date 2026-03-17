package pl.banzaijiujitsu.backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Getter
@Setter
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID uuid;

    @Column(nullable = false)
    private String email;
    private String name;
    private String surname;
    private String phoneNumber;
    private Integer monthlyFee;

    @ManyToOne
    private Localization localization;

}
