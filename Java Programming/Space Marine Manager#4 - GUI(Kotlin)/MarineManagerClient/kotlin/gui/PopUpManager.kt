package gui

import javafx.scene.Scene
import javafx.stage.Modality
import javafx.stage.Stage
import marine.structure.SetByUser
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method
import java.util.*
import kotlin.collections.HashMap

class PopUpManager {
    companion object{
        public inline fun <reified T>setFieldsSetByUser(obj : T) : Boolean{
            var marineFields : HashMap<String, CommandFieldControl> = HashMap()

            val sortedMethods = PriorityQueue<Method> { o1, o2 -> o1.getAnnotation(SetByUser::class.java).attributeName.compareTo(o2.getAnnotation(SetByUser::class.java).attributeName) }
            for (m in T::class.java.declaredMethods) {
                if (m.isAnnotationPresent(SetByUser::class.java)) sortedMethods.offer(m)
            }

            while (!sortedMethods.isEmpty()) {
                val m = sortedMethods.poll()
                if (m.isAnnotationPresent(SetByUser::class.java)) {
                    val annot = m.getAnnotation(SetByUser::class.java)

                    if(annot.isComplex)continue;

                    var commCont = CommandFieldControl()
                    commCont.initField(Internationalizer.createStringBinding("${annot.attributeName}ColHeader")
                    ) { str, errorProp ->
                        try {
                            m.invoke(obj, str)
                            true;
                        } catch (e: InvocationTargetException) {
                            errorProp.set(e.cause?.message ?: "Exception Occurred")
                            false;
                        }
                    }
                    marineFields[annot.attributeName] = commCont;


                }
            }
            return getParamsByPopUp(marineFields, obj!!::class.simpleName ?: "")
        }


        fun getParamsByPopUp(map: HashMap<String, CommandFieldControl>, title : String) : Boolean{
            val popUp : Stage = Stage()

            val popUpControl = CommandPopUpControl()
            popUpControl.initialize(map, popUp, title)
            val popUpScene : Scene = Scene(popUpControl)
            popUpScene.stylesheets.add("css/popUpWindow.css")
            popUp.scene = popUpScene
            popUp.initModality(Modality.APPLICATION_MODAL)
            popUp.sizeToScene()
            popUp.isResizable = false;
            popUp.showAndWait()
            return popUpControl.isFieldsCorrect
        }

        fun getParamsByPopUp(fieldName : String, field : CommandFieldControl, title : String) : Boolean{
            val popUp : Stage = Stage()

            val popUpControl = CommandPopUpControl()
            var map : HashMap<String, CommandFieldControl> = HashMap();
            map[fieldName] = field;
            return getParamsByPopUp(map, title)
        }
    }
}