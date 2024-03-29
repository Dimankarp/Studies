package manager;

import marine.Command;
import marine.CommandExecutor;
import marine.CommandInterpreter;
import marine.structure.SetByUser;

import java.io.IOException;
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
public class ServerCommandInterpreter extends CommandInterpreter {

    private Scanner scanner;

    private Scanner blockingScanner;

    private Storage.CommandExecutor commExec;
    private HashMap<String, Method> commands;

    private int currCallLevel;

    public Scanner getScanner(){
        return scanner;
    }
    public void setScanner(Scanner newScanner){
        this.scanner = newScanner;

    }

    public Scanner getBlockingScanner(){
        return blockingScanner;
    }
    public void setBlockingScanner(Scanner newScanner){
        this.blockingScanner = newScanner;

    }

    public ServerCommandInterpreter(Scanner initialScanner, Storage.CommandExecutor exec){
        super(initialScanner, exec);
        commExec = exec;
        scanner = initialScanner;
        blockingScanner = new Scanner(System.in);
        currCallLevel = 0;
        commands = new HashMap<>();
        HashMap<Command, Method> execCommands = commExec.getCommands();
        for (Command comm : execCommands.keySet()){
                Method currMethod = execCommands.get(comm);
                commands.put(comm.name(), currMethod);
                for (String alias : comm.aliases()) {
                    commands.put(alias, currMethod);
                }
            }
        }


    public void interpreterCycle() throws Exception {

        currCallLevel+=1;
        if(currCallLevel > 20){
            currCallLevel-=1;
            throw new Exception("The call stack can't be more than 20.");
        }


        String input;
        try{
            if(scanner.hasNextLine()){
                input = scanner.nextLine();
            }
            else{
                currCallLevel-=1;
                return;

            }

        }
        catch (Exception e){
            //Do if EOF is encountered in the Stream
            currCallLevel-=1;
            System.exit(0);
            return;
        }

        String[] parts = input.split("\s");
        if(parts.length < 1)
        {
            currCallLevel-=1;
            System.out.println("Unknown command");
            return;
        }
        String currCommand = parts[0];
        if (commands.containsKey(currCommand)) {

            Method command = commands.get(currCommand);
            Command annot = command.getAnnotation(Command.class);

            if(annot.isNotManuallyExecuted()){
                System.out.printf("You can't execute this command manually!\n");
                currCallLevel-=1;
                return;
            }

            if(parts.length -1 < annot.basicArgsCount())
            {
                System.out.printf("Not enough arguments provided. %d basic arguments expected.%n", annot.basicArgsCount());
                currCallLevel-=1;
                return;
            }
            if(parts.length -1 > annot.basicArgsCount())
            {
                System.out.printf("Too many arguments provided. %d basic arguments expected.%n", annot.basicArgsCount());
                currCallLevel-=1;
                return;
            }
            String[] basicArgs = new String[annot.basicArgsCount()];
            System.arraycopy(parts, 1, basicArgs, 0, annot.basicArgsCount());
            Object[] complexArgs = new Object[annot.objectArgsCount()];
            for(int i = 0; i < annot.objectArgsCount(); i++)
            {
                Object fieldObj;
                try {
                    fieldObj = annot.objectArgsTypes()[i].getConstructor().newInstance();
                    fillAttributes(fieldObj);
                }
                catch (Exception e){

                    System.out.println(e.getMessage());
                    currCallLevel-=1;
                    return;
                }
                complexArgs[i] = fieldObj;

            }

            command.invoke(commExec, basicArgs, complexArgs);

        } else{
            System.out.printf("Unknown command %s%n", currCommand);
            currCallLevel-=1;
            return;
        }

        currCallLevel-=1;
    }


//WARNING! DURING THE EXECUTION OF THE METHODS BELOW - THE SERVER STOPS HANDLING SELECTOR AND THE CLIENTS!

    private void fillAttributes(Object obj) throws Exception {
        System.out.println("Filling the attributes of: " + obj.getClass().getSimpleName());
        PriorityQueue<Method> sortedMethods = new PriorityQueue<>(new Comparator<Method>() {
            @Override
            public int compare(Method o1, Method o2) {
                return o1.getAnnotation(SetByUser.class).attributeName().compareTo(o2.getAnnotation(SetByUser.class).attributeName());
            }
        });
        for (Method m : obj.getClass().getDeclaredMethods()){
            if(m.isAnnotationPresent(SetByUser.class))
                sortedMethods.offer(m);
        }
        while(!sortedMethods.isEmpty()){
            Method m = sortedMethods.poll();
            if (m.isAnnotationPresent(SetByUser.class)) {
                SetByUser annot = m.getAnnotation(SetByUser.class);

                if (annot.canBeNull() && askQuestion(String.format("The field %s is not required. Do you want to skip it?", annot.attributeName())))
                    continue;

                if (annot.isComplex()) {

                    Class argClass = m.getParameterTypes()[0];
                    try {
                        Object argObj = argClass.getConstructor().newInstance();
                        fillAttributes(argObj);
                        m.invoke(obj, argObj);
                        System.out.println("The object was successfully set to the field!");
                    } catch (Exception e) {
                        System.out.println(e.getCause().getMessage());
                    }

                } else if (annot.isEnum()) {

                    Class argClass = annot.enumClass();
                    System.out.printf("Next constants are available for the %s field%n", annot.attributeName());
                    for (Object constant : argClass.getEnumConstants()) {
                        System.out.print(constant.toString() + " ");
                    }
                    System.out.println();
                    while(true){
                        String answ  =  blockingScanner.nextLine();//scanner.nextLine();
                        if(answ == null){
                            throw new Exception("Input is null");
                        }
                        try {
                            m.invoke(obj, answ);
                            System.out.println("The enum constant was successfully set to the field!");
                            break;
                        } catch (Exception e) {
                            System.out.println(e.getCause().getMessage());
                        }
                    }


                } else {
                    System.out.printf("Please, enter the value for the %s field%n", annot.attributeName());
                    while (true) {
                        String input =  blockingScanner.nextLine();//scanner.nextLine();
                        if (input.equals("")) {
                            if (annot.canBeNull()) {
                                try {
                                    m.invoke(obj, null);
                                    System.out.println("The null was successfully set to the field!");
                                    break;
                                } catch (Exception e) {
                                    System.out.println(e.getCause().getMessage());

                                }

                            } else {
                                System.out.println("This field can't be null!");
                            }

                        } else {
                            try {
                                m.invoke(obj, input);
                                System.out.println("The value was successfully set to the field!");
                                break;
                            } catch (Exception e) {
                                System.out.println(e.getCause().getMessage());

                            }
                        }
                    }

                }


            }
        }

        System.out.printf("The object of class %s has been successfully created%n", obj.getClass().getSimpleName());
    }
    private boolean askQuestion(String question) throws IOException {
        System.out.println(question + "Y/N");
        while (true) {
            String answ =  blockingScanner.nextLine().toLowerCase().trim();//scanner.nextLine().toLowerCase().trim();
            if (answ.equals("y")) return true;
            if (answ.equals("n")) return false;
            System.out.println("Incorrect answer - Please, type Y/N!");
        }
    }



}
