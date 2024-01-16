package manager;

import manager.structure.SetByUser;

import java.lang.reflect.Method;
import java.util.*;

/**
 * Launching class with a minimum setup
 */
public class Program {


    static Scanner scanner = new Scanner(System.in);
    public static void main(String[] args) {

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



        Storage priorQueue = new Storage(System.getenv("MARINE_PATH"));
        Storage.CommandExecutor executor = priorQueue.new CommandExecutor();
        CommandInterpreter interpreter = new CommandInterpreter(scanner, executor);
        executor.setInterpreter(interpreter);
        while(true){
            try{
                interpreter.interpreterCycle();
            }
            catch (Exception e){
                System.out.println(e.getMessage());
            }

        }

}
}

