package mitya.haha.service;

import mitya.haha.model.AccessLevel;
import mitya.haha.model.Role;
import mitya.haha.repository.RolesRepository;
import org.springframework.stereotype.Service;

import javax.management.relation.RoleNotFoundException;
import java.util.Optional;

@Service
public class RolesService {

    private final RolesRepository repository;

    public RolesService(RolesRepository repo) {
        this.repository = repo;
    }

    public void registerNewRole(String name, AccessLevel level) throws RoleAlreadyRegisteredException{
        if (repository.existsRoleByName(name))
            throw new RoleAlreadyRegisteredException(String.format("Role %s is already registered in the service!", name));
        Role newRole = new Role();
        newRole.setName(name);
        newRole.setAccessLevel(level);
        repository.save(newRole);

    }

    public Role loadRoleEntityByRolename(String roleName) throws RoleNotFoundException {
        Optional<Role> role = repository.getRoleByName(roleName);
        if (role.isPresent()) return role.get();
        else
            throw new RoleNotFoundException(String.format("Couldn't get Role from repository by rolename: %s", roleName));
    }

}
