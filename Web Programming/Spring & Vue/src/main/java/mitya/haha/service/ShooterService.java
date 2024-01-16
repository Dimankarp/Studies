package mitya.haha.service;

import mitya.haha.model.Role;
import mitya.haha.model.User;
import mitya.haha.utils.RoleGuard;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.transaction.annotation.Transactional;
public interface ShooterService {
    public User loadUserEntityByUsername(String username) throws UsernameNotFoundException;
    @Transactional
    public User loadUserByUsernameWithRecords(String username) throws UsernameNotFoundException;

    public void registerUser(String username, String password) throws UserAlreadyRegisteredException;

    public void addRole(String username, Role role) throws UsernameNotFoundException;
}
