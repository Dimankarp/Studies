package manager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
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

            logger.info("Initializing server on port :8089 ...");
            System.out.println("Initializing server on port :8089 ...");
            Server server = new Server(8089);
            logger.info("Creating storage ...");
            System.out.println("Creating storage ...");
            Storage serverStorage = new Storage(System.getenv("MARINE_PATH"), server.getMainResponder());
            logger.info("Starting server ...");

            System.out.println("Starting server ...");

            Storage.CommandExecutor exec = serverStorage.new CommandExecutor();
            ServerCommandInterpreter interpet = new ServerCommandInterpreter(new Scanner(new InputStream() {
                @Override
                public int read() throws IOException {
                    if(System.in.available() > 0){
                        int read = System.in.read();
                        return read;
                    }
                    return -1;
                }
            }), exec);
            exec.setInterpreter(interpet);
            server.start(serverStorage.new CommandExecutor(), interpet);
        } catch (IOException e) {

            logger.error("Server encountered critical error! The reason: ", e);

            System.out.printf("Server encountered critical error! The reason: %s", e.getMessage());
            System.out.println("Please, press enter to restart the server.");
            System.console().readLine();
            logger.info("Restarting server...");
            startServer();

        }


    }
}

