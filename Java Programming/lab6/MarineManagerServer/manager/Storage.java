package manager;
import jakarta.xml.bind.JAXBException;
import marine.Command;
import marine.CommandContainer;
import marine.structure.SpaceMarine;
import marine.structure.Weapon;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.management.InvalidAttributeValueException;
import java.io.*;
import java.lang.reflect.Method;
import java.nio.file.FileSystemException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Class for working with data structure.
 */
public class Storage {


    private static final Logger logger = LoggerFactory.getLogger("manager.Storage");
    private final PriorityQueue<SpaceMarine> data;

    private PriorityQueue<SpaceMarine> getData(){
    lastAccessDate = new Date();
    return data;
    }
    final private Date creationDate;
    private Date lastAccessDate;
    private FileOperator fileOp;

    private Server.Responder responder;

    public void setResponder(Server.Responder responder){
        this.responder = responder;
    }

    public Storage(String PATH, Server.Responder responder){
        data = new PriorityQueue<>();
        this.responder = responder;
        try{
            logger.info("Create File Operator with PATH {}", PATH);
            fileOp = new FileOperator(PATH);
            logger.info("Fetching the Storage data from {}marine.xml", PATH);
            fetchStorage();
            HashSet<Integer> takenIds = SpaceMarine.getTakenIds();
            data.forEach((x) -> {takenIds.add(x.getId());});
        }
        catch (Exception e){
            logger.info("Couldn't fetch storage from %s, because of: {}", e.getMessage());
            System.out.printf("Couldn't fetch storage from %s, because of: %s \n", PATH, e.getMessage());
        }
        creationDate = new Date();
        lastAccessDate = new Date();
    }

    private void fetchStorage() throws JAXBException, FileNotFoundException, FileSystemException {

           SpaceMarine.SpaceMarineContainer container = (SpaceMarine.SpaceMarineContainer)fileOp.importFromXML();
           data.clear();
           Collections.addAll(data, container.arr);
    }

    /**
     * Class for executing and keeping commands with @Command annotation
     */
    public class CommandExecutor extends marine.CommandExecutor {

        private HashMap<Command, Method> commands;
        public HashMap<Command,Method> getCommands(){return  commands;}

        public CommandExecutor(){
            super();
            commands = new HashMap<>();
            for (Method method : Storage.CommandExecutor.class.getDeclaredMethods()) {
                if (method.isAnnotationPresent(Command.class)) {
                    Command annot = method.getAnnotation(Command.class);
                    commands.put(annot, method);
                }
            }
        }
        public void executeCommand(CommandContainer command) throws Exception{
            if(command != null){
                Command commandAnnotation = command.commandAnnotation();
                logger.info("Trying to execute {} command.", commandAnnotation.name());
                if(commands.containsKey(commandAnnotation)){
                    commands.get(commandAnnotation).invoke(this, command.basicArgs(), command.objectArgs());
                    logger.info("Successfully executed {} command.", commandAnnotation.name());
                    return;
                }
            }
            logger.warn("Failed to execute a command from the container, because it was null.");
            throw new IllegalArgumentException("Can't execute a null-command.");
        }

        @Command(
                name = "help",
                aliases = {"hlp", "?", "man"},
                desc = "help - shows list of available commands")
        public void help(String[] basicArgs, Object[] complexArgs) {
            StringWriter response = new StringWriter();
            response.append("Current commands are handled:\n");
            for (Method m : CommandExecutor.class.getDeclaredMethods()) {
                if (m.isAnnotationPresent(Command.class))
                    response.append(m.getAnnotation(Command.class).desc() + '\n');
            }
            responder.giveResponse(response.toString());
        }

        @Command(
                name = "info",
                aliases = {"inf", "!", "data"},
                desc = "info - prints information about current collection (length, date of init etc.)"
        )
        public void info(String[] basicArgs, Object[] complexArgs) {
            StringWriter response = new StringWriter();
            response.append(String.format("manager.Storage type: %s%n\n", data.getClass().getSimpleName()));
            response.append("Element type: Space Marine\n");
            response.append(String.format("Current Size: %d%n\n", data.size()));
            response.append(String.format("Creation Date: %s%n\n", creationDate.toString()));
            response.append(String.format("Last Access Date: %s%n\n", lastAccessDate.toString()));
            responder.giveResponse(response.toString());
            logger.info("Showed INFO.");
        }

        @Command(
                name = "exit",
                aliases = {"ext", "leave", "quit"},
                desc = "exit - exits program (without saving)")
        public void exit(String[] basicArgs, Object[] complexArgs) {
            logger.warn("Server manual exit with code 0.");
            System.exit(0);
        }

