package manager;

import marine.net.User;
import marine.net.UserCreditContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.awt.*;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.Random;


public class DBUserRepository implements UserRepository{

    private static final Logger logger = LoggerFactory.getLogger("manager.DBUserRepo");
    private DBOperator dbOp;

    public void setOperator(DBOperator dbOp) {
        this.dbOp = dbOp;
    }

    public DBUserRepository(DBOperator op) throws SQLException {
        dbOp = op;
        int changed;
        changed = dbOp.executeUpdate((x) ->
                dbOp.getStatement("CREATE TABLE IF NOT EXISTS users(" +
                        "id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY, " +
                        "nick VARCHAR(255) UNIQUE," +
                        "pass_hash BYTEA," +
                        "is_super BOOLEAN DEFAULT FALSE," +
                        "color VARCHAR(7) DEFAULT '#000000' CHECK (color ~* '^#[a-f0-9]{6}$') )"));
        if(changed !=0){
            logger.warn("Couldn't find table \"users\", so created it!");
            System.out.println("Couldn't find table \"users\", so created it!");
        }
        dbOp.executeUpdate((x) ->
        { PreparedStatement serverUser = dbOp.getStatement("INSERT INTO users(nick, pass_hash, is_super)" +
                "VALUES (?,?,?) ON CONFLICT DO NOTHING");
            serverUser.setString(1, "SERVER");
            serverUser.setBytes(2, new byte[]{});
            serverUser.setBoolean(3, true);
            return serverUser;
        });

    }

    @Override
    public User registerUser(UserCreditContainer credit) throws IllegalStateException {
        try {
                PreparedStatement marineStatement = dbOp.getStatement("INSERT INTO users(nick, pass_hash, color)" +
                        " VALUES (?, ?, ?)", Statement.RETURN_GENERATED_KEYS);

                marineStatement.setString(1, new String(credit.nicknameBytes()));
                marineStatement.setBytes(2, credit.passHashBytes());

                Random rand = new Random();
                Color color = new Color(rand.nextFloat(), rand.nextFloat(), rand.nextFloat());

                marineStatement.setString(3, String.format( "#%02X%02X%02X",
                    (int)( color.getRed()),
                    (int)( color.getGreen()),
                    (int)( color.getBlue())));
                marineStatement.executeUpdate();
               ResultSet key = marineStatement.getGeneratedKeys();
               key.next();
               User newUser = new User(new String(credit.nicknameBytes()), key.getInt(1));
               newUser.setColor(color);
            return newUser;
        } catch (SQLException e) {
            logger.error("Couldn't register user at users table, because of {}", e.toString());
            System.out.printf("Couldn't register user at users table because of %s.", e.toString());
            throw (IllegalStateException) new IllegalStateException("Couldn't register user at users table!").initCause(e);
        }
    }

    @Override
    public void removeUser(User user) throws IllegalStateException, IllegalArgumentException {

    }

    @Override
    public User getUser(int id) throws IllegalStateException, IllegalArgumentException {
        try {
            ResultSet users = dbOp.executeQuery((x) -> {
                PreparedStatement statement = dbOp.getStatement(
                        "SELECT users.* " +
                                "FROM users " +
                                "WHERE id = ?");
                statement.setInt(1, id);
                return statement;
            });

            if(users.next()) {
                User foundUser = new User(users.getString("nick"), users.getInt("id"));
                foundUser.setColor(Color.decode(users.getString("color")));
                foundUser.setSuperuser(users.getBoolean("is_super"));
                return foundUser;
            }
            else{
                users.close();
                throw new IllegalArgumentException("Couldn't find a user by the id.");
            }

        }
        catch (SQLException e){
            logger.error("Couldn't get user from the users table, because of {}", e.toString());
            System.out.printf("Couldn't get user from the users tables because of %s.", e.toString());
            throw (IllegalStateException) new IllegalStateException("Couldn't get user by id from the users tables!").initCause(e);
        }
    }

    @Override
    public User getUser(String nick) throws IllegalStateException, IllegalArgumentException {
        try {
            ResultSet users = dbOp.executeQuery((x) -> {
                PreparedStatement statement = dbOp.getStatement(
                        "SELECT users.* " +
                                "FROM users " +
                                "WHERE nick = ?");
                statement.setString(1, nick);
                return statement;
            });

            if(users.next()) {
                User foundUser = new User(users.getString("nick"), users.getInt("id"));
                foundUser.setColor(Color.decode(users.getString("color")));
                foundUser.setSuperuser(users.getBoolean("is_super"));
                return foundUser;
            }
            else{
                users.close();
                throw new IllegalArgumentException("Couldn't find a user by the id.");
            }

        }
        catch (SQLException e){
            logger.error("Couldn't get user from the users table, because of {}", e.toString());
            System.out.printf("Couldn't get user from the users tables because of %s.", e.toString());
            throw (IllegalStateException) new IllegalStateException("Couldn't get user by id from the users tables!").initCause(e);
        }
    }

    @Override
    public boolean checkUserExist(String nick) throws IllegalStateException, IllegalArgumentException {
        try {
            ResultSet users = dbOp.executeQuery((x) -> {
                PreparedStatement statement = dbOp.getStatement(
                        "SELECT users.* " +
                                "FROM users " +
                                "WHERE nick = ?");
                statement.setString(1, nick);
                return statement;
            });
            return users.next();
        }
        catch (SQLException e){
            logger.error("Couldn't get user from the users table, because of {}", e.toString());
            System.out.printf("Couldn't get user from the users tables because of %s.", e.toString());
            throw (IllegalStateException) new IllegalStateException("Couldn't get user by id from the users tables!").initCause(e);
        }
    }

    @Override
    public boolean tryLogin(UserCreditContainer credit) throws IllegalStateException, IllegalAccessException {
        try {
            ResultSet users = dbOp.executeQuery((x) -> {
                PreparedStatement statement = dbOp.getStatement(
                        "SELECT users.* " +
                                "FROM users " +
                                "WHERE nick = ?");
                statement.setString(1, new String(credit.nicknameBytes()));
                return statement;
            });
            if(users.next()) {
                return Arrays.equals(users.getBytes("pass_hash"), credit.passHashBytes());
            }
            else{
                users.close();
                throw new IllegalArgumentException("Couldn't find a user with provided nickname.");
            }

        }
        catch (SQLException e){
            logger.error("Couldn't get user from the users table, because of {}", e.toString());
            System.out.printf("Couldn't get user from the users tables because of %s.", e.toString());
            throw (IllegalStateException) new IllegalStateException("Couldn't get user by id from the users tables!").initCause(e);
        }
    }


    @Override
    public String toString(){
        return String.format("User repository implemented by DB with URL: %s", dbOp.getURL());
    }
}
