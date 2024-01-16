package gui

import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.layout.VBox
import javafx.stage.Stage
import java.io.IOException
import java.util.*

open class CommandPopUpControl : VBox() {

    private lateinit var fields : HashMap<String, CommandFieldControl>;

    private lateinit var stage : Stage;

    @FXML
    private lateinit var doneButton : Button;
    @FXML
    private lateinit var titleLabel : Label;

    public var isFieldsCorrect : Boolean = false

    init{
        val bundle : ResourceBundle = ResourceBundle.getBundle("TextResources", Internationalizer.locale.value)
        var loader : FXMLLoader = FXMLLoader(javaClass.classLoader.getResource("commandPopUpControl.fxml"), bundle)
        loader.setRoot(this);
        loader.setController(this);

        try{
            loader.load<Parent>()
        }
        catch (e : IOException){
            throw RuntimeException(e);
        }

        doneButton.textProperty().bind(Internationalizer.createStringBinding("popUpDoneLabel"))
        doneButton.onAction = EventHandler {e-> doneAction(e) }
    }

    fun initialize(map: HashMap<String, CommandFieldControl>, stage : Stage, title : String){
        fields = map;
        this.stage = stage;
        titleLabel.text = title;

        for(field in fields.values){
            this.children.add(1, field)
        }
    }


    private fun changeFieldDisableState(state : Boolean){
        for(field in fields.values){
            field.isDisable(state)
        }
    }

    private fun doneAction(e : ActionEvent){
        changeFieldDisableState(true)
        doneButton.isDisable = true
        for(field in fields.values){
            field.checkCorrectness(ActionEvent())
            if(!field.isCorrect.value){
                changeFieldDisableState(false)
                doneButton.isDisable = false
                return
            }
        }
        isFieldsCorrect = true
        stage.close()
    }

}