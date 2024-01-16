package mitya.haha.service;

import mitya.haha.model.User;
import mitya.haha.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ShooterDetailsService implements UserDetailsService {

    private final UserRepository repository;

    public ShooterDetailsService(UserRepository repo) {
        this.repository = repo;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> user = repository.getUserByUsername(username);
        if (user.isPresent()) return user.get();
        else
            throw new UsernameNotFoundException(String.format("Couldn't get User from repository by username: %s", username));
    }

}
