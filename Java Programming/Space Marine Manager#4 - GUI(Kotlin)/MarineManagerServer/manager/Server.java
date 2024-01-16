package manager;


import marine.net.CommandContainer;


import marine.net.ResponseContainer;
import marine.net.User;
import marine.structure.SpaceMarine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.io.*;
import java.net.InetSocketAddress;
import java.net.ServerSocket;


import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;

import java.util.concurrent.*;

public class Server {

    private static final Logger logger = LoggerFactory.getLogger("manager.Server");

    private ServerSocketChannel serverSocketChannel;
    private ServerSocket serverSocket;

    private Storage.CommandExecutor connectedExecutor;
    private ServerCommandInterpreter interpreter;
    private Responder mainResponder;
    private ExecutorService responsePool;

    private ExecutorService executePool;

    private ForkJoinPool readPool;


    private Thread interpreterThread;

    public Responder getMainResponder() {
        return mainResponder;
    }


    public Server(int port) throws IOException {


        responsePool = Executors.newCachedThreadPool();
        executePool = Executors.newFixedThreadPool(2);
        readPool = new ForkJoinPool(2);


        serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.configureBlocking(true);
        serverSocket = serverSocketChannel.socket();
        serverSocket.bind(new InetSocketAddress("localhost", port));

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                serverSocketChannel.close();
            } catch( IOException ignored) {}
        }));

        mainResponder = new Responder();
    }

    public void start(Storage.CommandExecutor storageExecutor, ServerCommandInterpreter interp) {

        if (interpreterThread != null) interpreterThread.interrupt();

        interpreter = interp;
        connectedExecutor = storageExecutor;

        Thread interpreterThread = new Thread(() -> {
            while (true) {
                try {
                    interpreter.interpreterCycle();
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }

            }
        });
        interpreterThread.start();
        while (true) {
            System.out.println("Waiting for new connections...");
            logger.info("Waiting for new connections...");
            try {
                accept(serverSocketChannel.accept());
            } catch (IOException e) {
                logger.warn("Failed to accept new connection.");
                System.out.println("Couldn't accept new connection!");
            }
        }

    }

    private void accept(SocketChannel client) throws IOException {

        logger.info("Accepting channel {}", client.getRemoteAddress().toString());
        client.configureBlocking(true);
        client.socket().setSoTimeout(5 * 60 * 1000);
        readPool.submit(new ReadChannelTask(client));

        logger.info("Successfully accepted new connection from {}", client.getRemoteAddress().toString());
        System.out.printf("Accepted new connection from %s\n", client.getRemoteAddress().toString());


    }

    private class ReadChannelTask implements Runnable {

        private SocketChannel client;

        public ReadChannelTask(SocketChannel connectedClient) {
            client = connectedClient;
        }

        public void run() {
            SocketAddress remoteAdress;
            try {
                remoteAdress = client.getRemoteAddress();
            } catch (IOException e) {
                logger.warn("Couldn't start reading cycle because of {}:{}", e.toString(), e.getMessage());
                System.out.printf("Couldn't start reading cycle because of %s:%s\n", e.toString(), e.getMessage());
                return;
            }
            while (client.isConnected()) {

                CommandContainer readObject;
                try {
                    logger.info("Waiting command from client {}", remoteAdress.toString());
                    System.out.printf("Waiting command from client %s \n", remoteAdress.toString());

                    readObject = (CommandContainer) readObject(client);
                }
                catch (EOFException e){
                    logger.warn("Client {} has closed the connection!", remoteAdress.toString(), e);
                    System.out.printf("Client %s has closed the connection!\n", remoteAdress.toString());
                    return;
                } catch (NotYetConnectedException | IOException e) {
                    logger.warn("Connection is lost from {}", remoteAdress.toString(), e);
                    System.out.printf("Connection is lost from %s!\n", remoteAdress.toString());
                    return;
                } catch (ClassNotFoundException e) {
                    logger.warn("Class wasn't found for provided object.");
                    mainResponder.giveResponse("Class is not found for provided object.");
                    continue;
                }


                if (readObject == null) {
                    continue;
                }


                Server.Responder currResponder = new Server.Responder();
                currResponder.setCurrentRespondent(client);
                if (readObject.userCredit() == null) {
                    currResponder.giveResponse("No user credentials were provided for login/register. Access Denied!");
                    continue;
                }

                Storage.CommandExecutor currExec = connectedExecutor.createExecutor(currResponder, remoteAdress);
                executePool.execute(() -> {
                    try {
                        CommandContainer fetchedCommand = readObject;
                        System.out.printf("Executing command - %s %s from %s \n", fetchedCommand.commandAnnotation().name(), String.join(" ", fetchedCommand.basicArgs()), remoteAdress.toString());
                        logger.info("Executing command - {} {} from {}", fetchedCommand.commandAnnotation().name(), String.join(" ", fetchedCommand.basicArgs()), remoteAdress.toString());
                        currExec.executeCommand(fetchedCommand);
                        System.out.printf("Successfully executed  %s from %s \n", fetchedCommand.commandAnnotation().name(), remoteAdress.toString());
                        logger.info("Successfully executed  {} from {}", fetchedCommand.commandAnnotation().name(), remoteAdress.toString());
                    } catch (Exception e) {
                        currResponder.giveManualResponse(e.getCause() == null ? e.toString() : e.getCause().getMessage());
                        mainResponder.giveResponse(e.getCause() == null ? e.getMessage() : e.getCause().getMessage());
                    }
                });
            }
        }
    }


    private Object readObject(SocketChannel client) throws IOException, ClassNotFoundException {

        logger.info("Reading object from client {}", client.getRemoteAddress().toString());
        System.out.printf("Reading object from client %s \n", client.getRemoteAddress().toString());
        InputStream socketInputStream = client.socket().getInputStream();

        try {
            ObjectInputStream objStream = new ObjectInputStream(socketInputStream);
            Object fetchedObj = objStream.readObject();
            logger.info("Successfully received an object");
            System.out.printf("Received an object.\n");
            return fetchedObj;
        } catch (java.net.SocketTimeoutException e) {
            logger.warn("The response time was exceeded on reading from {}", client.getRemoteAddress().toString());
            System.out.printf("The response time was exceeded on reading from %s\n", client.getRemoteAddress().toString());
            client.close();
            throw new IOException("The response time of the socket was exceeded!");
        } catch (IOException e) {
            throw e;
        } catch (Exception e) {
            logger.warn("Failed to fully receive an object, because of {}", e.toString());
            System.out.printf("Failed to receive an object, because of %s\n", e);
            return null;
        }
    }

    @Deprecated
    private void read(SelectionKey key) throws IOException {

        SocketChannel client = (SocketChannel) key.channel();

        System.out.printf("Reading client %s \n", client.getRemoteAddress().toString());

        ByteBuffer buffer = ByteBuffer.allocate(8096);

        client.read(buffer);

        String data = new String(buffer.array()).trim();
        System.out.println(data);
    }

    public class Responder {

        public SocketChannel currentRespondent;


        public void setCurrentRespondent(SocketChannel currentRespondent){
            this.currentRespondent = currentRespondent;
        }

        public void giveResponse(String message) {;
            System.out.println(message);
            giveManualResponse(message);

        }
        public void giveManualResponse(String message) {
            if (currentRespondent != null) {
                responsePool.execute(() ->
                {
                    try {
                        ResponseContainer response = new ResponseContainer(message, new SpaceMarine[]{}, new User[]{});
                        ObjectOutputStream objStream = new ObjectOutputStream(currentRespondent.socket().getOutputStream());
                        objStream.writeObject(response);

                        logger.info("Sent response to the client {}", currentRespondent.getRemoteAddress());

                    } catch (IOException e) {
                        logger.warn("Couldn't give a response to the client, because {}", e.toString());
                        System.out.printf("Couldn't give a response, because of %s\n!", e);
                    }
                });
            }


        }


        public void giveResponse(ResponseContainer response) {

            System.out.println(response.message());
            giveManualResponse(response);

        }

        public void giveManualResponse(ResponseContainer response) {
            if (currentRespondent != null) {
                responsePool.execute(() ->
                {
                    try {
                       ObjectOutputStream objStream = new ObjectOutputStream(currentRespondent.socket().getOutputStream());
                        objStream.writeObject(response);

                        logger.info("Sent response to the client {}", currentRespondent.getRemoteAddress());

                    } catch (IOException e) {
                        logger.warn("Couldn't give a response to the client, because {}", e.toString());
                        System.out.printf("Couldn't give a response, because of %s\n!", e);
                    }
                });
            }


        }

    }
}