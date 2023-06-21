package gui

import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.geometry.HPos
import javafx.scene.control.*
import javafx.scene.effect.Blend
import javafx.scene.effect.BlendMode
import javafx.scene.effect.ColorAdjust
import javafx.scene.effect.ColorInput
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.layout.GridPane
import javafx.scene.paint.Color
import javafx.util.Callback
import manager.Client
import java.lang.Exception
import java.util.*
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException


class LoginWindowController {


    private lateinit var app : ManagerApp;
    private lateinit var executor : Client.CommandExecutor;
    private lateinit var client : Client;
    @FXML
    private lateinit var loginPane : GridPane;
    @FXML
    private lateinit var welcomeLabel : Label;

    @FXML
    private lateinit var usernameLabel : Label;

    @FXML
    private lateinit var passwordLabel : Label;

    @FXML
    private lateinit var loginLangBox : ComboBox<Locale>;
    private var availableLangs : ObservableList<Locale> = FXCollections.observableList(Internationalizer.supportedLocales.toList());

    @FXML
    private lateinit var loginButton : Button;
    @FXML
    private lateinit var userField : TextField;
    @FXML
    private lateinit var passwordField : PasswordField;

    fun initModel(app : ManagerApp, exec : Client.CommandExecutor, client: Client){
        this.app = app;
        executor = exec;
        this.client = client;

        welcomeLabel.textProperty().bind(Internationalizer.createStringBinding("loginWelcomeLabelText"))
        usernameLabel.textProperty().bind(Internationalizer.createStringBinding("loginUsernameLabelText"))
        passwordLabel.textProperty().bind(Internationalizer.createStringBinding("loginPasswordLabelText"))
        loginButton.textProperty().bind(Internationalizer.createStringBinding("loginButtonText"))
        loginLangBox.items = availableLangs;
        loginLangBox.value = Internationalizer.locale.value;

        val logoImage : Image = Image(javaClass.classLoader.getResource("icons/logoWhite.png").toString(), 80.0, 80.0, true, false)
        val logoView = ImageView(logoImage)
        val clipView = ImageView(logoImage)
        logoView.clip = clipView
        val monochrome = ColorAdjust()
        monochrome.setSaturation(-1.0)
        logoView.effect = Blend(
                BlendMode.MULTIPLY,
                monochrome,
                ColorInput(logoView.x, logoView.y, logoView.fitWidth , logoView.fitHeight, Color.GREEN)
        )


        //<Label text="LOGO"  GridPane.rowIndex="0" maxWidth="Infinity" maxHeight="Infinity" alignment="CENTER"/>
        loginPane.add(logoView, 0, 0)
        GridPane.setHalignment(logoView, HPos.CENTER)


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

        checkConnection(client.isConnected)

        client.connectedProperty.addListener(){ov, oldVal, newVal ->
            checkConnection(newVal)
        }

    }

    fun checkConnection(status : Boolean){
        if(status){
            loginButton.isDisable = false
            userField.isDisable = false
            passwordField.isDisable = false
        }
        else{
            loginButton.isDisable = true
            userField.isDisable = true
            passwordField.isDisable = true
        }
    }

    @FXML
    fun languageChanged(e : ActionEvent){
        Internationalizer.setLocale(loginLangBox.value)
    }
    @FXML
    fun login(e : ActionEvent){
        if(client.isConnected && userField.text.length > 4 && passwordField.text.length>4) {

            userField.isDisable = true;
            passwordField.isDisable = true;


            if (client.isRegistered) {
                app.mainWindow();
                return;
            }
            else {
                val response = executor.executeCommand("login", arrayOf(userField.text, passwordField.text), arrayOf());
                try {
                    response.get(2000, TimeUnit.MILLISECONDS)
                } catch (e: Exception) {
                    userField.isDisable = false;
                    passwordField.isDisable = false;
                    return;
                }
                if (client.isRegistered) {
                    app.mainWindow();
                    return;
                } else {
                    userField.isDisable = false;
                    passwordField.isDisable = false;
                }
            }
        }
    }



}
