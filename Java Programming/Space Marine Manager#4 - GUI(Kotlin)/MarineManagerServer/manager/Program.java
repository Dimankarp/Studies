package manager;
import marine.net.User;
import marine.net.UserCreditContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.*;




/**
 * Launching class with a minimum setup
 */
public class Program {

    private static final Logger logger = LoggerFactory.getLogger("manager.Program");

    public static void main(String[] args) {

        System.out.println(
                        " _____         _         _____                     \n" +
                        "|     |___ ___|_|___ ___|   __|___ ___ _ _ ___ ___ \n" +
                        "| | | | .'|  _| |   | -_|__   | -_|  _| | | -_|  _|\n" +
                        "|_|_|_|__,|_| |_|_|_|___|_____|___|_|  \\_/|___|_|  \n" +
                        "---------------------------------------------------\n" +
                        "       Welcome to Space Marine Keeper's Nexus      \n" +
                        "      Control every Marine Keeper with a single    \n" +
                        "      central Keeper's hub.                        \n" +
                        "                 For the Emperor!                  \n" +
                        "---------------------------------------------------");
        startServer();
    }

    private static void startServer() {
        try {


            String dbHostName = "localhost";
            String hostname = System.getenv("MARINE_HOST");
            if(hostname != null)dbHostName = hostname;

            ResourceBundle dbCreditBundle = ResourceBundle.getBundle("DBCredit");
            Properties dbProps = new Properties();
            dbProps.setProperty("user", dbCreditBundle.getString("username"));
            DBOperator operator = new DBOperator(dbHostName, "studs", dbProps);

            int port = 8089;
            try {
                int envPort = Integer.parseInt(System.getenv("MARINE_PORT"));
                if(envPort > 1024 && envPort < 65536) port = envPort;
            }
            catch (NumberFormatException e){}

            logger.info("Initializing server on port :{} ...", port);
            System.out.printf("Initializing server on port :%d ...\n", port);

            Server server = new Server(port);
            logger.info("Creating storage ...");
            System.out.println("Creating storage ...");

            DBSpaceMarineRepository marineRepo = new DBSpaceMarineRepository(operator);
            DBUserRepository userRepo = new DBUserRepository(operator);
            Storage serverStorage = new Storage(System.getenv("MARINE_PATH"), marineRepo, userRepo);
            logger.info("Starting server ...");

            System.out.println("Starting server ...");

            User serverUser = new User("SERVER", 1);
            serverUser.setSuperuser(true);
            Storage.CommandExecutor exec = serverStorage.new CommandExecutor(server.getMainResponder(), serverUser);
            ServerCommandInterpreter interpet = new ServerCommandInterpreter(new Scanner(System.in), exec);
            exec.setInterpreter(interpet);
            server.start(serverStorage.new CommandExecutor(server.getMainResponder(), serverUser), interpet);
        } catch (IOException e) {

            logger.error("Server encountered critical error! The reason: ", e);

            System.out.printf("Server encountered critical error! The reason: %s", e.getMessage());
            System.out.println("Please, press enter to restart the server.");
            new Scanner(System.in).nextLine();
            logger.info("Restarting server...");
            startServer();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }
}

