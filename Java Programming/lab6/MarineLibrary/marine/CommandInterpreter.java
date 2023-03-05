package marine;

import marine.structure.SetByUser;

import java.lang.reflect.Method;
import java.util.Comparator;
import java.util.HashMap;
import java.util.PriorityQueue;
import java.util.Scanner;

/**
 * Class for command interpreting and execution calling
 *
 * <p>
 *     Works by interpreter cycles with a maximum call stack of 20.
 * </p>
 */
public abstract class CommandInterpreter {

    protected Scanner scanner;

    protected CommandExecutor commExec;
    protected HashMap<String, Method> commands;

    protected int currCallLevel;

    public Scanner getScanner(){
        return scanner;
    }
    public void setScanner(Scanner newScanner){
        this.scanner = newScanner;

    }

    public CommandInterpreter(Scanner initialScanner, CommandExecutor exec){
        this.scanner = initialScanner;
        this.commExec = exec;
        commands = new HashMap<>();
        currCallLevel = 0;
    }

    public abstract void interpreterCycle() throws Exception;

}
