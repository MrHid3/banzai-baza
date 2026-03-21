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

    public AppUser(String email, String password, Collection<Role> roles) throws InvalidEmailException, InvalidPasswordException {
        this.setEmail(email);
        this.setPassword(password);
        this.setRoles(roles);
    }

    public void setPassword(String password) throws  InvalidPasswordException {
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

    @Column(nullable = false)
    private String password;

    @ManyToMany(fetch =  FetchType.EAGER)
    @JoinTable(
            name = "app_users_roles",
            joinColumns = @JoinColumn(
                    name = "app_user_uuid", referencedColumnName = "uuid"),
            inverseJoinColumns = @JoinColumn(
                    name = "role_id", referencedColumnName = "id"))
    private Collection<Role> roles;

    @ManyToMany
    private Collection<Localization> localizations;
}
