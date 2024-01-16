package manager;

import javafx.beans.property.SimpleBooleanProperty;
import marine.Command;
import marine.CommandInterpreter;
import marine.net.CommandContainer;
import marine.net.ResponseContainer;
import marine.net.UserCreditContainer;
import marine.structure.SpaceMarine;
import marine.structure.Weapon;
import models.DataModel;

import javax.management.InvalidAttributeValueException;
import java.io.*;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;

import java.nio.ByteBuffer;
import java.nio.channels.NotYetConnectedException;
import java.nio.channels.SocketChannel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


public class Client {

    private SocketChannel clientChannel;
    private Lock socketLock;
    private Thread pingThread;
    private DataModel model;

    public DataModel getModel(){return model;}

    private ExecutorService executeThreadPool;

    private volatile SimpleBooleanProperty isConnectedProperty;
    public boolean isConnected(){return isConnectedProperty.getValue();}

    public SimpleBooleanProperty getConnectedProperty(){return isConnectedProperty;}

    public void isConnected(boolean conn){isConnectedProperty.set(conn);}
    private final int responseTimeMills = 2*1000;
    private final int pingTimePeriodMills = 10 * 1000;

    private volatile UserCreditContainer userCredit;
    public  UserCreditContainer getUserCredit(){
        return userCredit;
    }

    public boolean isRegistered(){return userCredit != null;}
    private int awaitingResponseCount;



