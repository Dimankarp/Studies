package gui

import javafx.beans.binding.StringBinding
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleStringProperty
import javafx.beans.property.StringProperty
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.canvas.Canvas
import javafx.scene.control.Label
import javafx.scene.control.ScrollPane
import javafx.scene.control.TextField
import javafx.scene.layout.GridPane
import javafx.scene.text.TextFlow
import java.io.IOException
import java.lang.RuntimeException
import java.util.*
import java.util.function.DoubleFunction

class CommandFieldControl : GridPane() {

    @FXML
    private lateinit var fieldLabel : Label;

    @FXML
    private lateinit var fieldField : TextField;

    var isCorrect : SimpleBooleanProperty = SimpleBooleanProperty(false)
    private set;
    fun getFieldValue() : String{return fieldField.text}

    private lateinit var  corrFunc : (String, StringProperty) -> Boolean;

    @FXML
    private lateinit var errorLabel : Label;

    init{
        val bundle : ResourceBundle = ResourceBundle.getBundle("TextResources", Internationalizer.locale.value)
        var loader : FXMLLoader = FXMLLoader(javaClass.classLoader.getResource("commandFieldControl.fxml"), bundle)
        loader.setRoot(this);
        loader.setController(this);

        try{
            loader.load<Parent>()
        }
        catch (e : IOException){
            throw RuntimeException(e);
        }
    }

    fun initField(name : StringBinding, corrFunc : (String, StringProperty) -> Boolean, initialValue : String) : SimpleBooleanProperty{
        fieldLabel.textProperty().bind(name)
        fieldField.text = initialValue
        fieldField.focusedProperty().addListener { ov, oldV, newV ->
            if (!newV) {
                checkCorrectness(ActionEvent())
            }
        }
        fieldField.onAction = EventHandler {e -> checkCorrectness(e)}
        errorLabel.textProperty().set("")
        this.corrFunc = corrFunc;
        return isCorrect
    }

    fun initField(name : StringBinding, corrFunc : (String, StringProperty) -> Boolean) : SimpleBooleanProperty{
        return initField(name, corrFunc, "")
    }



     fun checkCorrectness(e : ActionEvent){
        errorLabel.textProperty().set("")
        isCorrect.set(corrFunc(fieldField.text, errorLabel.textProperty()))
    }

    fun isDisable(state : Boolean){fieldField.isDisable = state}


}