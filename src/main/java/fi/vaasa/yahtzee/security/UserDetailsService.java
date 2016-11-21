package fi.vaasa.yahtzee.security;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import fi.vaasa.yahtzee.domain.dto.UserDTO;

/**
 * Authenticate a user with fake mechanism.
 */
@Component("userDetailsService")
public class UserDetailsService implements org.springframework.security.core.userdetails.UserDetailsService {

    private final Logger log = LoggerFactory.getLogger(UserDetailsService.class);
    
    @Override
    public UserDetails loadUserByUsername(final String login) {
        log.debug("Authenticating {}", login);
        String lowercaseLogin = login.toLowerCase(Locale.ENGLISH);
        
        UserDTO fakeUser = MockSecutiryUtils.createDummyUserAccount(login);
        
        List<GrantedAuthority> grantedAuthorities = fakeUser.getAuthorities().stream()
                .map(authority -> new SimpleGrantedAuthority(authority))
                .collect(Collectors.toList());
        
        return new org.springframework.security.core.userdetails.User(lowercaseLogin, MockSecutiryUtils.NO_PASSWORD, grantedAuthorities);
    }
}