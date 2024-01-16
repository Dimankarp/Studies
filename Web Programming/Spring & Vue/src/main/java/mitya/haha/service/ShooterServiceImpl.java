package mitya.haha.service;

import mitya.haha.model.Role;
import mitya.haha.model.User;
import mitya.haha.repository.UserRepository;
import mitya.haha.utils.RoleGuard;
import org.hibernate.Hibernate;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class ShooterServiceImpl implements ShooterService{
    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;

    public ShooterServiceImpl(UserRepository repo, PasswordEncoder passwordEncoder) {
        this.repository = repo;
        this.passwordEncoder = passwordEncoder;
    }

    public User loadUserEntityByUsername(String username) throws UsernameNotFoundException {
        Optional<User> user = repository.getUserByUsername(username);
        if (user.isPresent()) return user.get();
        else
            throw new UsernameNotFoundException(String.format("Couldn't get User from repository by username: %s", username));
    }
    @Transactional
    public User loadUserByUsernameWithRecords(String username) throws UsernameNotFoundException {
        User user = loadUserEntityByUsername(username);
        Hibernate.initialize(user.getRecords());
        return user;
    }


    public void registerUser(String username, String password) throws UserAlreadyRegisteredException {
        if (repository.existsUserByUsername(username))
            throw new UserAlreadyRegisteredException(String.format("%s is already registered in the service!", username));

        User newUser = new User();
        newUser.setUsername(username);
        newUser.setEncodedPassword(passwordEncoder.encode(password));
        repository.save(newUser);
    }

    @RoleGuard(roles={"SCOPE_MITYA"})
    public void addRole(String username, Role role) throws UsernameNotFoundException {
        Optional<User> user = repository.getUserByUsername(username);
        if (user.isPresent()){
            User fetchedUser =  user.get();
            fetchedUser.getRoles().add(role);
            repository.save(fetchedUser);
        }
        else
            throw new UsernameNotFoundException(String.format("Couldn't get User from repository by username: %s", username));
    }

}
