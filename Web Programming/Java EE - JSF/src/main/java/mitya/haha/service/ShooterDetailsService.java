package mitya.haha.service;

import mitya.haha.model.User;
import mitya.haha.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Base64;
import java.util.Optional;

@Service
public class ShooterDetailsService implements UserDetailsService {

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;

    public ShooterDetailsService(UserRepository repo, PasswordEncoder passwordEncoder){
        this.repository = repo;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> user =  repository.getUserByUsername(username);
        if(user.isPresent()) return user.get();
        else throw new UsernameNotFoundException(String.format("Couldn't get User from repository by username: %s", username));
    }

    public void registerUser(String username, String password) throws UserAlreadyRegisteredException{
        if(repository.existsUserByUsername(username))throw new UserAlreadyRegisteredException(String.format("%s is already registered in the service!", username));

        User newUser = new User();
        newUser.setUsername(username);
        newUser.setEncodedPassword(passwordEncoder.encode(password));
        repository.save(newUser);
    }

}
