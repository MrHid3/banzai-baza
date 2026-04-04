package pl.banzaijiujitsu.backend.service;

import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import pl.banzaijiujitsu.backend.exception.InvalidEmailException;
import pl.banzaijiujitsu.backend.exception.InvalidUuidException;
import pl.banzaijiujitsu.backend.exception.UserNotActiveException;
import pl.banzaijiujitsu.backend.model.AppUser;
import pl.banzaijiujitsu.backend.model.Privilege;
import pl.banzaijiujitsu.backend.model.Role;

import java.util.*;

@Service
public class AppUserDetailsService implements UserDetailsService {

    final private AppUserService appUserService;

    @Autowired
    public AppUserDetailsService(AppUserService appUserService) {
        this.appUserService = appUserService;
    }

    @Override
    public @NonNull UserDetails loadUserByUsername(@NonNull String email) throws InvalidEmailException, UserNotActiveException {
        AppUser user = appUserService.findByEmail(email)
                .orElseThrow(() -> new InvalidEmailException("Email not found"));
        if (!user.isEnabled()) {
            throw new UserNotActiveException();
        }
        return new User(user.getEmail(), user.getPassword(), getGrantedAuthorities(user.getRole()));
    }

    public Optional<UserDetails> loadUserByUuid(UUID uuid) throws InvalidUuidException {
        AppUser user = appUserService.findByUuid(uuid)
                .orElseThrow(InvalidUuidException::new);
        if (!user.isEnabled()) {
            throw new UserNotActiveException();
        }
        return Optional.of(new User(user.getEmail(), user.getPassword(), getGrantedAuthorities(user.getRole())));
    }

    public UserDetails loadUserByEmail(String email) throws UsernameNotFoundException {
        AppUser user = appUserService.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Username not found"));
        if (!user.isEnabled()) {
            throw new UserNotActiveException();
        }
        return new User(user.getEmail(), user.getPassword(), getGrantedAuthorities(user.getRole()));

    }

    private Collection<GrantedAuthority> getAuthoritiesList(String role) {
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(role));
        return authorities;
    }

    private List<String> getPrivileges(Collection<Role> roles) {

        List<String> privileges = new ArrayList<>();
        List<Privilege> collection = new ArrayList<>();
        for (Role role : roles) {
            privileges.add(role.getName());
            collection.addAll(role.getPrivileges());
        }
        for (Privilege item : collection) {
            privileges.add(item.getName());
        }
        return privileges;
    }

    public UserDetails loadUserByPhoneNumber(String phoneNumber) throws UsernameNotFoundException {
        AppUser user = appUserService.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new UsernameNotFoundException("Username not found"));
        return new User(user.getEmail(), user.getPassword(), getGrantedAuthorities(user.getRole()));
    }

    private List<GrantedAuthority> getGrantedAuthorities(List<String> privileges) {
        List<GrantedAuthority> authorities = new ArrayList<>();
        for (String privilege : privileges) {
            authorities.add(new SimpleGrantedAuthority(privilege));
        }
        return authorities;
    }

    private List<GrantedAuthority> getGrantedAuthorities(Role role) {
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(role.getName()));
        return authorities;
    }
}
