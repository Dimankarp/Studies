package gui

import javafx.beans.binding.Bindings
import javafx.beans.property.ObjectProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.value.ObservableValue
import javafx.collections.FXCollections
import javafx.collections.ListChangeListener
import javafx.event.EventHandler
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.control.TableColumn
import javafx.scene.control.TableView
import javafx.scene.control.cell.CheckBoxTableCell
import javafx.scene.control.cell.ComboBoxTableCell
import javafx.scene.control.cell.TextFieldTableCell
import javafx.scene.layout.VBox
import javafx.util.Callback
import manager.Client
import marine.net.ResponseContainer
import marine.structure.SpaceMarine
import marine.structure.Weapon
import models.DataModel
import java.io.IOException
import java.util.*
import java.util.concurrent.TimeUnit


class TableControl : VBox() {

    @FXML
    private lateinit var table: TableView<SpaceMarine>;

    private lateinit var model: DataModel;

    private lateinit var client : Client;
    private lateinit var executor: Client.CommandExecutor;

    init{
        val bundle : ResourceBundle = ResourceBundle.getBundle("TextResources", Internationalizer.locale.value)
        var loader : FXMLLoader = FXMLLoader(javaClass.classLoader.getResource("tableControl.fxml"), bundle)
        loader.setRoot(this);
        loader.setController(this);

        try{
            loader.load<Parent>()
        }
        catch (e : IOException){
            throw RuntimeException(e);
        }

    }

