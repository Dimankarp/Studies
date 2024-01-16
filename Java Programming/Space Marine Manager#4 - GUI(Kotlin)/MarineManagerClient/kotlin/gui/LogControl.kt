package gui

import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.control.Label
import javafx.scene.control.ScrollPane
import javafx.scene.layout.*
import javafx.scene.text.Text
import javafx.scene.text.TextFlow
import models.DataModel
import java.io.IOException
import java.lang.RuntimeException
import java.util.*

class LogControl : GridPane() {

    @FXML
    private lateinit var logFlow : TextFlow;

    @FXML
    private lateinit var scrollPane : ScrollPane;

    private val MAXIMUM_MESSAGES : Int = 50;

    init{
        val bundle : ResourceBundle = ResourceBundle.getBundle("TextResources", Internationalizer.locale.value)
        var loader : FXMLLoader = FXMLLoader(javaClass.classLoader.getResource("logControl.fxml"), bundle)
        loader.setRoot(this);
        loader.setController(this);
        try{
            loader.load<Parent>()
        }
        catch (e : IOException){
            throw RuntimeException(e);
        }

        logFlow.lineSpacing = 2.0;

    }

    fun addText(text : String){
        if(logFlow.children.count() > MAXIMUM_MESSAGES) logFlow.children.remove(0,1);
        logFlow.children.add(Text(text + "\n"))
        scrollPane.vvalue = 1.0
    }


}