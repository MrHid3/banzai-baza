package pl.banzaijiujitsu.backend.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import pl.banzaijiujitsu.backend.exception.InvalidEmailException;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Entity
@Getter
@Setter
public class Member {

    public Member(){}

    public Member(String email) {
        this.setEmail(email);
    }

    public Member(String email, String name, String surname, Location location) {
        this.setEmail(email);
        this.name = name;
        this.surname = surname;
        this.location = location;
    }

    public void setEmail(String email) throws InvalidEmailException {
        if(!(email == null || email.isEmpty())){
            Pattern pattern  = Pattern.compile("^[\\w\\-.]+@([\\w-]+\\.)+[\\w-]{2,}$");
            Matcher matcher = pattern.matcher(email);
            if(!matcher.find()){
                throw new InvalidEmailException("Invalid email format");
            }
        }

        this.email = email;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID uuid;

    private String email;
    private String name;
    private String surname;

    @Column(nullable = false)
    private String phoneNumber;

    @Column(nullable = false)
    private Integer monthlyFee;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Location location;

    private String comment;

    @Column(nullable = false)
    private Boolean isActive = true;

    @ManyToMany(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    private List<MemberCategory> categories = new ArrayList<MemberCategory>();

    @OneToMany(mappedBy = "payer")
    @JsonManagedReference
    private List<Payment> payments = new ArrayList<Payment>();
}