    public Client() throws IOException {
        socketLock = new ReentrantLock();
        userCredit = null;
        isConnectedProperty = new SimpleBooleanProperty(false);

        model = new DataModel();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                if(clientChannel != null) {
                    clientChannel.close();
                }
                if(pingThread != null) {
                    pingThread.interrupt();
                }
                executeThreadPool.shutdownNow();
            } catch( IOException ignored) {}
        }));

    }

    public void connect(String host, int port, ClientCommandInterpreter interp) throws IOException {
        try{
            executeThreadPool = Executors.newFixedThreadPool(3);
            clientChannel = SocketChannel.open(new InetSocketAddress(host, port));
            clientChannel.configureBlocking(true);
            clientChannel.socket().setSoTimeout(responseTimeMills);
            awaitingResponseCount = 0;
            isConnectedProperty.set(true);

            //Check-if-server-is-alive task
            pingThread = new Thread(() -> {
                Command pingCommand = ClientCommandInterpreter.getCommands().get("show");
                String[] emptyArr = new String[]{};
                while(!Thread.currentThread().isInterrupted()){
                    try{
                        CommandContainer pingContainer = new CommandContainer(pingCommand, emptyArr, emptyArr, userCredit);
                        ResponseContainer response = sendCommand(pingContainer);
                        if(response.mentionedMarines() != null){
                            model.setMarines(response.mentionedMarines());
                        }
                        Thread.sleep(pingTimePeriodMills);
                    } catch (IOException|InterruptedException e) {
                        isConnectedProperty.set(false);
                        return;
                    }
                }
            });
            pingThread.start();





        } catch (IOException e) {
            throw new IOException(String.format("Couldn't connect to %s:%d", host, port));
        }
    }

    public void disconnect() throws IOException {
        if(clientChannel != null) {
            clientChannel.close();
        }
            isConnectedProperty.set(false);
        if(pingThread != null) {
            pingThread.interrupt();
        }
        if(executeThreadPool != null) {
            executeThreadPool.shutdown();
        }

    }

    private ResponseContainer sendCommand(CommandContainer command) throws IOException{
        try {
            socketLock.lock();
            writeObject(command);
            return readLastResponse();
        }
        finally {
            socketLock.unlock();
        }

    }

    @Deprecated
    private int write(ByteBuffer buffer) throws IOException {
        if(clientChannel.isConnected()){
            try{
                return clientChannel.write(buffer);
            }
            catch (IOException e){
                throw e;
            }
        }
        else throw new NotYetConnectedException();
    }
    private ResponseContainer readLastResponse() throws IOException {
        awaitingResponseCount++;
        try{
            ObjectInputStream objInStream = new ObjectInputStream(clientChannel.socket().getInputStream());
            ResponseContainer lastResponse = (ResponseContainer)objInStream.readObject();
            awaitingResponseCount--;
            while(awaitingResponseCount > 0){
                objInStream = new ObjectInputStream(clientChannel.socket().getInputStream());
                lastResponse = (ResponseContainer)objInStream.readObject();
                awaitingResponseCount--;
            }
            return lastResponse;
        }
        catch (java.net.SocketTimeoutException e) {
            System.out.println("The response time is too high!");
            throw new IOException("The response time is too high!");
        }
        catch (ClassNotFoundException e){
            System.out.println("Couldn't parse the response!");
            return null;
        }
        catch (Exception e) {
            System.out.printf("Failed to receive a response, because of %s\n", e);
            return null;
        }
    }


    private void writeObject(Object obj) throws IOException {
        if(clientChannel.isConnected()){
            try{
                ObjectOutputStream objOutStream = new ObjectOutputStream(clientChannel.socket().getOutputStream());
                objOutStream.writeObject(obj);
            }
            catch (IOException e){
                throw e;
            }
        }
        else throw new NotYetConnectedException();
    }

    @Deprecated
    private int read(ByteBuffer buffer) throws IOException {
        if(clientChannel.isConnected()){
            try{
                return clientChannel.read(buffer);
            }
            catch (IOException e){
                throw e;
            }
        }
        else throw new NotYetConnectedException();
    }



    public class CommandExecutor extends marine.CommandExecutor{

        private ClientCommandInterpreter interpreter;

        public void setInterpreter(ClientCommandInterpreter interp) {
            interpreter = interp;
        }
        private FileOperator fileOp;
        public void setFileOp(String path){
            fileOp = new FileOperator(path);
        }

        private int callStackLevel = 0;

      static private HashMap<Command, Method> commandsToMethods;


        static public HashMap<String, Command> getCommandsToMethods(){return commands;}

      static{
          commandsToMethods = new HashMap<>();
          for (Method method : Client.CommandExecutor.class.getDeclaredMethods()) {
              if (method.isAnnotationPresent(Command.class)) {
                  Command annot = method.getAnnotation(Command.class);
                  commandsToMethods.put(annot, method);
                  for (String alias : annot.aliases()) {
                      commandsToMethods.put(annot, method);
                  }
              }
          }
      }

        static private HashMap<String, Command> commands;

        static public HashMap<String, Command> getCommands(){return commands;}

        static{
            commands = new HashMap<>();
            for (Method method : Client.CommandExecutor.class.getDeclaredMethods()) {
                if (method.isAnnotationPresent(Command.class)) {
                    Command annot = method.getAnnotation(Command.class);
                    commands.put(annot.name(), annot);
                    for (String alias : annot.aliases()) {
                        commands.put(alias, annot);
                    }
                }
            }
        }

        public CommandExecutor(){
        }

        public Future<ResponseContainer> executeCommand(Command annot, String[] basicArgs, Object[] objectArgs){
              return (Future<ResponseContainer>) executeThreadPool.submit(() -> {
                  try {
                      return execute(annot, basicArgs, objectArgs);
                  }  catch (IOException e) {
                      isConnectedProperty.set(false);
                        return null;
            }
              });
        }

        public Future<ResponseContainer> executeCommand(String commandName, String[] basicArgs, Object[] objectArgs){
            if(CommandExecutor.getCommands().containsKey(commandName)){
                Command executedCommand = CommandExecutor.getCommands().get(commandName);
                return executeCommand(executedCommand, basicArgs, objectArgs);
            }
            else{
                throw new IllegalArgumentException("Provided command doesn't exist");
            }
        }

        private ResponseContainer execute(Command annot, String[] basicArgs, Object[] objectArgs) throws IOException {

            if(annot.isLocallyExecuted()){
                try {
                    CommandExecutor.commandsToMethods.get(annot).invoke(this, basicArgs, objectArgs);
                    return null;
                }
                catch (Exception e){
                    System.out.printf("Couldn't execute command %s, because of %s\n", annot.name(), e.getCause());
                    return null;
                }
            }
            CommandContainer container = new CommandContainer(annot, basicArgs, Arrays.copyOf(objectArgs, objectArgs.length, Serializable[].class), userCredit);
            ResponseContainer response = sendCommand(container);
            System.out.println(response.message());
            return response;
        }

        //CLIENT ONLY COMMANDS
        @Command(
                name = "sleep",
                aliases = {"slp", "zzz", "wait"},
                desc = "sleep {time} - sleeps for {time} ms.",
                basicArgsCount = 1,
                isLocallyExecuted = true)
        public void sleep(String[] basicArgs, Object[] complexArgs) throws InterruptedException {
            Thread.sleep(Long.parseLong(basicArgs[0]));
        }

        @Command(
                name = "whoami",
                aliases = {"who", "user", "me"},
                desc = "whoami - get your user credits if it's entered.",
                isLocallyExecuted = true)
        public void whoami(String[] basicArgs, Object[] complexArgs){
            if(userCredit != null){
                System.out.printf("Your nickname is: %s\n",new String(userCredit.nicknameBytes()));
                if(userCredit.passHashBytes() != null){
                    System.out.println("Your password is set");
                }
                else{
                    System.out.println("Your password is NOT set");
                }
            }
            else {
                System.out.println("You are not logged in!");
            }
        }

        @Command(
                name = "relogin",
                aliases = {"reg", "register", "log"},
                desc = "relogin {userContainer} - enter your credentials to relogin/register.",
                objectArgsCount = 1,
                isLocallyExecuted = true,
                isNotManuallyExecuted = true)
        public void relogin(String[] basicArgs, Object[] complexArgs) throws NoSuchAlgorithmException, IOException {
            UserCreditContainer credit = (UserCreditContainer)complexArgs[0];

            System.out.printf("Trying to relogin\n");
            Command loginCommand = null;
            for(Command annot : CommandExecutor.commandsToMethods.keySet()){
                if(annot.name().equals("login")){
                    loginCommand = annot;
                    break;
                }
            }
            CommandContainer container = new CommandContainer(loginCommand, new String[]{}, new Serializable[]{credit}, credit);
            ResponseContainer response = sendCommand(container);
            if(response.message().equals(new String(credit.nicknameBytes()))){
                System.out.printf("Successfully reloginned!\n");
                userCredit = credit;
            }
            else{
                throw new IOException("Invalid relogin credits provided!");
            }


        }


        @Command(
                name = "login",
                aliases = {"reg", "register", "log"},
                desc = "login {nickname} {password} - enter your credentials to login/register.",
                basicArgsCount = 2,
                isLocallyExecuted = true,
                isNotManuallyExecuted = true)
        public void login(String[] basicArgs, Object[] complexArgs) throws NoSuchAlgorithmException, IOException {
            if(basicArgs[0].length() > 30){
                throw new IllegalStateException("The nickname must be shorter than 30 characters!");
            }
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            UserCreditContainer credit = new UserCreditContainer(basicArgs[0].getBytes(), digest.digest(basicArgs[1].getBytes()));

            System.out.printf("Trying to register as %s\n", basicArgs[0]);
            Command loginCommand = null;
            for(Command annot : CommandExecutor.commandsToMethods.keySet()){
                if(annot.name().equals("login")){
                    loginCommand = annot;
                    break;
                }
            }
            CommandContainer container = new CommandContainer(loginCommand, new String[]{}, new Serializable[]{credit}, credit);
            ResponseContainer response = sendCommand(container);
            if(response.message().equals(basicArgs[0])){
                System.out.printf("Successfully registered as %s\n", basicArgs[0]);
                userCredit = credit;
            }
            else{
                System.out.println(response);
            }


        }

        //SHARED COMMANDS
        @Command(
                name = "ping",
                aliases = {"png", "pin", "pong"},
                desc = "ping - pings server and gets response",
                isNotManuallyExecuted = true
        )
        public void ping(String[] basicArgs, Object[] complexArgs) {

        }
        @Command(
                name = "help",
                aliases = {"hlp", "?", "man"},
                desc = "help - shows list of available commands",
                isLocallyExecuted = true)
        public void help(String[] basicArgs, Object[] complexArgs) {
            StringBuilder sb = (StringBuilder)complexArgs[0];
            sb.append("Current commands are handled:\n");
            for (Method m : CommandExecutor.class.getDeclaredMethods()) {
                if (m.isAnnotationPresent(Command.class))
                    sb.append(m.getAnnotation(Command.class).desc()).append("\n");
            }

        }
        @Command(
                name = "info",
                aliases = {"inf", "!", "data"},
                desc = "info - prints information about current collection (length, date of init etc.)"
        )
        public void info(String[] basicArgs, Object[] complexArgs) {

        }

        @Command(
                name = "exit",
                aliases = {"ext", "leave", "quit"},
                desc = "exit - exits program (without saving)",
                isLocallyExecuted = true)
        public void exit(String[] basicArgs, Object[] complexArgs) {
            System.exit(0);
        }

        @Command(
                name = "show",
                aliases = {"shw", ":", "look"},
                desc = "show - prints bio of every Space Marine contained in storage")
        public void show(String[] basicArgs, Object[] complexArgs) {

        }


        @Command(
                name = "add",
                aliases = {"+", "put", "enqueue"},
                desc = "add {SpaceMarine} - adds a new Space Marine to manager.Storage",
                objectArgsCount = 1,
                objectArgsTypes = {SpaceMarine.class})
        public void add(String[] basicArgs, Object[] complexArgs) {

        }

        @Command(
                name = "update",
                aliases = {"updt", "^", "change"},
                desc = "update id {SpaceMarine} - updates attributes of the Marine with passed id with data of a passed SpaceMarine",
                basicArgsCount = 1,
                objectArgsCount = 1,
                objectArgsTypes = {SpaceMarine.class})
        public void update(String[] basicArgs, Object[] complexArgs) throws InvalidAttributeValueException {

        }

        @Command(
                name = "remove_by_id",
                aliases = {"rm_id", "-id"},
                desc = "remove_by_id id - removes the Marine with passed id from Storage",
                basicArgsCount = 1)
        public void remove_by_id(String[] basicArgs, Object[] complexArgs) throws Exception {

        }

        @Command(
                name = "clear",
                aliases = {"clr", "erase"},
                desc = "clear  - clears the Storage")
        public void clear(String[] basicArgs, Object[] complexArgs) {

        }

        @Command(
                name = "execute_script",
                aliases = {"exec", "script", "sh"},
                desc = "execute_script file_name - interprets commands from provided file in PATH by lines",
                basicArgsCount = 1,
                isLocallyExecuted = true)
        public void execute_script(String[] basicArgs, Object[] complexArgs) throws Exception {
            callStackLevel+=1;
            if(callStackLevel >= 20){
                callStackLevel-=1;
                throw new Exception("The stack level has reached 20. Aborting script execution");
            }
            try(Scanner fileScanner = fileOp.getScanner(basicArgs[0])){
                interpreter.setScanner(fileScanner);
                while (fileScanner.hasNextLine()) {
                    interpreter.interpreterCycle();
                }
                System.out.printf("Successfully executed %s script\n", basicArgs[0]);
            } catch (FileNotFoundException e) {
                System.out.println(e.getMessage());
            }
            catch (Exception e){
                callStackLevel-=1;
                throw e;
            }
            callStackLevel-=1;


        }

        @Command(
                name = "remove_first",
                aliases = {"rm_first", "-first"},
                desc = "remove_first - removes first Space Marine from the Storage")
        public void remove_first(String[] basicArgs, Object[] complexArgs) throws Exception {
        }

        @Command(
                name = "add_if_min",
                aliases = {"add_min", "if_min"},
                desc = "add_if_min {SpaceMarine} - adds new Space Marine to Storage if his rating is less, than current minimum",
                objectArgsCount = 1,
                objectArgsTypes = {SpaceMarine.class})
        public void add_if_min(String[] basicArgs, Object[] complexArgs) {

        }

        @Command(
                name = "remove_greater",
                aliases = {"rm_greater", "rm_big"},
                desc = "remove_greater {SpaceMarine} - removes every Space marine that has bigger rating than passed one from Storage.",
                objectArgsCount = 1,
                objectArgsTypes = {SpaceMarine.class})
        public void remove_greater(String[] basicArgs, Object[] complexArgs) throws Exception {
        }

        @Command(
                name = "sum_of_health",
                aliases = {"sum", "hp"},
                desc = "sum_of_health  - prints sum of health of every Marine in Storage")
        public void sum_of_health(String[] basicArgs, Object[] complexArgs) {
        }

        @Command(
                name = "average_of_health",
                aliases = {"average", "avrg"},
                desc = "average_of_health - prints average of health of every Marine in Storage")
        public void average_of_health(String[] basicArgs, Object[] complexArgs) throws Exception {
        }

        @Command(
                name = "print_unique_weapon_type",
                aliases = {"unique", "weapType"},
                desc = "print_unique_weapon_type - prints every unique weapon type of Marines in Storage")
        public void print_unique_weapon_type(String[] basicArgs, Object[] complexArgs) throws Exception {

        }

    }


}
