package gui

import gui.PopUpManager.Companion.getParamsByPopUp
import gui.PopUpManager.Companion.setFieldsSetByUser
import javafx.application.Platform
import javafx.event.EventHandler
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.control.Button
import javafx.scene.layout.FlowPane
import javafx.stage.FileChooser
import javafx.stage.Stage
import manager.Client
import marine.Command
import marine.net.ResponseContainer
import marine.structure.Chapter
import marine.structure.Coordinates
import marine.structure.SpaceMarine
import models.DataModel
import java.io.File
import java.io.IOException
import java.lang.NumberFormatException
import java.lang.StringBuilder
import java.util.*
import java.util.concurrent.TimeUnit
import javax.management.InvalidAttributeValueException

class CommandsControl : FlowPane() {

    private lateinit var model: DataModel;

    private lateinit var logControl: LogControl;

    private lateinit var executor : Client.CommandExecutor;

    private lateinit var fileChooser : FileChooser;
    private lateinit var stage : Stage;

    init{
        val bundle : ResourceBundle = ResourceBundle.getBundle("TextResources", Internationalizer.locale.value)
        var loader : FXMLLoader = FXMLLoader(javaClass.classLoader.getResource("commandsControl.fxml"), bundle)
        loader.setRoot(this);
        loader.setController(this);

        try{
            loader.load<Parent>()
        }
        catch (e : IOException){
            throw RuntimeException(e);
        }

    }

    fun initModel(model : DataModel, executor: Client.CommandExecutor, logC : LogControl, stage : Stage){
        this.model = model;
        this.logControl = logC;
        this.executor = executor;
        this.stage = stage;

        fileChooser = FileChooser()
        fileChooser.titleProperty().bind(Internationalizer.createStringBinding("scriptChooserTitle"))

        val exec : marine.CommandExecutor = CommandButtonExecutor();
        for (method in CommandButtonExecutor::class.java.declaredMethods.sortedArrayWith({a,b -> a.name.compareTo(b.name)})) {
            if (method.isAnnotationPresent(Command::class.java)) {
                val annot = method.getAnnotation(Command::class.java)

                var curButton = Button();
                curButton.textProperty().bind(Internationalizer.createStringBinding("commButtonLabel${annot.name}"))
                curButton.onAction = EventHandler { method.invoke(exec, null, null) }
                this.children.add(curButton);
            }
        }

    }

