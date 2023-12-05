package mitya.haha.repository;

import mitya.haha.model.User;
import org.springframework.dao.DataAccessException;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {

    public Optional<User> getUserByUsername(String username);
    public boolean existsUserByUsername(String username);

}
