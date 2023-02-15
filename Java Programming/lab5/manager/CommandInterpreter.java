package manager;

import manager.structure.SetByUser;

import java.lang.reflect.Method;
import java.util.Comparator;
import java.util.HashMap;
import java.util.PriorityQueue;
import java.util.Scanner;

public class CommandInterpreter {

    private Scanner scanner;

    private Storage.CommandExecutor commExec;
    private HashMap<String, Method> commands;

    public Scanner getScanner(){
        return scanner;
    }
    public void setScanner(Scanner newScanner){
        this.scanner = newScanner;

    }

    public CommandInterpreter(Scanner initialScanner, Storage.CommandExecutor exec){
        this.scanner = initialScanner;
        this.commExec = exec;
        commands = new HashMap<>();
        for (Method method : Storage.CommandExecutor.class.getDeclaredMethods()) {
            if (method.isAnnotationPresent(Command.class)) {
                Command annot = method.getAnnotation(Command.class);
                commands.put(annot.name(), method);
                for (String alias : annot.aliases()) {
                    commands.put(alias, method);
                }
            }
        }
    }

    public void interpreterCycle()
    {
            if(!scanner.hasNextLine())return;
            String[] parts = scanner.nextLine().split("\s");
            if(parts.length < 1)
            {
                System.out.println("Unknown command");
                return;
            }
            String currCommand = parts[0];
            if (commands.containsKey(currCommand)) {
                Method command = commands.get(currCommand);
                Command annot = command.getAnnotation(Command.class);


                if(parts.length -1 < annot.basicArgsCount())
                {
                    System.out.printf("Not enough arguments provided. %d basic arguments expected.%n", annot.basicArgsCount());
                    return;
                }
                if(parts.length -1 > annot.basicArgsCount())
                {
                    System.out.printf("Too many arguments provided. %d basic arguments expected.%n", annot.basicArgsCount());
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
                        return;
                    }
                    complexArgs[i] = fieldObj;

                }
                try{
                    command.invoke(commExec, basicArgs, complexArgs);
                }
                catch(Exception e){
                    System.out.println(e.getCause().getMessage());
                }
            } else{
                System.out.printf("Unknown command %s%n", currCommand);
                return;
            }


        }




    private void fillAttributes(Object obj) {
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
                        String answ = scanner.nextLine();
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
                        String input = scanner.nextLine();
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
    private boolean askQuestion(String question) {
        System.out.println(question + "Y/N");
        while (true) {
            String answ = scanner.nextLine().toLowerCase().trim();
            if (answ.equals("y")) return true;
            if (answ.equals("n")) return false;
            System.out.println("Incorrect answer - Please, type Y/N!");
        }
    }



}
