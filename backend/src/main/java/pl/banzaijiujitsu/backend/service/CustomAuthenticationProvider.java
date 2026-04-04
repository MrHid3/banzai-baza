package pl.banzaijiujitsu.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import pl.banzaijiujitsu.backend.exception.InvalidEmailException;

@Service
public class CustomAuthenticationProvider implements AuthenticationProvider {

    final private AppUserDetailsService appUserDetailsService;
    final private EncodingService encodingService;

    @Autowired
    public CustomAuthenticationProvider(AppUserDetailsService appUserDetailsService, EncodingService encodingService){
        this.appUserDetailsService = appUserDetailsService;
        this.encodingService = encodingService;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String email = authentication.getName();
        String password = (String) authentication.getCredentials();
//        String password = (String) authentication.getCredentials();

        UserDetails userDetails = appUserDetailsService.loadUserByEmail(email);

        if(userDetails == null){
            throw new InvalidEmailException("EMAIL_NOT_FOUND");
        }

        if(!encodingService.passwordMatches(password, userDetails.getPassword())){
            throw new BadCredentialsException("INVALID_PASSWORD");
        }


        return new UsernamePasswordAuthenticationToken(userDetails, password, userDetails.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.equals(authentication);
    }
}
