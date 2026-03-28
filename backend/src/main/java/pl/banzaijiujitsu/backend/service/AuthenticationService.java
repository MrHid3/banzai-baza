package pl.banzaijiujitsu.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import pl.banzaijiujitsu.backend.exception.InvalidEmailException;
import pl.banzaijiujitsu.backend.model.AppUser;
import pl.banzaijiujitsu.backend.model.Role;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
public class AuthenticationService {

    @Autowired
    private AppUserService appUserService;

    public Optional<User> loadUserByEmail(String email) throws InvalidEmailException {
        Optional<AppUser> optionalAppUser = this.appUserService.findByEmail(email);
        if(optionalAppUser.isEmpty()){
            return Optional.empty();
        }
        AppUser appUser = optionalAppUser.get();
        return Optional.of(new User(appUser.getEmail(), appUser.getPassword(), getGrantedAuthorities(appUser.getRole())));
    }

    private List<GrantedAuthority> getGrantedAuthorities(List<String> privileges){
        List<GrantedAuthority> authorities = new ArrayList<>();
        for (String privilege : privileges) {
            authorities.add(new SimpleGrantedAuthority(privilege));
        }
        return authorities;
    }

    private List<GrantedAuthority> getGrantedAuthorities(Role role){
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(role.getName()));

        return authorities;
    }
}
