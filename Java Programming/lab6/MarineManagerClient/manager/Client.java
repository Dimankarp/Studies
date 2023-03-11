package manager;

import marine.Command;
import marine.CommandContainer;
import marine.structure.SpaceMarine;
import marine.structure.Weapon;

import javax.management.InvalidAttributeValueException;
import java.io.*;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;

import java.nio.ByteBuffer;
import java.nio.channels.NotYetConnectedException;
import java.nio.channels.SocketChannel;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Scanner;

public class Client {

    private SocketChannel clientSocket;

    private ByteArrayOutputStream responseStream;

    private final long responseTimeMills = 2* 100L;

    public Client() throws IOException {
    }

    public void connect(String host, int port) throws IOException {
        try{
            clientSocket = SocketChannel.open(new InetSocketAddress(host, port));
            clientSocket.configureBlocking(false);
            responseStream = new ByteArrayOutputStream();
        } catch (IOException e) {
            throw new IOException(String.format("Couldn't connect to %s:%d", host, port));
        }
    }

    public int write(ByteBuffer buffer) throws IOException {
        if(clientSocket.isConnected()){
            try{
                return clientSocket.write(buffer);
            }
            catch (IOException e){
                throw e;
            }
        }
        else throw new NotYetConnectedException();
    }

    public String readResponse() throws IOException {
        StringBuilder sb = new StringBuilder();
       long startingTime =  System.currentTimeMillis();
        ByteBuffer inputBuffer = ByteBuffer.allocate(1024);
        while(clientSocket.isConnected() && System.currentTimeMillis()-startingTime < responseTimeMills){
        try{
            inputBuffer.clear();
            clientSocket.read(inputBuffer);
            inputBuffer.flip();

            byte[] currInput = new byte[inputBuffer.limit()];
            inputBuffer.get(currInput);
            responseStream.write(currInput);

            ByteBuffer responseBuffer = ByteBuffer.wrap(responseStream.toByteArray());

            if(responseBuffer.remaining() > 4){
                responseBuffer.mark();
                int expectedLen = responseBuffer.getInt();
                if(responseBuffer.remaining() >= expectedLen){
                    byte[] serializedObj = new byte[expectedLen];
                    responseBuffer.get(serializedObj);

                    responseBuffer.compact();
                    responseBuffer.flip();
                    byte[] leftBuffer = new byte[responseBuffer.remaining()];
                    responseBuffer.get(leftBuffer);
                    responseStream.reset();
                    responseStream.write(leftBuffer);
                    ObjectInputStream objStream = new ObjectInputStream(new ByteArrayInputStream(serializedObj));
                    try{
                        sb.append((String)objStream.readObject());
                    }
                    catch (ClassNotFoundException e){
                        responseStream.reset();
                        System.out.println("Couldn't parse the response!");
                        return null;
                    }

                }
                else{
                    responseBuffer.reset();
                }
            }

        }
        catch (IOException e){
            responseStream.reset();
            throw e;
        }
    }
        responseStream.reset();
        if(!sb.isEmpty()){
            return sb.toString();
        }
        System.out.println("The response time is too high!");
     throw new NotYetConnectedException();
    }


    public void writeObject(Object obj) throws IOException {
        if(clientSocket.isConnected()){
            try(ByteArrayOutputStream outByte = new ByteArrayOutputStream(); ObjectOutputStream objStream = new ObjectOutputStream(outByte)){
                objStream.writeObject(obj);
                ByteBuffer buffer = ByteBuffer.allocate(outByte.size() + 4); //4 is for length integer
                buffer.put(4, outByte.toByteArray());
                buffer.putInt(0, outByte.size());

                buffer.position(0);
                clientSocket.write(buffer);
            }
            catch (IOException e){
                throw e;
            }
        }
        else throw new NotYetConnectedException();
    }

    public int read(ByteBuffer buffer) throws IOException {
        if(clientSocket.isConnected()){
            try{
                return clientSocket.read(buffer);
            }
            catch (IOException e){
                throw e;
            }
        }
        else throw new NotYetConnectedException();
    }

    public boolean disconnect() throws IOException {
        if(clientSocket.isConnected()){
            clientSocket.close();
            return true;
        }
        return false;
    }

    public class CommandExecutor extends marine.CommandExecutor{

        private FileOperator fileOp;

        private int callStackLevel = 0;

        public void setFileOp(String path){
            fileOp = new FileOperator(path);
        }

        public void executeCommand(Command annot, String[] basicArgs, Object[] objectArgs) throws IOException {

            if(annot.name().equals("help")){
                help(basicArgs, objectArgs);
                return;
            }
            if(annot.name().equals("exit")){
                exit(basicArgs, objectArgs);
                return;
            }
            if(annot.name().equals("execute_script")){
               try {
                   execute_script(basicArgs, objectArgs);
                   return;
               }
               catch (Exception e){
                System.out.printf("Couldn't execute a script, because of %s\n!", e.getMessage());
               }
            }

            CommandContainer container = new CommandContainer(annot, basicArgs, Arrays.copyOf(objectArgs, objectArgs.length, Serializable[].class));
            writeObject(container);
            System.out.println(readResponse());
        }

        @Command(
                name = "help",
                aliases = {"hlp", "?", "man"},
                desc = "help - shows list of available commands")
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
                desc = "exit - exits program (without saving)")
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
                basicArgsCount = 1)
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
