package gui

import javafx.application.Application
import javafx.application.Platform
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.stage.Stage
import manager.Client
import manager.ClientCommandInterpreter
import marine.structure.SpaceMarine
import models.DataModel
import java.io.IOException
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.properties.Delegates

class ManagerApp : Application() {

    private var stage : Stage by Delegates.notNull()
    private lateinit var client : Client;
    private  lateinit var connectionThread: Thread;
    private  lateinit var showedModel : DataModel;

    //Changing Roots
    public lateinit var canvasControl: CanvasControl
    private set;
    public lateinit var tableControl: TableControl
    private set;



    companion object{
        var instance : ManagerApp by Delegates.notNull()
            private set;
    }
    init{
        instance = this
    }

    fun launch() {
        Application.launch();
    }

    public override fun start(primaryStage: Stage) {

        client = Client()
        val executor = client.CommandExecutor()
        executor.setFileOp(System.getenv("MARINE_PATH"))

        showedModel = DataModel()

        val interpreter = ClientCommandInterpreter(null, executor)
        executor.setInterpreter(interpreter)

        var port = 8089
        try {
            val envPort = port//System.getenv("MARINE_PORT").toIntOrNull() ?: port;
            if (envPort in 1025..65535) port = envPort
        } catch (e: NumberFormatException) {
        }

        connectionThread = Thread {
            connectToServer(client, "localhost", port, interpreter)

            while (!Thread.currentThread().isInterrupted) {
                if (!client.isConnected) {
                    client.disconnect()
                    connectToServer(client, "localhost", port, interpreter)
                }
            }
        };
        connectionThread.start();
        stage = primaryStage;
        loginWindow();
        //mainWindow();
        stage.show();
    }

    private fun connectToServer(currClient: Client, host: String, port: Int, interpreter: ClientCommandInterpreter) {
        while(!Thread.currentThread().isInterrupted) {
            try {
                System.out.printf("Trying to connect to the server %s:%d. \n", host, port)
                currClient.connect(host, port, interpreter)
                System.out.printf("Successfully connected to the server %s:%d. \n", host, port)
                return
            } catch (e: IOException) {
                System.out.printf("Couldn't reach server %s:%d. Trying again in 5 seconds ... \n", host, port)
                try {
                    TimeUnit.SECONDS.sleep(5)
                } catch (ex: InterruptedException) {
                    Thread.currentThread().interrupt(); // I could've added my own flag - but this works! Could be problems, though...
                    return;
                }
            }
        }
    }

    fun loginWindow(){

        val bundle : ResourceBundle = ResourceBundle.getBundle("TextResources", Internationalizer.locale.value)
        var loginLoader : FXMLLoader = FXMLLoader(javaClass.classLoader.getResource("loginWindow.fxml"), bundle);
        var scene : Parent = loginLoader.load();
        scene.stylesheets.add("css/loginWindow.css")
        var loginController : LoginWindowController = loginLoader.getController();
        loginController.initModel(this, client.CommandExecutor(), client);
        replaceRoot(scene);

        stage.titleProperty().unbind();
        stage.titleProperty().bind(Internationalizer.createStringBinding("loginWindowName"))
        stage.width = 380.0;
        stage.height = 480.0;
        stage.isResizable = false;
    }

    fun mainWindow(){

        val bundle : ResourceBundle = ResourceBundle.getBundle("TextResources", Internationalizer.locale.value)
        var mainLoader : FXMLLoader = FXMLLoader(javaClass.classLoader.getResource("mainWindow.fxml"), bundle);
        var scene : Parent = mainLoader.load();
        scene.stylesheets.add("css/mainWindow.css")
        var mainController : MainWindowController = mainLoader.getController();

        canvasControl = CanvasControl();
        tableControl = TableControl();
        tableControl.initModel(showedModel, client)
        canvasControl.initModel(showedModel, client)

        showedModel.clearMarines()

        val executor =  client.CommandExecutor();
        executor.setInterpreter(ClientCommandInterpreter(null, executor))

        mainController.initModel(this, showedModel, executor, client, stage);
        replaceRoot(scene);

        stage.titleProperty().unbind();
        stage.titleProperty().bind(Internationalizer.createStringBinding("mainWindowName"))
        stage.width = 700.0;
        stage.height = 540.0;

        stage.minWidth = 640.0;
        stage.minHeight = 480.0;

        stage.isResizable = true;

        stage.maxWidth = 1280.0;
        stage.maxHeight = 960.0;
    }


    fun replaceRoot(newRoot: Parent) {
        var scene : Scene? = stage.scene;
        if(scene == null){
            scene = Scene(newRoot, 350.0, 450.0)
            stage.scene = scene;
        }
        else {
            stage.scene.root = newRoot;
        }
    }

    override fun stop() {
        connectionThread.interrupt();
        client.disconnect();
        super.stop();
    }

}
