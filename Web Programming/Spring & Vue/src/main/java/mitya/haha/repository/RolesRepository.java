package mitya.haha.repository;

import mitya.haha.model.Role;
import mitya.haha.model.User;
import org.springframework.dao.DataAccessException;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RolesRepository extends CrudRepository<Role, Long> {

    public Optional<Role> getRoleByName(String roleName);
    public boolean existsRoleByName(String roleName);

}
