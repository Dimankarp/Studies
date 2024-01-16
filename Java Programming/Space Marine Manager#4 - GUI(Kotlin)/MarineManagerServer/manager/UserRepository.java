package manager;

import marine.net.User;
import marine.net.UserCreditContainer;

public interface UserRepository {

    User registerUser(UserCreditContainer credit) throws IllegalStateException;

    void removeUser(User user) throws IllegalStateException, IllegalArgumentException;

    User getUser(int id) throws IllegalStateException, IllegalArgumentException;

    User getUser(String nick) throws IllegalStateException, IllegalArgumentException;

    boolean checkUserExist(String nick) throws IllegalStateException, IllegalArgumentException;

    boolean tryLogin(UserCreditContainer credit) throws IllegalStateException, IllegalAccessException;

}
