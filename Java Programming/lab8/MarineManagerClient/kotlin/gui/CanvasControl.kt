package gui

import gui.graphics.MarineCard
import javafx.application.Platform
import javafx.collections.ListChangeListener
import javafx.event.EventHandler
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.Group
import javafx.scene.Parent
import javafx.scene.control.ScrollPane
import javafx.scene.input.MouseButton
import javafx.scene.input.MouseEvent
import javafx.scene.input.ScrollEvent
import javafx.scene.layout.GridPane
import manager.Client
import manager.Client.CommandExecutor
import marine.net.ResponseContainer
import marine.structure.SpaceMarine
import models.DataModel
import java.io.IOException
import java.util.*
import java.util.concurrent.TimeUnit
import java.util.function.Consumer


class CanvasControl : GridPane() {

    @FXML
    private lateinit var canvasGroup : Group;
    @FXML
    private lateinit var scrollPane : ScrollPane;

    private lateinit var model : DataModel;
    private lateinit var executor: Client.CommandExecutor;
    private lateinit var client: Client;

    private var lastVValue : Double = 1.0;
    private var lastHValue : Double = 1.0;


    private var scale : Double = 1.0
        set(newScale : Double){
        field = Mapper.map(newScale, 0.4, 5.0, 0.4, 5.0)
    }
    private var maxDiffX : Double = 1.0;
    private var maxDiffY : Double = 1.0;
    init{
        val bundle : ResourceBundle = ResourceBundle.getBundle("TextResources", Internationalizer.locale.value)
        var loader : FXMLLoader = FXMLLoader(javaClass.classLoader.getResource("canvasControl.fxml"), bundle)
        loader.setRoot(this);
        loader.setController(this);

        try{
            loader.load<Parent>()
        }
        catch (e : IOException){
            throw RuntimeException(e);
        }

        scrollPane.addEventFilter(ScrollEvent.ANY) { e ->

            lastVValue = scrollPane.vvalue
            lastHValue = scrollPane.hvalue
            if(e.deltaY > 0)zoomIn()
            else zoomOut()
            e.consume()
        }

    }

    fun zoomIn(){
        scale+=0.1
        updateMarines()
    }
    fun zoomOut(){
        scale-=0.1
        updateMarines()
    }


    fun initModel(model : DataModel, client: Client){
        this.model = model;
        this.client = client;
        this.executor = client.CommandExecutor();

        MarineCard.onUpdateConsumer = Consumer {x ->
            try {
                executor.executeCommand("update", arrayOf(x.id.toString()), arrayOf(x)).get(5000, TimeUnit.MILLISECONDS)
                var response : ResponseContainer = executor.executeCommand("show", arrayOf(), arrayOf()).get(5000, TimeUnit.MILLISECONDS)
                model.setMarines(response.mentionedMarines)
            }
            catch (e : Exception)
            {
            }
        }

        model.marinesList.addListener { e: ListChangeListener.Change<out SpaceMarine?> ->
            updateMarines()
        };


    }

    private fun updateMarines(){
        Platform.runLater {
            clear()
            for (marine in model.marinesList) {
                drawMarine(marine!!)
            }
            scrollPane.vvalue = lastVValue
            scrollPane.hvalue = lastHValue
        }
    }

    private fun clear(){
        canvasGroup.children.clear();
    }

    private fun drawMarine(marine : SpaceMarine){

        var card : MarineCard = MarineCard(marine)
        card.scale = scale
        card.isEditable = marine.owner.nickname.equals(String(client.userCredit.nicknameBytes))
        card.addToGroup(canvasGroup)

    }


}