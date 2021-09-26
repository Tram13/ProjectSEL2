package be.sel2.api.users;

import be.sel2.api.entities.UserInfo;
import be.sel2.api.exceptions.not_found.UserNotFoundException;
import be.sel2.api.repositories.UserRepository;
import be.sel2.api.util.specifications.DefaultSpecification;
import be.sel2.api.util.specifications.SearchCriteria;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Service that either loads the current user by it's user name or it's ID
 */
@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        DefaultSpecification<UserInfo> specification = new DefaultSpecification<>();
        specification.add(new SearchCriteria("email", email));
        Optional<UserInfo> userInfoOptional = userRepository.findOne(specification);
        if (userInfoOptional.isPresent()) {
            UserInfo user = userInfoOptional.get();
            List<GrantedAuthority> authorities = new ArrayList<>();
            authorities.add(new SimpleGrantedAuthority(user.getRole().toAuthorizationRole()));
            return new UserInfoDetails(user.getId(), email, user.getPassword(), authorities);
        } else {
            throw new UsernameNotFoundException("User not found with email: " + email);
        }

    }

    public UserDetails loadUserById(Long id) {
        UserInfo user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id));
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(user.getRole().toAuthorizationRole()));
        return new UserInfoDetails(id, user.getEmail(), user.getPassword(), authorities);
    }
}
