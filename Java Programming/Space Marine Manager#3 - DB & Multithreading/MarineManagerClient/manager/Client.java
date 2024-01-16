package manager;

import marine.Command;
import marine.CommandContainer;
import marine.UserCreditContainer;
import marine.structure.SpaceMarine;
import marine.structure.Weapon;

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

public class Client {

    private SocketChannel clientChannel;

    private final int responseTimeMills = 2*1000;

    private UserCreditContainer userCredit;

    private int awaitingResponseCount;



    public Client() throws IOException {
    }

    public void connect(String host, int port) throws IOException {
        try{

            clientChannel = SocketChannel.open(new InetSocketAddress(host, port));
            clientChannel.configureBlocking(true);
            clientChannel.socket().setSoTimeout(responseTimeMills);
            awaitingResponseCount = 0;

            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                try {
                    clientChannel.close();
                } catch( IOException ignored) {}
            }));

        } catch (IOException e) {
            throw new IOException(String.format("Couldn't connect to %s:%d", host, port));
        }
    }

    @Deprecated
    public int write(ByteBuffer buffer) throws IOException {
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

    public String readResponse() throws IOException {

        StringBuilder sb = new StringBuilder();
        awaitingResponseCount++;

        try{
            while(awaitingResponseCount > 0){

                ObjectInputStream objInStream = new ObjectInputStream(clientChannel.socket().getInputStream());
                sb.append((String)objInStream.readObject());
                awaitingResponseCount--;
                }
                return sb.toString();
        }
        catch (java.net.SocketTimeoutException e) {
            System.out.println("The response time is too high!");
            return null;
        }
        catch (ClassNotFoundException e){
            System.out.println("Couldn't parse the response!");
            return null;
        }
        catch (EOFException e) {
            throw new IOException("The server socket has been closed!");
        }
        catch (Exception e) {
            System.out.printf("Failed to receive a response, because of %s\n", e);
            return null;
        }
    }

    public String readLastResponse() throws IOException {
        awaitingResponseCount++;
        try{
            ObjectInputStream objInStream = new ObjectInputStream(clientChannel.socket().getInputStream());
            String lastString = (String)objInStream.readObject();
            awaitingResponseCount--;
            while(awaitingResponseCount > 0){
                objInStream = new ObjectInputStream(clientChannel.socket().getInputStream());
                lastString = (String)objInStream.readObject();
                awaitingResponseCount--;
            }
            return lastString;
        }
        catch (java.net.SocketTimeoutException e) {
            System.out.println("The response time is too high!");
            return null;
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


    public void writeObject(Object obj) throws IOException {
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
    public int read(ByteBuffer buffer) throws IOException {
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

    public boolean disconnect() throws IOException {
        if(clientChannel.isConnected()){
            clientChannel.close();
            return true;
        }
        return false;
    }

    public class CommandExecutor extends marine.CommandExecutor{

        private FileOperator fileOp;
        public void setFileOp(String path){
            fileOp = new FileOperator(path);
        }

        private int callStackLevel = 0;

        private HashMap<Command, Method> commands;

        public CommandExecutor(){
            commands = new HashMap<>();
            for (Method method : Client.CommandExecutor.class.getDeclaredMethods()) {
                if (method.isAnnotationPresent(Command.class)) {
                    Command annot = method.getAnnotation(Command.class);
                    commands.put(annot, method);
                    for (String alias : annot.aliases()) {
                        commands.put(annot, method);
                    }
                }
            }
        }
        public void executeCommand(Command annot, String[] basicArgs, Object[] objectArgs) throws IOException, IllegalAccessException {

            if(annot.isLocallyExecuted()){
               try {
                   commands.get(annot).invoke(this, basicArgs, objectArgs);
                   return;
               }
               catch (Exception e){
                System.out.printf("Couldn't execute command %s, because of %s\n", annot.name(), e.getCause());
                return;
               }
            }
            CommandContainer container = new CommandContainer(annot, basicArgs, Arrays.copyOf(objectArgs, objectArgs.length, Serializable[].class), userCredit);
            writeObject(container);
            System.out.println(readResponse());
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
            for(Command annot : commands.keySet()){
                if(annot.name().equals("login")){
                    loginCommand = annot;
                    break;
                }
            }
            CommandContainer container = new CommandContainer(loginCommand, new String[]{}, new Serializable[]{credit}, credit);
            writeObject(container);
            String response = readLastResponse();
            if(response.equals(basicArgs[0])){
                System.out.printf("Successfully registered as %s\n", basicArgs[0]);
                userCredit = credit;
            }
            else{
                System.out.println(response);
            }


        }

        //SHARED COMMANDS
        @Command(
                name = "help",
                aliases = {"hlp", "?", "man"},
                desc = "help - shows list of available commands",
                isLocallyExecuted = true)
        public void help(String[] basicArgs, Object[] complexArgs) {
            System.out.println("Current commands are handled:");
            for (Method m : CommandExecutor.class.getDeclaredMethods()) {
                if (m.isAnnotationPresent(Command.class))
                    System.out.println(m.getAnnotation(Command.class).desc() );
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
                Scanner oldScanner = interpreter.getScanner();
                interpreter.setScanner(fileScanner);
                while (fileScanner.hasNextLine()) {
                    interpreter.interpreterCycle();
                }
                interpreter.setScanner(oldScanner);
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