    fun initModel(model : DataModel, client: Client){
        this.model = model;
        this.client = client
        this.executor = client.CommandExecutor();
        table.isEditable = true;

        table.prefHeightProperty().bind(this.prefHeightProperty());

        val idCol : TableColumn<SpaceMarine, Int> = TableColumn();
        idCol.textProperty().bind(Internationalizer.createStringBinding("idColHeader"))
        idCol.cellValueFactory = Callback<TableColumn.CellDataFeatures<SpaceMarine, Int>, ObservableValue<Int>> {p->
            SimpleObjectProperty(p.value.id)
        }
        idCol.isEditable=false

        val ownerCol : TableColumn<SpaceMarine, String> = TableColumn();
        ownerCol.textProperty().bind(Internationalizer.createStringBinding("ownerColHeader"))
        ownerCol.cellValueFactory = Callback<TableColumn.CellDataFeatures<SpaceMarine, String>, ObservableValue<String>> {p->
            SimpleObjectProperty(p.value.owner.nickname)
        }
        ownerCol.isEditable=false

        val nameCol : TableColumn<SpaceMarine, String> = TableColumn();
        nameCol.textProperty().bind(Internationalizer.createStringBinding("nameColHeader"))
        nameCol.cellValueFactory = Callback<TableColumn.CellDataFeatures<SpaceMarine, String>, ObservableValue<String>> {p->
            SimpleObjectProperty(p.value.name)
        }

        nameCol.setCellFactory(TextFieldTableCell.forTableColumn())

        nameCol.onEditCommit = EventHandler {event ->
            var editedMarine : SpaceMarine = event.rowValue;
            try{
                if(editedMarine.owner.nickname.equals(String(client.userCredit.nicknameBytes))) {
                    editedMarine.setName(event.newValue)
                    updateMarine(editedMarine)
                }
            }
            catch (e : Exception)
            {
            }
            finally {
                event.getTableView().getColumns().get(0).setVisible(false);
                event.getTableView().getColumns().get(0).setVisible(true);

            }
        }


        val healthCol : TableColumn<SpaceMarine, String> = TableColumn();
        healthCol.textProperty().bind(Internationalizer.createStringBinding("healthColHeader"))
        healthCol.cellValueFactory = Callback<TableColumn.CellDataFeatures<SpaceMarine, String>, ObservableValue<String>> {p->
            SimpleObjectProperty(p.value.health.toString())
        }

        healthCol.setCellFactory(TextFieldTableCell.forTableColumn())
        healthCol.comparator = Comparator(){x,y ->  x.toInt()-y.toInt()}



        healthCol.onEditCommit = EventHandler {event ->
            var editedMarine : SpaceMarine = event.rowValue;
            try{
                if(editedMarine.owner.nickname.equals(String(client.userCredit.nicknameBytes))) {
                    editedMarine.setHealth(event.newValue)
                    updateMarine(editedMarine)
                }
            }
            catch (e : Exception)
            {
            }
            finally {
                event.getTableView().getColumns().get(0).setVisible(false);
                event.getTableView().getColumns().get(0).setVisible(true);

            }
        }


        val achievementsCol : TableColumn<SpaceMarine, String> = TableColumn();
        achievementsCol.textProperty().bind(Internationalizer.createStringBinding("achievementsColHeader"))
        achievementsCol.cellValueFactory = Callback<TableColumn.CellDataFeatures<SpaceMarine, String>, ObservableValue<String>> {p->
            SimpleObjectProperty(p.value.achievements)
        }
        achievementsCol.setCellFactory(TextFieldTableCell.forTableColumn())

        achievementsCol.onEditCommit = EventHandler {event ->
            var editedMarine : SpaceMarine = event.rowValue;
            try{
                if(editedMarine.owner.nickname.equals(String(client.userCredit.nicknameBytes))) {
                    editedMarine.achievements = (event.newValue)
                    updateMarine(editedMarine)
                }
            }
            catch (e : Exception)
            {
            }
            finally {
                event.getTableView().getColumns().get(0).setVisible(false);
                event.getTableView().getColumns().get(0).setVisible(true);

            }
        }


        val loyalCol : TableColumn<SpaceMarine, Boolean> = TableColumn();
        loyalCol.textProperty().bind(Internationalizer.createStringBinding("loyalColHeader"))
        loyalCol.cellValueFactory = Callback<TableColumn.CellDataFeatures<SpaceMarine, Boolean>, ObservableValue<Boolean>> {p->
            SimpleObjectProperty(p.value.loyal)
        }

        loyalCol.setCellFactory(ComboBoxTableCell.forTableColumn(true, false))
        loyalCol.onEditCommit = EventHandler {event ->
            var editedMarine : SpaceMarine = event.rowValue;
            try{
                if(editedMarine.owner.nickname.equals(String(client.userCredit.nicknameBytes))) {
                    editedMarine.loyal = (event.newValue)
                    updateMarine(editedMarine)
                }
            }
            catch (e : Exception)
            {
            }
            finally {
                event.getTableView().getColumns().get(0).setVisible(false);
                event.getTableView().getColumns().get(0).setVisible(true);

            }
        }



        val weaponTypeCol : TableColumn<SpaceMarine, Weapon> = TableColumn();
        weaponTypeCol.textProperty().bind(Internationalizer.createStringBinding("weaponTypeColHeader"))
        weaponTypeCol.cellValueFactory = Callback<TableColumn.CellDataFeatures<SpaceMarine, Weapon>, ObservableValue<Weapon>> {p->
            SimpleObjectProperty(p.value.weaponType)
        }

        weaponTypeCol.setCellFactory(ComboBoxTableCell.forTableColumn(Weapon.FLAMER, Weapon.HEAVY_FLAMER, Weapon.GRAV_GUN, Weapon.MISSILE_LAUNCHER))
        weaponTypeCol.onEditCommit = EventHandler {event ->
            var editedMarine : SpaceMarine = event.rowValue;
            try{
                if(editedMarine.owner.nickname.equals(String(client.userCredit.nicknameBytes))) {
                    editedMarine.weaponType = (event.newValue)
                    updateMarine(editedMarine)
                }
            }
            catch (e : Exception)
            {
            }
            finally {
                event.getTableView().getColumns().get(0).setVisible(false);
                event.getTableView().getColumns().get(0).setVisible(true);

            }
        }


        val chapterCol : TableColumn<SpaceMarine, String> = TableColumn();
        chapterCol.textProperty().bind(Internationalizer.createStringBinding("chapterColHeader"))



        val marinesCountCol : TableColumn<SpaceMarine, String> = TableColumn();
        marinesCountCol.textProperty().bind(Internationalizer.createStringBinding("marinesCountColHeader"))
        marinesCountCol.cellValueFactory = Callback<TableColumn.CellDataFeatures<SpaceMarine, String>, ObservableValue<String>> {p->
            SimpleObjectProperty(p.value.chapter.marinesCount.toString())
        }

        marinesCountCol.setCellFactory(TextFieldTableCell.forTableColumn())
        marinesCountCol.comparator = Comparator(){x,y ->  x.toInt()-y.toInt()}
        marinesCountCol.onEditCommit = EventHandler {event ->
            var editedMarine : SpaceMarine = event.rowValue;
            try{
                if(editedMarine.owner.nickname.equals(String(client.userCredit.nicknameBytes))) {
                    editedMarine.chapter.setMarinesCount(event.newValue)
                    updateMarine(editedMarine)
                }
            }
            catch (e : Exception)
            {
            }
            finally {
                event.getTableView().getColumns().get(0).setVisible(false);
                event.getTableView().getColumns().get(0).setVisible(true);

            }
        }


        val parentLegionCol : TableColumn<SpaceMarine, String> = TableColumn();
        parentLegionCol.textProperty().bind(Internationalizer.createStringBinding("parentLegionColHeader"))
        parentLegionCol.cellValueFactory = Callback<TableColumn.CellDataFeatures<SpaceMarine, String>, ObservableValue<String>> {p->
            SimpleObjectProperty(p.value.chapter.parentLegion)
        }

        parentLegionCol.setCellFactory(TextFieldTableCell.forTableColumn())

        parentLegionCol.onEditCommit = EventHandler {event ->
            var editedMarine : SpaceMarine = event.rowValue;
            try{
                if(editedMarine.owner.nickname.equals(String(client.userCredit.nicknameBytes))) {
                    editedMarine.chapter.parentLegion = (event.newValue)
                    updateMarine(editedMarine)
                }
            }
            catch (e : Exception)
            {
            }
            finally {
                event.getTableView().getColumns().get(0).setVisible(false);
                event.getTableView().getColumns().get(0).setVisible(true);

            }
        }


        val worldCol : TableColumn<SpaceMarine, String> = TableColumn();
        worldCol.textProperty().bind(Internationalizer.createStringBinding("worldColHeader"))
        worldCol.cellValueFactory = Callback<TableColumn.CellDataFeatures<SpaceMarine, String>, ObservableValue<String>> {p->
            SimpleObjectProperty(p.value.chapter.world)
        }
        worldCol.setCellFactory(TextFieldTableCell.forTableColumn())

        worldCol.onEditCommit = EventHandler {event ->
            var editedMarine : SpaceMarine = event.rowValue;
            try{
                if(editedMarine.owner.nickname.equals(String(client.userCredit.nicknameBytes))) {
                    editedMarine.chapter.world = (event.newValue)
                    updateMarine(editedMarine)
                }
            }
            catch (e : Exception)
            {
            }
            finally {
                event.getTableView().getColumns().get(0).setVisible(false);
                event.getTableView().getColumns().get(0).setVisible(true);

            }
        }

        chapterCol.columns.addAll(marinesCountCol, parentLegionCol, worldCol)

        val coordsCol : TableColumn<SpaceMarine, String> = TableColumn();
        coordsCol.textProperty().bind(Internationalizer.createStringBinding("coordinatesColHeader"))


        val xCol : TableColumn<SpaceMarine, String> = TableColumn();
        xCol.textProperty().bind(Internationalizer.createStringBinding("xColHeader"))
        xCol.cellValueFactory = Callback<TableColumn.CellDataFeatures<SpaceMarine, String>, ObservableValue<String>> {p->
            SimpleObjectProperty(p.value.coordinates.x.toString())
        }

        xCol.setCellFactory(TextFieldTableCell.forTableColumn())
        xCol.comparator = Comparator(){x,y ->  x.toInt()-y.toInt()}
        xCol.onEditCommit = EventHandler {event ->
            var editedMarine : SpaceMarine = event.rowValue;
            try{
                if(editedMarine.owner.nickname.equals(String(client.userCredit.nicknameBytes))) {
                    editedMarine.coordinates.setX(event.newValue)
                    updateMarine(editedMarine)
                }
            }
            catch (e : Exception)
            {
            }
            finally {
                event.getTableView().getColumns().get(0).setVisible(false);
                event.getTableView().getColumns().get(0).setVisible(true);

            }
        }

        val yCol : TableColumn<SpaceMarine, String> = TableColumn();
        yCol.textProperty().bind(Internationalizer.createStringBinding("yColHeader"))
        yCol.cellValueFactory = Callback<TableColumn.CellDataFeatures<SpaceMarine, String>, ObservableValue<String>> {p->
            SimpleObjectProperty(p.value.coordinates.y.toString())
        }
        yCol.setCellFactory(TextFieldTableCell.forTableColumn())
        yCol.comparator = Comparator(){x,y ->  x.toInt()-y.toInt()}
        yCol.onEditCommit = EventHandler {event ->
            var editedMarine : SpaceMarine = event.rowValue;
            try{
                if(editedMarine.owner.nickname.equals(String(client.userCredit.nicknameBytes))) {
                    editedMarine.coordinates.setY(event.newValue)
                    updateMarine(editedMarine)
                }
            }
            catch (e : Exception)
            {
            }
            finally {
                event.getTableView().getColumns().get(0).setVisible(false);
                event.getTableView().getColumns().get(0).setVisible(true);

            }
        }


        coordsCol.columns.addAll(xCol, yCol);

        table.columns.addAll(idCol, ownerCol, nameCol, healthCol, achievementsCol, loyalCol, weaponTypeCol, chapterCol, coordsCol)


        table.itemsProperty().set(model.marinesList);
      //  table.itemsProperty().value.addListener {e : ListChangeListener.Change<out SpaceMarine?> -> table.sortOrder.clear()}



    }

    private fun updateMarine(updatedMarine : SpaceMarine){
        try {
            executor.executeCommand("update", arrayOf(updatedMarine.id.toString()), arrayOf(updatedMarine)).get(5000, TimeUnit.MILLISECONDS)
            var response : ResponseContainer = executor.executeCommand("show", arrayOf(), arrayOf()).get(5000, TimeUnit.MILLISECONDS)
            client.model.setMarines(response.mentionedMarines)
        }
        catch (e : Exception)
        {
        }
    }


}