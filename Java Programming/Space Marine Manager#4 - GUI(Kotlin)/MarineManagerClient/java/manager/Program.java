package manager;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;
/**
 * Launching class with a minimum setup
 */
public class Program {



    public static void main(String[] args) throws IOException {

        System.out.println(
                " _____         _         _____                     \n" +
                "|     |___ ___|_|___ ___|  |  |___ ___ ___ ___ ___ \n" +
                "| | | | .'|  _| |   | -_|    -| -_| -_| . | -_|  _|\n" +
                "|_|_|_|__,|_| |_|_|_|___|__|__|___|___|  _|___|_|  \n" +
                "                                      |_|          \n" +
                "---------------------------------------------------\n" +
                "  Your new best friend in Space Marine Management  \n" +
                "                 For the Emperor!                  \n" +
                "---------------------------------------------------");



        Client client = new Client();
        Client.CommandExecutor executor = client.new CommandExecutor();
        executor.setFileOp(System.getenv("MARINE_PATH"));

        ClientCommandInterpreter interpreter = new ClientCommandInterpreter(null, executor);
        executor.setInterpreter(interpreter);

        int port = 8089;
        try {
            int envPort = Integer.parseInt(System.getenv("MARINE_PORT"));
            if(envPort > 1024 && envPort < 65536) port = envPort;
        }
        catch (NumberFormatException e){}

        connectToServer(client, "localhost", port, interpreter);

        while (true){
          if(!client.isConnected()){
            client.disconnect();
            connectToServer(client, "localhost", port, interpreter);
          }
        }




}

private static void connectToServer(Client currClient, String host, int port, ClientCommandInterpreter interpreter){
        try{
            System.out.printf("Trying to connect to the server %s:%d. \n", host, port);
            currClient.connect(host, port, interpreter);
            System.out.printf("Successfully connected to the server %s:%d. \n", host, port);
            return;
        } catch (IOException e) {
            System.out.printf("Couldn't reach server %s:%d. Trying again in 5 seconds ... \n", host, port);
           try {
               TimeUnit.SECONDS.sleep(5);
           }
           catch (InterruptedException ex){
               System.out.println("Pause got interrupted");
           }
            connectToServer(currClient, host, port, interpreter);
        }
}

}

