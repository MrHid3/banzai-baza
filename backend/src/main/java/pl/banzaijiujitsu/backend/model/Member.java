package pl.banzaijiujitsu.backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import pl.banzaijiujitsu.backend.exception.InvalidEmailException;

import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Entity
@Getter
@Setter
public class Member {

    public Member(){}

    public Member(String email, String name, String surname, Localization localization) {
        this.setEmail(email);
        this.name = name;
        this.surname = surname;
        this.localization = localization;
    }

    public void setEmail(String email) throws InvalidEmailException {
        if(email == null || email.isEmpty()){
            throw new InvalidEmailException("Email can't be empty");
        }

        Pattern pattern  = Pattern.compile("^[\\w\\-.]+@([\\w-]+\\.)+[\\w-]{2,}$");
        Matcher matcher = pattern.matcher(email);
        if(!matcher.find()){
            throw new InvalidEmailException("Invalid email format");
        }
        this.email = email;
    }

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