    inner class CommandButtonExecutor : marine.CommandExecutor(){
        @Command(name = "help", aliases = ["hlp", "?", "man"], desc = "help - shows list of available commands")
         override fun help(basicArgs: Array<String?>?, complexArgs: Array<Any?>?){
            try {
                var sb = StringBuilder()
                executor.help(arrayOf(), arrayOf(sb))
                logControl.addText(sb.toString())
            }
            catch (e : Exception)
            {
                logControl.addText(e.toString())
            }
         }
        @Command(name = "info", aliases = ["inf", "!", "data"], desc = "info - prints information about current collection (length, date of init etc.)")
         override fun info(basicArgs: Array<String?>?, complexArgs: Array<Any?>?){
            try {
                logControl.addText(executor.executeCommand("info", arrayOf(), arrayOf()).get(5000, TimeUnit.MILLISECONDS).message)
            }
            catch (e : Exception)
            {
                logControl.addText(e.toString())
            }
         }

        @Command(name = "exit", aliases = ["ext", "leave", "quit"], desc = "exit - exits program (without saving)")
         override fun exit(basicArgs: Array<String?>?, complexArgs: Array<Any?>?){
             Platform.exit()
         }

        @Command(name = "show", aliases = ["shw", ":", "look"], desc = "show - prints bio of every Space Marine contained in storage")
         override fun show(basicArgs: Array<String?>?, complexArgs: Array<Any?>?){
            try {
                val response : ResponseContainer = executor.executeCommand("show", arrayOf(), arrayOf()).get(5000, TimeUnit.MILLISECONDS)
                model.setMarines(response.mentionedMarines)
                logControl.addText(response.message)
            }
            catch (e : Exception)
            {
                logControl.addText(e.toString())
            }
         }



        @Command(name = "add", aliases = ["+", "put", "enqueue"], desc = "add {SpaceMarine} - adds a new Space Marine to manager.Storage", objectArgsCount = 1, objectArgsTypes = [SpaceMarine::class])
         override fun add(basicArgs: Array<String?>?, complexArgs: Array<Any?>?){

            var addedMarine : SpaceMarine = SpaceMarine()
            if(!setFieldsSetByUser(addedMarine))return

            var addedChapter : Chapter = Chapter();
            if(!setFieldsSetByUser(addedChapter))return

            var addedCoords : Coordinates = Coordinates();
            if(!setFieldsSetByUser(addedCoords))return

            addedMarine.chapter = addedChapter;
            addedMarine.coordinates = addedCoords;

            try {
                var response : ResponseContainer = executor.executeCommand("add", arrayOf(), arrayOf(addedMarine)).get(5000, TimeUnit.MILLISECONDS)
                logControl.addText(response.message)
                response = executor.executeCommand("show", arrayOf(), arrayOf()).get(5000, TimeUnit.MILLISECONDS)
                model.setMarines(response.mentionedMarines)
            }
            catch (e : Exception)
            {
                logControl.addText(e.toString())
            }
         }

        @Command(name = "update", aliases = ["updt", "^", "change"], desc = "update id {SpaceMarine} - updates attributes of the Marine with passed id with data of a passed SpaceMarine", basicArgsCount = 1, objectArgsCount = 1, objectArgsTypes = [SpaceMarine::class])
        @Throws(InvalidAttributeValueException::class)
         override fun update(basicArgs: Array<String?>?, complexArgs: Array<Any?>?){

            var argControl = CommandFieldControl()
            argControl.initField(Internationalizer.createStringBinding("commandArgID")
            ) { str, errorProp ->
                try {
                    str.toInt()
                    true;
                } catch (e: NumberFormatException) {
                    errorProp.set("ID must be a number!")
                    false;
                }
            }
            if(!getParamsByPopUp("id", argControl, "Update ID"))return

            var addedMarine : SpaceMarine = SpaceMarine()
            if(!setFieldsSetByUser(addedMarine))return

            var addedChapter : Chapter = Chapter();
            if(!setFieldsSetByUser(addedChapter))return

            var addedCoords : Coordinates = Coordinates();
            if(!setFieldsSetByUser(addedCoords))return

            addedMarine.chapter = addedChapter;
            addedMarine.coordinates = addedCoords;

            try {
                var response : ResponseContainer = executor.executeCommand("update", arrayOf(argControl.getFieldValue()), arrayOf(addedMarine)).get(5000, TimeUnit.MILLISECONDS)
                logControl.addText(response.message)
                response = executor.executeCommand("show", arrayOf(), arrayOf()).get(5000, TimeUnit.MILLISECONDS)
                model.setMarines(response.mentionedMarines)
            }
            catch (e : Exception)
            {
                logControl.addText(e.toString())
            }

         }

        @Command(name = "remove_by_id", aliases = ["rm_id", "-id"], desc = "remove_by_id id - removes the Marine with passed id from Storage", basicArgsCount = 1)
        @Throws(Exception::class)
         override fun remove_by_id(basicArgs: Array<String?>?, complexArgs: Array<Any?>?){
            var argControl = CommandFieldControl()
            argControl.initField(Internationalizer.createStringBinding("commandArgID")
            ) { str, errorProp ->
                try {
                    str.toInt()
                    true;
                } catch (e: NumberFormatException) {
                    errorProp.set("ID must be a number!")
                    false;
                }
            }
            if(!getParamsByPopUp("id", argControl, "Remove by id ID"))return

            try {
                var response : ResponseContainer = executor.executeCommand("remove_by_id", arrayOf(argControl.getFieldValue()), arrayOf()).get(5000, TimeUnit.MILLISECONDS)
                logControl.addText(response.message)
                response = executor.executeCommand("show", arrayOf(), arrayOf()).get(5000, TimeUnit.MILLISECONDS)
                model.setMarines(response.mentionedMarines)
            }
            catch (e : Exception)
            {
                logControl.addText(e.toString())
            }
         }

        @Command(name = "clear", aliases = ["clr", "erase"], desc = "clear  - clears the Storage")
         override fun clear(basicArgs: Array<String?>?, complexArgs: Array<Any?>?){
            try {
                var response : ResponseContainer = executor.executeCommand("clear", arrayOf(), arrayOf()).get(5000, TimeUnit.MILLISECONDS)
                logControl.addText(response.message)
                response = executor.executeCommand("show", arrayOf(), arrayOf()).get(5000, TimeUnit.MILLISECONDS)
                model.setMarines(response.mentionedMarines)
            }
            catch (e : Exception)
            {
                logControl.addText(e.toString())
            }
         }

         @Command(name = "execute_script", aliases = ["exec", "script", "sh"], desc = "execute_script file_name - interprets commands from provided file in PATH by lines", basicArgsCount = 1)
        @Throws(Exception::class)
         override fun execute_script(basicArgs: Array<String?>?, complexArgs: Array<Any?>?){

            var file : File? = fileChooser.showOpenDialog(stage)
             if(file != null){

                 try {
                     executor.setFileOp(file.parent)
                     executor.executeCommand("execute_script", arrayOf(file.name), arrayOf())
                     logControl.addText("Successfully executed script ${file.name}.")

                     var response = executor.executeCommand("show", arrayOf(), arrayOf()).get(5000, TimeUnit.MILLISECONDS)
                     model.setMarines(response.mentionedMarines)
                 }
                 catch (e : Exception)
                 {
                     logControl.addText(e.toString())
                 }

             }

         }

        @Command(name = "remove_first", aliases = ["rm_first", "-first"], desc = "remove_first - removes first Space Marine from the Storage")
        @Throws(Exception::class)
        override fun remove_first(basicArgs: Array<String?>?, complexArgs: Array<Any?>?){
            try {
                var response : ResponseContainer = executor.executeCommand("remove_first", arrayOf(), arrayOf()).get(5000, TimeUnit.MILLISECONDS)
                logControl.addText(response.message)
                response = executor.executeCommand("show", arrayOf(), arrayOf()).get(5000, TimeUnit.MILLISECONDS)
                model.setMarines(response.mentionedMarines)
            }
            catch (e : Exception)
            {
                logControl.addText(e.toString())
            }
        }

        @Command(name = "add_if_min", aliases = ["add_min", "if_min"], desc = "add_if_min {SpaceMarine} - adds new Space Marine to Storage if his rating is less, than current minimum", objectArgsCount = 1, objectArgsTypes = [SpaceMarine::class])
        override fun add_if_min(basicArgs: Array<String?>?, complexArgs: Array<Any?>?){
            var addedMarine : SpaceMarine = SpaceMarine()
            if(!setFieldsSetByUser(addedMarine))return

            var addedChapter : Chapter = Chapter();
            if(!setFieldsSetByUser(addedChapter))return

            var addedCoords : Coordinates = Coordinates();
            if(!setFieldsSetByUser(addedCoords))return

            addedMarine.chapter = addedChapter;
            addedMarine.coordinates = addedCoords;

            try {
                var response : ResponseContainer = executor.executeCommand("add_if_min", arrayOf(), arrayOf(addedMarine)).get(5000, TimeUnit.MILLISECONDS)
                logControl.addText(response.message)
                response = executor.executeCommand("show", arrayOf(), arrayOf()).get(5000, TimeUnit.MILLISECONDS)
                model.setMarines(response.mentionedMarines)
            }
            catch (e : Exception)
            {
                logControl.addText(e.toString())
            }
        }
        @Command(name = "remove_greater", aliases = ["rm_greater", "rm_big"], desc = "remove_greater {SpaceMarine} - removes every Space marine that has bigger rating than passed one from Storage.", objectArgsCount = 1, objectArgsTypes = [SpaceMarine::class])
        override fun remove_greater(basicArgs: Array<String?>?, complexArgs: Array<Any?>?){
            var addedMarine : SpaceMarine = SpaceMarine()
            if(!setFieldsSetByUser(addedMarine))return

            var addedChapter : Chapter = Chapter();
            if(!setFieldsSetByUser(addedChapter))return

            var addedCoords : Coordinates = Coordinates();
            if(!setFieldsSetByUser(addedCoords))return

            addedMarine.chapter = addedChapter;
            addedMarine.coordinates = addedCoords;

            try {
                var response : ResponseContainer = executor.executeCommand("remove_greater", arrayOf(), arrayOf(addedMarine)).get(5000, TimeUnit.MILLISECONDS)
                logControl.addText(response.message)
                response = executor.executeCommand("show", arrayOf(), arrayOf()).get(5000, TimeUnit.MILLISECONDS)
                model.setMarines(response.mentionedMarines)
            }
            catch (e : Exception)
            {
                logControl.addText(e.toString())
            }
        }
        @Command(name = "sum_of_health", aliases = ["sum", "hp"], desc = "sum_of_health  - prints sum of health of every Marine in Storage")
        override fun sum_of_health(basicArgs: Array<String?>?, complexArgs: Array<Any?>?){
            try {
                val response : ResponseContainer = executor.executeCommand("sum_of_health", arrayOf(), arrayOf()).get(5000, TimeUnit.MILLISECONDS)
                logControl.addText(response.message)
            }
            catch (e : Exception)
            {
                logControl.addText(e.toString())
            }
        }
        @Command(name = "average_of_health", aliases = ["average", "avrg"], desc = "average_of_health - prints average of health of every Marine in Storage")
        @Throws(Exception::class)
        override fun average_of_health(basicArgs: Array<String?>?, complexArgs: Array<Any?>?){
            try {
                val response : ResponseContainer = executor.executeCommand("average_of_health", arrayOf(), arrayOf()).get(5000, TimeUnit.MILLISECONDS)
                logControl.addText(response.message)
            }
            catch (e : Exception)
            {
                logControl.addText(e.toString())
            }
        }

        @Command(name = "print_unique_weapon_type", aliases = ["unique", "weapType"], desc = "print_unique_weapon_type - prints every unique weapon type of Marines in Storage")
        @Throws(Exception::class)
        override fun print_unique_weapon_type(basicArgs: Array<String?>?, complexArgs: Array<Any?>?){
            try {
                val response : ResponseContainer = executor.executeCommand("print_unique_weapon_type", arrayOf(), arrayOf()).get(5000, TimeUnit.MILLISECONDS)
                logControl.addText(response.message)
            }
            catch (e : Exception)
            {
                logControl.addText(e.toString())
            }
        }

    }



    }
