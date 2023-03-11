package manager;

import jakarta.xml.bind.JAXBException;
import marine.CommandContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.io.*;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

public class Server {

    private static final Logger logger = LoggerFactory.getLogger("manager.Server");

    private Selector serverSelector;
    private ServerSocketChannel serverSocketChannel;
    private ServerSocket serverSocket;

    private Storage.CommandExecutor connectedExecutor;

    private ServerCommandInterpreter interpreter;
    private Responder mainResponder;

    public Responder getMainResponder() {
        return mainResponder;
    }

    public Server(int port) throws IOException {


        serverSelector = Selector.open();
        serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.configureBlocking(false);
        serverSocket = serverSocketChannel.socket();
        serverSocket.bind(new InetSocketAddress("localhost", port));
        int ops = serverSocketChannel.validOps();
        serverSocketChannel.register(serverSelector, ops);

        mainResponder = new Responder();


    }

    public void start(Storage.CommandExecutor storageExecutor, ServerCommandInterpreter interp) throws IOException {

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                storageExecutor.save(new String[0], new Object[0]);
                System.out.println("Successfully exported Storage at exit.");
                logger.info("Successfully exported Storage at exit.");
            } catch (JAXBException | IOException e) {
                System.out.println("Couldn't export Storage at exit.");
                logger.warn("Couldn't export Storage at exit.");
            }
        }));

        interpreter = interp;
        connectedExecutor = storageExecutor;
        System.out.println("Waiting for connections...");
        while(true){
            serverSelector.select(2 * 100L);
            Iterator<SelectionKey> iterator = serverSelector.selectedKeys().iterator();
            while(iterator.hasNext()){
                SelectionKey key = iterator.next();
                logger.info("Started selector cycle with key {}", key);
                mainResponder.setCurrentRespondent(key);
                if(key.isAcceptable()){
                    try{
                        accept(key);
                    }
                    catch (IOException e){
                        logger.warn("Failed to accept new connection.");
                        System.out.println("Couldn't accept new connection!");
                    }

                }
                else if(key.isReadable()){

                    SocketChannel keyChannel = (SocketChannel)key.channel();
                    CommandContainer fetchedCommand;
                    logger.info("Reading command of client {}", keyChannel.getRemoteAddress().toString());
                    System.out.printf("Reading command of client %s \n", keyChannel.getRemoteAddress().toString());
                    try{
                        fetchedCommand = (CommandContainer) readObject(key);
                    }
                    catch (NotYetConnectedException|IOException e){
                        logger.warn("Connection is lost from {}", keyChannel.getRemoteAddress().toString(), e);
                        System.out.printf("Connection is lost from %s!\n", keyChannel.getRemoteAddress().toString());
                        key.cancel();
                        continue;
                    }
                    catch (ClassNotFoundException e){
                        logger.warn("Class wasn't found for provided object.");
                        mainResponder.giveResponse("Class is not found for provided object.");
                        continue;
                    }

                    if(fetchedCommand == null){
                        continue;
                    }

                    try{
                        System.out.printf("Executing command - %s %s from %s \n", fetchedCommand.commandAnnotation().name(), String.join(" ", fetchedCommand.basicArgs()),  keyChannel.getRemoteAddress().toString());
                        logger.info("Executing command - {} {} from {}", fetchedCommand.commandAnnotation().name(), String.join(" ", fetchedCommand.basicArgs()),  keyChannel.getRemoteAddress().toString());
                        storageExecutor.executeCommand(fetchedCommand);
                        System.out.printf("Successfully executed  %s from %s \n", fetchedCommand.commandAnnotation().name(), (keyChannel.getRemoteAddress().toString()));
                        logger.info("Successfully executed  {} from {}", fetchedCommand.commandAnnotation().name(), keyChannel.getRemoteAddress().toString());
                    }
                    catch (Exception e){
                        mainResponder.giveResponse(e.getMessage());
                    }




                }
                iterator.remove();
                logger.info("Ended selector cycle with key {}", key);
            }
            if(System.in.available() > 1){
                try
                {
                    mainResponder.setCurrentRespondent(null);
                    interpreter.interpreterCycle();
                }
                catch (Exception e){
                    System.out.println(e.getMessage());
                }
            }
        }

    }

    private void accept(SelectionKey key) throws IOException {

        logger.info("Trying to accept key {}", key);
        SocketChannel client = serverSocketChannel.accept();
        if(client == null)return;
        client.configureBlocking(false);

        client.register(serverSelector, SelectionKey.OP_READ, new ByteArrayOutputStream());
        logger.info("Successfully accepted new connection from {}", client.getRemoteAddress().toString());
        System.out.printf("Accepted new connection from %s\n",client.getRemoteAddress().toString());

    }

    private void read(SelectionKey key) throws IOException {

        SocketChannel client = (SocketChannel)key.channel();

        System.out.printf("Reading client %s \n", client.getRemoteAddress().toString());

        ByteBuffer buffer = ByteBuffer.allocate(8096);

        client.read(buffer);

        String data = new String(buffer.array()).trim();
        System.out.println(data);
    }

    private Object readObject(SelectionKey key) throws IOException, ClassNotFoundException {


            SocketChannel client = (SocketChannel)key.channel();
            ByteBuffer inputBuffer = ByteBuffer.allocate(1024);
            logger.info("Reading object from client {}", ((SocketChannel)key.channel()).getRemoteAddress().toString());

            int readBytesCount = client.read(inputBuffer);
            logger.info("Read {} bytes from client {}", readBytesCount, ((SocketChannel)key.channel()).getRemoteAddress().toString());
            inputBuffer.flip();
            System.out.printf("Reading client %s \n", client.getRemoteAddress().toString());


            ByteArrayOutputStream currByteStream = (ByteArrayOutputStream)key.attachment();
            byte[] currInput = new byte[inputBuffer.limit()];

            inputBuffer.get(currInput);
            currByteStream.write(currInput);

            ByteBuffer commandBuffer = ByteBuffer.wrap(currByteStream.toByteArray());
            //Trying to create an object



            if(commandBuffer.remaining() > 4){

                commandBuffer.mark();
                int expectedLen = commandBuffer.getInt();
                logger.info("Trying to create object from {} bytes", expectedLen);
                if(commandBuffer.remaining() >= expectedLen){
                    byte[] serializedObj = new byte[expectedLen];
                    commandBuffer.get(serializedObj);
//                    System.out.println(expectedLen);
//                    System.out.println(Arrays.toString(commandBuffer.array()));
//                    System.out.println(Arrays.toString(serializedObj));
                    commandBuffer.compact();
                    commandBuffer.flip();
                    byte[] leftBuffer = new byte[commandBuffer.remaining()];
                    commandBuffer.get(leftBuffer);
                    currByteStream.reset();
                    currByteStream.write(leftBuffer);

//
//                    System.out.println(Arrays.toString(commandBuffer.array()));
                    try(ObjectInputStream objStream = new ObjectInputStream(new ByteArrayInputStream(serializedObj))){
                        logger.info("Successfully received an object");
                        System.out.printf("Received an object.\n");
                        return objStream.readObject();
                    }
                }
                else{
                    commandBuffer.reset();
                    logger.warn("Failed to fully receive an object - not enough bytes in the stream. Expected {}, came {}!", expectedLen, commandBuffer.remaining());
                    System.out.printf("Not enough bytes in the stream to fully create an object!\n");
                    return null;
                }
            }
            logger.warn("Failed to fully receive an object - not enough bytes to read object length!");
            System.out.printf("Didn't fully receive an object - not enough bytes in the stream to read object length!\n");
            return null;




    }

    public class Responder{

        public SelectionKey currentRespondent;

        public void setCurrentRespondent(SelectionKey currentRespondent) {
            this.currentRespondent = currentRespondent;
        }

        public void giveResponse(String response){

            System.out.println(response);
            if(currentRespondent != null) {
                try(ByteArrayOutputStream outByte = new ByteArrayOutputStream(); ObjectOutputStream objStream = new ObjectOutputStream(outByte)){
                    objStream.writeObject(response);
                    ByteBuffer buffer = ByteBuffer.allocate(outByte.size() + 4); //4 is for length integer
                    buffer.put(4, outByte.toByteArray());
                    buffer.putInt(0, outByte.size());

                    buffer.position(0);
                    SocketChannel client = (SocketChannel) currentRespondent.channel();
                    client.write(buffer);
                    logger.info("Sent response to the client {}", ((SocketChannel)currentRespondent.channel()).getRemoteAddress());

                } catch (IOException e) {
                    logger.warn("Couldn't give a response to the client");
                    System.out.println("Couldn't give a response!");
                }
            }

        }

    }





}