        @Command(
                name = "show",
                aliases = {"shw", ":", "look"},
                desc = "show - prints bio of every Space Marine contained in storage")
        public void show(String[] basicArgs, Object[] complexArgs) {
            StringWriter response = new StringWriter();
             response.append("The Storage contains:\n");
            for (Object marine : data.toArray()) response.append(marine.toString() + '\n');
            responder.giveResponse(response.toString());
            logger.info("Printed elements of the Storage.");
        }


        @Command(
                name = "add",
                aliases = {"+", "put", "enqueue"},
                desc = "add {SpaceMarine} - adds a new Space Marine to manager.Storage",
                objectArgsCount = 1,
                objectArgsTypes = {SpaceMarine.class})
        public void add(String[] basicArgs, Object[] complexArgs) {
            SpaceMarine marine = (SpaceMarine)complexArgs[0];
            marine.setId();
            marine.updateCreationDate();
            getData().add(marine);
            try{
                save(basicArgs, complexArgs);
            }
            catch (Exception e){
                System.out.println(e.getMessage());
            }

            responder.giveResponse("Successfully added new Space Marine to the Storage!");
            logger.info("Successfully added SpaceMarine {}", marine);
        }

        @Command(
                name = "update",
                aliases = {"updt", "^", "change"},
                desc = "update id {SpaceMarine} - updates attributes of the Marine with passed id with data of a passed SpaceMarine",
                basicArgsCount = 1,
                objectArgsCount = 1,
                objectArgsTypes = {SpaceMarine.class})
        public void update(String[] basicArgs, Object[] complexArgs) throws InvalidAttributeValueException {
            StringWriter response = new StringWriter();
            int id;
            try {
                id = Integer.parseInt(basicArgs[0]);
            } catch (Exception e) {
                throw new InvalidAttributeValueException("Passed id is not an Integer!");
            }
            getData().forEach(x -> {
                SpaceMarine marine = (SpaceMarine) x;
                if (marine.getId() == id) {
                    try {
                        SpaceMarine fieldHolder = (SpaceMarine) complexArgs[0];
                        marine.setName(fieldHolder.getName());
                        marine.setCoordinates(fieldHolder.getCoordinates());
                        marine.setHealth(fieldHolder.getHealth());
                        marine.setLoyal(fieldHolder.getLoyal());
                        marine.setAchievements(fieldHolder.getAchievements());
                        marine.setWeaponType(fieldHolder.getWeaponType());
                        marine.setChapter(fieldHolder.getChapter());
                        response.append(String.format("Successfully update attributes of Space Marine id: %d%n", id));
                        logger.info("Successfully update SpaceMarine id {}", id);
                    } catch (Exception e) {
                        logger.warn("Failed to update SpaceMarine id {}", id);
                        System.out.println("Something went terribly wrong!");
                    }
                }

            });
            responder.giveResponse(response.toString());
        }

        @Command(
                name = "remove_by_id",
                aliases = {"rm_id", "-id"},
                desc = "remove_by_id id - removes the Marine with passed id from Storage",
                basicArgsCount = 1)
        public void remove_by_id(String[] basicArgs, Object[] complexArgs) throws Exception {
            if (data.isEmpty()) {
                throw new Exception("The Storage is empty!");
            }
            int id;
            try {
                id = Integer.parseInt(basicArgs[0]);
            } catch (Exception e) {
                throw new InvalidAttributeValueException("Passed id is not an Integer!");
            }
            if(getData().removeIf(x -> x.getId() == id)){
                responder.giveResponse(String.format("Successfully removed marine with id: %d\n", id));
                logger.info("Successfully remove marine with id {}", id);
                return;
            }
            responder.giveResponse("Couldn't find a Space Marine with the specified id");

        }

        @Command(
                name = "clear",
                aliases = {"clr", "erase"},
                desc = "clear  - clears the Storage")
        public void clear(String[] basicArgs, Object[] complexArgs) {

            getData().clear();
            responder.giveResponse("Successfully cleared the  Storage.");
            logger.info("Storage was cleared!");
        }

        @Command(
                name = "save",
                aliases = {"sve", "store", "export"},
                desc = "save - exports Storage to XML file")
        public void save(String[] basicArgs, Object[] complexArgs) throws JAXBException, IOException {
            SpaceMarine.SpaceMarineContainer container = new SpaceMarine.SpaceMarineContainer();
            container.arr = getData().toArray(getData().toArray(new SpaceMarine[0]));
            fileOp.exportToXML(container);
            System.out.println("Successfully exported Storage.");
            logger.info("Storage was exported!");
        }

