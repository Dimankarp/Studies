package gui

import javafx.collections.FXCollections
import javafx.collections.ListChangeListener
import javafx.collections.ObservableList
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.fxml.FXML
import javafx.scene.Node
import javafx.scene.control.*
import javafx.scene.control.TabPane.TabClosingPolicy
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.layout.*
import javafx.stage.Stage
import javafx.util.Callback
import manager.Client
import marine.net.ResponseContainer
import marine.net.UserCreditContainer
import marine.structure.SpaceMarine
import models.DataModel
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.math.log

class MainWindowController {

    private lateinit var app: ManagerApp;
    private lateinit var executor: Client.CommandExecutor;
    private lateinit var client: Client;
    private lateinit var shownModel : DataModel

    @FXML
    private lateinit var mainTabPane: TabPane;

    @FXML
    private lateinit var tabButtonBox: HBox;

    @FXML
    private lateinit var secondaryBox: VBox;

    @FXML
    private lateinit var secondaryPane: GridPane;

    @FXML
    private lateinit var loginLangBox : ComboBox<Locale>;
    private var availableLangs : ObservableList<Locale> = FXCollections.observableList(Internationalizer.supportedLocales.toList());

    @FXML
    private lateinit var logBox: VBox;

    private lateinit var refreshButton : Button
    private lateinit var filterButton : Button
    private lateinit var autoRefreshBox : CheckBox


    fun initModel(app: ManagerApp, shownModel : DataModel, exec: Client.CommandExecutor, client: Client, stage : Stage) {
        this.app = app;
        executor = exec;
        this.client = client;
        this.shownModel = shownModel;



        val commControl = CommandsControl();
        val logControl = LogControl();
        commControl.initModel(client.model, exec, logControl, stage)
        secondaryPane.add(commControl, 0, 0, 2, 1)

        logBox.children.add(logControl)

        app.tableControl.prefHeightProperty().bind(mainTabPane.heightProperty())
        app.tableControl.prefWidthProperty().bind(mainTabPane.widthProperty())

        app.canvasControl.prefHeightProperty().bind(mainTabPane.heightProperty())
        app.canvasControl.prefWidthProperty().bind(mainTabPane.widthProperty())

        val tableControlTab  = Tab("Table", app.tableControl);
        tableControlTab.textProperty().bind(Internationalizer.createStringBinding("tableControlTabName"))

        val canvasControlTab  = Tab("Canvas", app.canvasControl);
        canvasControlTab.textProperty().bind(Internationalizer.createStringBinding("canvasControlTabName"))

        mainTabPane.tabs.addAll(tableControlTab, canvasControlTab)
        mainTabPane.tabClosingPolicy = TabClosingPolicy.UNAVAILABLE
        refreshButton  = Button("Refresh")
/*
        refreshButton  = Button()
        refreshButton.setPrefSize(Region.USE_COMPUTED_SIZE, 20.0);

         var refreshView : ImageView =ImageView( Image(javaClass.classLoader.getResource("icons/Refresh.png").toString(), 20.0, 20.0, true, false))
        refreshButton.graphic = refreshView
*/
         filterButton = Button("Filter");
         autoRefreshBox= CheckBox()

        refreshButton.onAction = EventHandler(this::refreshButtonAction)

        autoRefreshBox.selectedProperty().addListener {ob, ov, nv -> if(nv){refreshButton.isVisible= false} }
        autoRefreshBox.selectedProperty().set(true)
        autoRefreshBox.styleClass.add("autoRefresh")

        client.model.marinesList.addListener {e: ListChangeListener.Change<out SpaceMarine?> ->
            if(autoRefreshBox.isSelected){
                refreshShownList(client.model.marinesList)
            }
            else{
                refreshButton.isVisible = true
            }
        }

        tabButtonBox.children.setAll(filterButton, refreshButton, autoRefreshBox)

        commControl.prefHeightProperty().bind(secondaryBox.heightProperty())
        commControl.prefWidthProperty().bind(secondaryBox.widthProperty())


        loginLangBox.items = availableLangs;
        loginLangBox.value = Internationalizer.locale.value;


        var callback  = Callback<ListView<Locale?>?, ListCell<Locale?>?>  {
            object : ListCell<Locale?>() {
                init {
                    super.setPrefWidth(100.0)
                }
                override fun updateItem(item: Locale?,
                                        empty: Boolean) {
                    super.updateItem(item, empty)
                    if (item != null) {
                        text = item.getDisplayLanguage(Internationalizer.supportedLocales[0])
                    } else {
                        text = null
                    }
                }
            }
        }
        loginLangBox.setCellFactory(callback)
        loginLangBox.buttonCell = callback.call(null);


        client.connectedProperty.addListener(){ov, oldVal, newVal ->
            checkConnection(newVal)
        }

    }

    private fun checkConnection(status : Boolean){
        if(status){
            try {
                executor.executeCommand("relogin", arrayOf(), arrayOf(client.userCredit)).get(5000, TimeUnit.MILLISECONDS)
                mainTabPane.isDisable = false
                secondaryBox.isDisable = false
                logBox.isDisable = false

            }
            catch (e : Exception)
            {
                app.loginWindow()
            }
        }
        else{
            mainTabPane.isDisable = true
            secondaryBox.isDisable = true
            logBox.isDisable = true
        }
    }

    @FXML
    private fun refreshButtonAction(e : ActionEvent){
        refreshShownList(client.model.marinesList)
        refreshButton.isVisible = false
    }

    private fun refreshShownList(updatedList : ObservableList<out SpaceMarine?>){
        shownModel.setMarines(updatedList)
    }

    @FXML
    fun languageChanged(e : ActionEvent){
        Internationalizer.setLocale(loginLangBox.value)
    }




}

