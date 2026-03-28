package pl.banzaijiujitsu.backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import pl.banzaijiujitsu.backend.exception.InvalidEmailException;
import pl.banzaijiujitsu.backend.exception.InvalidPasswordException;
import pl.banzaijiujitsu.backend.service.EncodingService;

import java.util.Collection;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Entity
@Getter
@Setter
@ToString
@Table
public class AppUser {

    public AppUser(){}

    public AppUser(String email, String password) throws InvalidEmailException, InvalidPasswordException {
        this.setEmail(email);
        this.setPassword(password);
    }

    public AppUser(String email, String password, Role role) throws InvalidEmailException, InvalidPasswordException {
        this.setEmail(email);
        this.setPassword(password);
        this.setRole(role);
    }

    public enum AppUserStatus{
        PENDING, ACTIVE
    }

    public boolean isEnabled(){
        return this.status == AppUserStatus.ACTIVE;
    }

    public void setPassword(String password) throws InvalidPasswordException {
        Pattern pattern = Pattern.compile("^((?=\\S*?[A-Z])(?=\\S*?[a-z])(?=\\S*?[0-9])(?=\\S*?[?!\\\\|'\";:+=-_()*&^%$#@<>,.`~\\[\\]{}/]).{8,})\\S$");
        Matcher matcher = pattern.matcher(password);
        if(!matcher.find()){
            throw new InvalidPasswordException("Password is too easy");
        }
        this.password = password;
    }

    public void setEmail(String email) throws InvalidEmailException {
        Pattern pattern  = Pattern.compile("^[\\w\\-.]+@([\\w-]+\\.)+[\\w-]{2,}$");
        Matcher matcher = pattern.matcher(email);
        if(!matcher.find()){
            throw new InvalidEmailException();
        }
        this.email = email;
    }

    public void hashPassword(EncodingService encodingService) throws InvalidPasswordException {
        if(this.password == null || this.password.isEmpty()){
            throw new InvalidPasswordException("Password is empty");
        }
        this.password = encodingService.encodePassword(this.password);
    }

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID uuid;

    private String username;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(unique = true)
    private String phoneNumber;

    private String password;

    @ManyToOne(fetch =  FetchType.EAGER)
    private Role role;

    @ManyToMany
    private Collection<Location> locations;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AppUserStatus status = AppUserStatus.PENDING;
}
