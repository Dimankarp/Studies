package manager;
import com.sun.istack.NotNull;

import marine.Command;
import marine.net.CommandContainer;
import marine.net.ResponseContainer;
import marine.net.User;
import marine.net.UserCreditContainer;

import marine.structure.SpaceMarine;
import marine.structure.Weapon;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.lang.reflect.Method;
import java.net.SocketAddress;

import java.util.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;


/**
 * Class for working with data structure.
 */
public class Storage {


    private static final Logger logger = LoggerFactory.getLogger("manager.Storage");
    private SpaceMarineRepository marineData;
    private UserRepository userData;
    private HashSet<String> activeUsers;
    private Hashtable<SocketAddress, User> addressToUser;

    public HashSet<String> getActiveUsers() {
        return activeUsers;
    }

    private SpaceMarineRepository getMarineData(){
    lastAccessDate = new Date();
    return marineData;
    }

    private UserRepository getUserData(){
        return userData;
    }
    final private Date creationDate;
    private Date lastAccessDate;
    private FileOperator fileOp;
    private ReadWriteLock rwLock;


    public Storage(String PATH, @NotNull SpaceMarineRepository marineRepo, @NotNull UserRepository userRepo){
        marineData = marineRepo;
        userData = userRepo;
        rwLock = new ReentrantReadWriteLock();
        logger.info("Creating File Operator with PATH {}", PATH);
        fileOp = new FileOperator(PATH);

        activeUsers = new HashSet<>();
        addressToUser = new Hashtable<>();

        creationDate = new Date();
        lastAccessDate = new Date();
    }
    /**
     * Class for executing and keeping commands with @Command annotation
     */
    public class CommandExecutor extends marine.CommandExecutor {

        private HashMap<Command, Method> commands;
        public HashMap<Command,Method> getCommands(){return  commands;}

        private Server.Responder responder;

        private SocketAddress addr;

        public void setAddr(SocketAddress addr) {this.addr = addr;}
        private User caller;

        public CommandExecutor(Server.Responder responder, User caller){
            super();
            this.responder = responder;
            this.caller = caller;
            commands = new HashMap<>();
            for (Method method : Storage.CommandExecutor.class.getDeclaredMethods()) {
                if (method.isAnnotationPresent(Command.class)) {
                    Command annot = method.getAnnotation(Command.class);
                    commands.put(annot, method);
                }
            }
        }


        public CommandExecutor createExecutor(Server.Responder responder, SocketAddress addr){
           CommandExecutor newExec = new CommandExecutor(responder, getUserByAddress(addr));
           newExec.setAddr(addr);
            return newExec;
        }

        private User getUserByAddress(SocketAddress addr){
            return addressToUser.get(addr);
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
                name = "login",
                aliases = {"reg", "register", "log"},
                desc = "login {nickname} {password} - enter your credentials to login/register.",
                basicArgsCount = 2,
                isLocallyExecuted = true,
                isNotManuallyExecuted = true)
        public void login(String[] basicArgs, Object[] complexArgs) throws Exception {
             UserCreditContainer userCredit = (UserCreditContainer)complexArgs[0];
             String nick = new String(userCredit.nicknameBytes());
             // if(activeUsers.contains(nick))throw new IllegalArgumentException(String.format("The user %s is already logged in!", nick));

             if(userData.checkUserExist(nick)){
                 if(userData.tryLogin(userCredit)){
                     User currUser = userData.getUser(nick);
                     activeUsers.add(nick);
                     addressToUser.put(addr, currUser);
                     responder.giveManualResponse(nick);
                 }
                 else{
                     throw new IllegalArgumentException(String.format("Wrong password was provided for %s", nick));
                 }
             }
             else{
                 User currUser = userData.registerUser(userCredit);
                 activeUsers.add(nick);
                 addressToUser.put(addr, currUser);
                 responder.giveManualResponse(nick);
             }


        }

        @Command(
                name = "ping",
                aliases = {"png", "pin", "pong"},
                desc = "ping - pings server and gets response",
                isNotManuallyExecuted = true
        )
        public void ping(String[] basicArgs, Object[] complexArgs) {
            responder.giveResponse("Pong");
        }
        @Command(
                name = "help",
                aliases = {"hlp", "?", "man"},
                desc = "help - shows list of available commands")
        public void help(String[] basicArgs, Object[] complexArgs) {
            StringWriter response = new StringWriter();
            response.append("Current commands are handled:\n");
            for (Method m : CommandExecutor.class.getDeclaredMethods()) {
                if (m.isAnnotationPresent(Command.class) && !m.getAnnotation(Command.class).isNotManuallyExecuted())
                    response.append(m.getAnnotation(Command.class).desc() + '\n');
            }
            responder.giveResponse(response.toString());
        }