        @Command(
                name = "execute_script",
                aliases = {"exec", "script", "sh"},
                desc = "execute_script file_name - interprets commands from provided file in PATH by lines",
                basicArgsCount = 1)
        public void execute_script(String[] basicArgs, Object[] complexArgs) throws Exception {

                logger.info("Starting executing script {}", basicArgs[0]);
                ServerCommandInterpreter currInterpreter = (ServerCommandInterpreter)interpreter;
                Scanner fileScanner = fileOp.getScanner(basicArgs[0]);
                Scanner oldScanner = currInterpreter.getScanner();
                Scanner oldBlockingScanner = currInterpreter.getBlockingScanner();

            currInterpreter.setScanner(fileScanner);
            currInterpreter.setBlockingScanner(fileScanner);

                while (fileScanner.hasNextLine()) {
                        interpreter.interpreterCycle();
                }
            currInterpreter.setScanner(oldScanner);
            currInterpreter.setBlockingScanner(oldBlockingScanner);
                System.out.printf("Successfully executed %s script\n", basicArgs[0]);
                logger.info("Starting executed script {}", basicArgs[0]);
        }

        @Command(
                name = "remove_first",
                aliases = {"rm_first", "-first"},
                desc = "remove_first - removes first Space Marine from the Storage")
        public void remove_first(String[] basicArgs, Object[] complexArgs) throws Exception {
            if (data.isEmpty()) {
                throw new Exception("The Storage is empty!");
            }
            try{
                SpaceMarine mar = getData().poll();
                responder.giveResponse(String.format("Successfully removed first Marine id: %d\n.", mar.getId()));
            }
            catch (Exception e){
                throw new Exception("Can't remove the first Marine");
            }


        }

        @Command(
                name = "add_if_min",
                aliases = {"add_min", "if_min"},
                desc = "add_if_min {SpaceMarine} - adds new Space Marine to Storage if his rating is less, than current minimum",
                objectArgsCount = 1,
                objectArgsTypes = {SpaceMarine.class})
        public void add_if_min(String[] basicArgs, Object[] complexArgs) {
            SpaceMarine marine = (SpaceMarine) complexArgs[0];
            if(data.isEmpty()){
                data.offer(marine);
                responder.giveResponse("Successfully added new Space Marine to the Storage, although it was empty!");
                return;
            }
            if(marine.compareTo(data.peek()) < 0){
                getData().offer(marine);
                responder.giveResponse("Successfully added new Space Marine to the Storage.");
                return;
            }
            responder.giveResponse("Provided Space Marine has a rating bigger, than current minimum.");
        }

        @Command(
                name = "remove_greater",
                aliases = {"rm_greater", "rm_big"},
                desc = "remove_greater {SpaceMarine} - removes every Space marine that has bigger rating than passed one from Storage.",
                objectArgsCount = 1,
                objectArgsTypes = {SpaceMarine.class})
        public void remove_greater(String[] basicArgs, Object[] complexArgs) throws Exception {
            if(data.isEmpty()) throw new Exception("The Storage is empty - can't remove the marine");
            SpaceMarine marine = (SpaceMarine) complexArgs[0];
            getData().removeIf(x -> (marine.compareTo(x) < 0));
            responder.giveResponse("Successfully removed greater elements");
        }

        @Command(
                name = "sum_of_health",
                aliases = {"sum", "hp"},
                desc = "sum_of_health  - prints sum of health of every Marine in Storage")
        public void sum_of_health(String[] basicArgs, Object[] complexArgs) {
            int sumHp = 0;
            for (SpaceMarine marine : data) sumHp += marine.getHealth();
            responder.giveResponse(String.format("Sum of health: %d\n", sumHp));
        }

        @Command(
                name = "average_of_health",
                aliases = {"average", "avrg"},
                desc = "average_of_health - prints average of health of every Marine in Storage")
        public void average_of_health(String[] basicArgs, Object[] complexArgs) throws Exception {
            if(data.isEmpty()) throw new Exception("The Storage is empty - can't calculate average");
            int sumHp = 0;
            for (SpaceMarine marine : data) sumHp += marine.getHealth();
            responder.giveResponse(String.format("Average of health: %.2f\n", (float)sumHp/data.size()));
        }

        @Command(
                name = "print_unique_weapon_type",
                aliases = {"unique", "weapType"},
                desc = "print_unique_weapon_type - prints every unique weapon type of Marines in Storage")
        public void print_unique_weapon_type(String[] basicArgs, Object[] complexArgs) throws Exception {
            if(data.isEmpty()) throw new Exception("The Storage is empty - can't find any weapon");
            HashSet<Weapon> uniqueTypes = new HashSet<>();
            StringWriter response = new StringWriter();
            for (SpaceMarine marine : data) uniqueTypes.add(marine.getWeaponType());
            System.out.print("Unique weapons: ");
            for(Weapon weap : uniqueTypes)response.append(weap.name() + " ");
            response.append("\n");
            responder.giveResponse(response.toString());

        }

    }
}