        @Command(
                name = "info",
                aliases = {"inf", "!", "data"},
                desc = "info - prints information about current collection (length, date of init etc.)"
        )
        public void info(String[] basicArgs, Object[] complexArgs) throws  IllegalStateException{
            rwLock.readLock().lock();
            try {
                StringWriter response = new StringWriter();
                response.append(String.format("manager.Storage type: %s\n", marineData.getClass().getSimpleName()));
                response.append("Element type: Space Marine\n");
                response.append(String.format("Current Size: %d\n", marineData.getMarinesCount()));
                response.append(String.format("Creation Date: %s\n", creationDate.toString()));
                response.append(String.format("Last Access Date: %s\n", lastAccessDate.toString()));
                responder.giveResponse(response.toString());
                logger.info("Showed INFO.");
            }
            finally {
                rwLock.readLock().unlock();
            }
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
        public void show(String[] basicArgs, Object[] complexArgs) throws IllegalArgumentException, IllegalStateException{
            rwLock.readLock().lock();
            try {
            StringWriter response = new StringWriter();
             response.append("The Storage contains:\n");
             List<SpaceMarine> fetchedMarines = marineData.getMarines().toList();
             Iterator<SpaceMarine> iter = fetchedMarines.iterator();
            while(iter.hasNext()) {
                SpaceMarine marine = iter.next();
                User owner = userData.getUser(marineData.getOwnerId(marine.getId()));
                marine.setOwner(owner);
                response.append(String.format("Owner: %s | %s \n", owner.getNickname(), marine.toString()));
            }
            responder.giveResponse(new ResponseContainer(response.toString(),  fetchedMarines.toArray(new SpaceMarine[0]), new User[]{}));
            logger.info("Printed elements of the Storage.");
            }
            finally {
                rwLock.readLock().unlock();
            }
        }


        @Command(
                name = "add",
                aliases = {"+", "put", "enqueue"},
                desc = "add {SpaceMarine} - adds a new Space Marine to manager.Storage",
                objectArgsCount = 1,
                objectArgsTypes = {SpaceMarine.class})
        public void add(String[] basicArgs, Object[] complexArgs)throws IllegalArgumentException, IllegalStateException{

            SpaceMarine marine = (SpaceMarine)complexArgs[0];
            marine.updateCreationDate();
            rwLock.writeLock().lock();
            try {
              marineData.addMarine(marine, caller);
            }
            finally {
                rwLock.writeLock().unlock();
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
        public void update(String[] basicArgs, Object[] complexArgs) throws IllegalArgumentException, IllegalStateException {
            StringWriter response = new StringWriter();
            int id;
            try {
                id = Integer.parseInt(basicArgs[0]);
            } catch (Exception e) {
                throw new IllegalArgumentException("Passed id is not an Integer!");
            }
            rwLock.writeLock().lock();
            try {
                marineData.updateMarine(id, (SpaceMarine) complexArgs[0], caller);
                response.append(String.format("Successfully updated attributes of Space Marine id: %d%n", id));
                logger.info("Successfully updated SpaceMarine id {}", id);
            }
            catch (IllegalAccessException e){
                throw new IllegalArgumentException(String.format(e.getMessage()));
            }
            finally {
                rwLock.writeLock().unlock();
            }
            responder.giveResponse(response.toString());
        }

        @Command(
                name = "remove_by_id",
                aliases = {"rm_id", "-id"},
                desc = "remove_by_id id - removes the Marine with passed id from Storage",
                basicArgsCount = 1)
        public void remove_by_id(String[] basicArgs, Object[] complexArgs) throws IllegalStateException, IllegalArgumentException {
            rwLock.readLock().lock();
            try {
                if (marineData.getMarinesCount() == 0) {
                    throw new IllegalStateException("The Storage is empty!");
                }
            }
            finally {
                rwLock.readLock().unlock();
            }

            int id;
            try {
                id = Integer.parseInt(basicArgs[0]);
            } catch (Exception e) {
                throw new IllegalArgumentException("Passed id is not an Integer!");
            }
            rwLock.writeLock().lock();
            try{
                marineData.removeMarine(id, caller);
                responder.giveResponse(String.format("Successfully removed marine with id: %d\n", id));
                logger.info("Successfully removed marine with id {}", id);
            }
            catch (IllegalAccessException e){
                throw new IllegalArgumentException(e.getMessage());
            }
            finally {
                rwLock.writeLock().unlock();
            }

        }

        @Command(
                name = "clear",
                aliases = {"clr", "erase"},
                desc = "clear  - clears the Storage")
        public void clear(String[] basicArgs, Object[] complexArgs) throws IllegalStateException {
            rwLock.writeLock().lock();
            try{
                marineData.clear(caller);
            } catch (IllegalAccessException e) {
                throw new IllegalArgumentException(e.getMessage());
            } finally {
                rwLock.writeLock().unlock();
            }
            responder.giveResponse("Successfully cleared the  Storage.");
            logger.info("Storage was cleared!");
        }

        @Command(
                name = "execute_script",
                aliases = {"exec", "script", "sh"},
                desc = "execute_script file_name - interprets commands from provided file in PATH by lines",
                basicArgsCount = 1)
        public void execute_script(String[] basicArgs, Object[] complexArgs) throws Exception {

                logger.info("Starting executing script {}", basicArgs[0]);
                ServerCommandInterpreter currInterpreter = (ServerCommandInterpreter)interpreter;
                try(Scanner fileScanner = fileOp.getScanner(basicArgs[0]); Scanner oldBlockingScanner = currInterpreter.getBlockingScanner()){
                    Scanner oldScanner = currInterpreter.getScanner();
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
        }

        @Command(
                name = "remove_first",
                aliases = {"rm_first", "-first"},
                desc = "remove_first - removes first Space Marine from the Storage")
        public void remove_first(String[] basicArgs, Object[] complexArgs) throws IllegalStateException {
            rwLock.readLock().lock();
            try {
                if (marineData.getMarinesCount() == 0) {
                    throw new IllegalStateException("The Storage is empty!");
                }
            }
            finally {
                rwLock.readLock().unlock();
            }
            rwLock.writeLock().lock();
            try{
                marineData.removeFirst(caller);
                responder.giveResponse(String.format("Successfully removed first marine.\n"));
                logger.info(String.format("Successfully removed first marine.\n"));
            }
            catch (IllegalAccessException e){
                throw new IllegalArgumentException(e.getMessage());
            }
            finally {
                rwLock.writeLock().unlock();
            }

        }

        @Command(
                name = "add_if_min",
                aliases = {"add_min", "if_min"},
                desc = "add_if_min {SpaceMarine} - adds new Space Marine to Storage if his rating is less, than current minimum",
                objectArgsCount = 1,
                objectArgsTypes = {SpaceMarine.class})
        public void add_if_min(String[] basicArgs, Object[] complexArgs) throws IllegalStateException, IllegalArgumentException {
            SpaceMarine marine = (SpaceMarine) complexArgs[0];
            rwLock.writeLock().lock();
            try {
              if( marineData.addIfMin(marine, caller)){
                  responder.giveResponse(String.format("Successfully added the marine.\n"));
                  logger.info(String.format("Successfully added the marine.\n"));
              }
              else{
                  responder.giveResponse(String.format("Didn't added the marine.\n"));
                  logger.info(String.format("Didn't added the marine .\n"));
              }

            }
            finally {
                rwLock.writeLock().unlock();
            }
        }

        @Command(
                name = "remove_greater",
                aliases = {"rm_greater", "rm_big"},
                desc = "remove_greater {SpaceMarine} - removes every Space marine that has bigger rating than passed one from Storage.",
                objectArgsCount = 1,
                objectArgsTypes = {SpaceMarine.class})
        public void remove_greater(String[] basicArgs, Object[] complexArgs) throws IllegalStateException, IllegalArgumentException {
            rwLock.readLock().lock();
            try {
                if (marineData.getMarinesCount() == 0) {
                    throw new IllegalStateException("The Storage is empty!");
                }
            }
            finally {
                rwLock.readLock().unlock();
            }
            rwLock.writeLock().lock();
            try{
                SpaceMarine comparable = (SpaceMarine)complexArgs[0];
                marineData.removeGreater(comparable, caller);
                responder.giveResponse(String.format("Successfully removed elements greater than provided marine.\n"));
                logger.info(String.format("Successfully removed elements greater than provided marine.\n"));
            }
            catch (IllegalAccessException e){
                throw new IllegalArgumentException(e.getMessage());
            }
            finally {
                rwLock.writeLock().unlock();
            }
        }

        @Command(
                name = "sum_of_health",
                aliases = {"sum", "hp"},
                desc = "sum_of_health  - prints sum of health of every Marine in Storage")
        public void sum_of_health(String[] basicArgs, Object[] complexArgs) {
            rwLock.readLock().lock();
            try {
                responder.giveResponse(String.format("Sum of health: %d\n", marineData.getSumOfHealth()));
            }
            finally {
                rwLock.readLock().unlock();
            }

        }

        @Command(
                name = "average_of_health",
                aliases = {"average", "avrg"},
                desc = "average_of_health - prints average of health of every Marine in Storage")
        public void average_of_health(String[] basicArgs, Object[] complexArgs) throws Exception {
            rwLock.readLock().lock();
            try {
                responder.giveResponse(String.format("Average of health: %d\n", marineData.getAverageOfHealth()));
            }
            finally {
                rwLock.readLock().unlock();
            }

        }

        @Command(
                name = "print_unique_weapon_type",
                aliases = {"unique", "weapType"},
                desc = "print_unique_weapon_type - prints every unique weapon type of Marines in Storage")
        public void print_unique_weapon_type(String[] basicArgs, Object[] complexArgs) throws Exception {
            StringWriter response = new StringWriter();
            rwLock.readLock().lock();
            try {
                if (marineData.getMarinesCount() == 0) throw new Exception("The Storage is empty - can't find any weapon");
                Iterator<Weapon> iterator = marineData.getUniqueWeapons().iterator();
                response.append("Unique weapons: ");
                iterator.forEachRemaining((x)->response.append(x.getName() +"\n"));

            }
            finally {
                rwLock.readLock().unlock();
            }

            response.append("\n");
            responder.giveResponse(response.toString());

        }

    }
}
