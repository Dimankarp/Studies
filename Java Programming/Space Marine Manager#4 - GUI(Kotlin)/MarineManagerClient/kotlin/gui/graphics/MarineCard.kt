package gui.graphics

import gui.CommandFieldControl
import gui.Internationalizer
import gui.Mapper
import gui.PopUpManager
import javafx.event.EventHandler
import javafx.scene.Group
import javafx.scene.effect.*
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.input.MouseEvent
import javafx.scene.paint.Color
import javafx.scene.paint.Paint
import javafx.scene.shape.Circle
import javafx.scene.text.Text
import marine.structure.SpaceMarine
import java.lang.Exception
import java.lang.reflect.InvocationTargetException
import java.util.DoubleSummaryStatistics
import java.util.function.Consumer
import javax.management.InvalidAttributeValueException

class MarineCard (marine: SpaceMarine){


    private val representedMarine : SpaceMarine

    private val imageView : ImageView;
    private val clipView : ImageView
    private val circle : Circle;
    private val text : Text;
    private val paint : Paint;
    private var coloringEffect : Effect;
    private var originalRadius : Double
    private var originalImageDim : Double
    private var widthBound : Double
    private var heightBound : Double
    public var isEditable : Boolean = false

    companion object{
        public var onUpdateConsumer : Consumer<SpaceMarine> = Consumer({})
    }


    fun setBound(h : Double, w: Double){
        widthBound = w
        heightBound = h
    }


    public var scale : Double = 1.0
        set(scale : Double){
            field = scale

            circle.radius = originalRadius * scale
            circle.centerX = Mapper.map(representedMarine.coordinates.x.toDouble() * scale, -1000.0, 1000.0, 0.0, widthBound * scale)
            circle.centerY = Mapper.map(representedMarine.coordinates.y.toDouble() * scale, -1000.0, 1000.0, 0.0, heightBound * scale)
            circle.strokeWidth = 0.2 * scale
            imageView.fitWidth = originalImageDim * scale
            imageView.fitHeight = originalImageDim * scale
            imageView.x = circle.centerX - imageView.fitWidth/2
            imageView.y = circle.centerY - circle.radius - imageView.fitHeight

            clipView.x = imageView.x
            clipView.y = imageView.y
            clipView.fitWidth = imageView.fitWidth
            clipView.fitHeight = imageView.fitHeight

            val monochrome = ColorAdjust()
            monochrome.setSaturation(-1.0)
            coloringEffect = Blend(
                    BlendMode.MULTIPLY,
                    monochrome,
                    ColorInput(imageView.x, imageView.y, imageView.fitWidth , imageView.fitHeight, paint)
            )
            imageView.clip = clipView
            imageView.effect = coloringEffect
            text.stroke
            text.x = imageView.x
            text.y = imageView.y
           // println(imageView.fitWidth)

           // println("${representedMarine.id} | Circle: x ${circle.centerX} y ${circle.centerY} radius ${circle.radius} | text: x ${text.x}  y ${text.y} | image: x ${imageView.x} y ${imageView.y}")

        }



    init{
        representedMarine = marine
        val image : Image = Image(javaClass.classLoader.getResource("icons/4.png").toString())
        imageView = ImageView(image)
        imageView.isPreserveRatio = true
        imageView.isSmooth = false
        originalImageDim = 50.0
        imageView.fitHeight = originalImageDim
        imageView.fitWidth = originalImageDim


        var r = marine.owner.color.getRed();
        var g = marine.owner.color.getGreen();
        var b = marine.owner.color.getBlue();
        var a = marine.owner.color.getAlpha();
        var opacity = 0.8
        paint = Color.rgb(r,g,b,opacity)

         originalRadius = Mapper.map(marine.health.toDouble(), 1.0, 1000.0, 0.5, 5.0);
        originalImageDim = 50.0 * Mapper.map(originalRadius, 0.5, 5.0, 0.8, 1.2);
        imageView.fitHeight = originalImageDim
        imageView.fitWidth = originalImageDim


        heightBound = 1000.0
        widthBound = 1000.0

        circle = Circle(Mapper.map(marine.coordinates.x.toDouble() * scale, -1000.0, 1000.0, 0.0, widthBound * scale),
                Mapper.map(marine.coordinates.y.toDouble() * scale, -1000.0, 1000.0, 0.0, heightBound * scale),
                originalRadius , paint)
        circle.strokeWidth = 0.2
        circle.stroke = Color.BLACK

        clipView = ImageView(image)
        clipView.isPreserveRatio = true
        clipView.isSmooth = false



        imageView.onMouseClicked = EventHandler {e -> updateMarineByPopup(e)}


        val monochrome = ColorAdjust()
        monochrome.setSaturation(-1.0)
         coloringEffect = Blend(
                BlendMode.MULTIPLY,
                monochrome,
                ColorInput(imageView.x, imageView.y, imageView.fitWidth , imageView.fitHeight, paint)
        )

        text = Text("ID: ${marine.id}")
        text.stroke = paint
        scale = scale
    }

    public fun addToGroup(gr : Group){
        gr.children.addAll(circle, imageView, text)
    }

    //Sadly, no Reflection here (I have annotations only for setters)
    fun updateMarineByPopup(e : MouseEvent){
        val fields : HashMap<String, CommandFieldControl> = HashMap()


        var fieldControl : CommandFieldControl = CommandFieldControl()

        fields["ID"] = fieldControl
        fieldControl.initField(Internationalizer.createStringBinding("idColHeader"), {str, errorProp ->
            true
        }, representedMarine.id.toString())
        fieldControl.isDisable(true)

        fieldControl  = CommandFieldControl()
        fields["Owner"] = fieldControl
        fieldControl.initField(Internationalizer.createStringBinding("ownerColHeader"), {str, errorProp ->
            true
        }, representedMarine.owner.nickname)
        fieldControl.isDisable(true)


        fieldControl  = CommandFieldControl()
        fields["Name"] = fieldControl
        fieldControl.initField(Internationalizer.createStringBinding("nameColHeader"), {str, errorProp ->
                try {
                    representedMarine.name = str
                    true;
                } catch (e: InvalidAttributeValueException) {
                    errorProp.set(e.cause?.message ?: "Exception Occurred")
                    false;
                }
        }, representedMarine.name)

        fieldControl  = CommandFieldControl()
        fields["Health"] = fieldControl
        fieldControl.initField(Internationalizer.createStringBinding("healthColHeader"), {str, errorProp ->
            try {
                representedMarine.setHealth(str)
                true;
            } catch (e: InvalidAttributeValueException) {
                errorProp.set(e.cause?.message ?: "Exception Occurred")
                false;
            }
        }, representedMarine.health.toString())

        fieldControl  = CommandFieldControl()
        fields["Achievements"] = fieldControl
        fieldControl.initField(Internationalizer.createStringBinding("achievementsColHeader"), {str, errorProp ->
            try {
                representedMarine.achievements = str
                true;
            } catch (e: InvalidAttributeValueException) {
                errorProp.set(e.cause?.message ?: "Exception Occurred")
                false;
            }
        }, representedMarine.achievements)

        fieldControl  = CommandFieldControl()
        fields["Is Loyal"] = fieldControl
        fieldControl.initField(Internationalizer.createStringBinding("loyalColHeader"), {str, errorProp ->
            try {
                representedMarine.setLoyal(str)
                true;
            } catch (e: InvalidAttributeValueException) {
                errorProp.set(e.cause?.message ?: "Exception Occurred")
                false;
            }
        }, representedMarine.loyal.toString())

        fieldControl  = CommandFieldControl()
        fields["Weapon"] = fieldControl
        fieldControl.initField(Internationalizer.createStringBinding("weaponTypeColHeader"), {str, errorProp ->
            try {
                representedMarine.setWeaponType(str)
                true;
            } catch (e: InvalidAttributeValueException) {
                errorProp.set(e.cause?.message ?: "Exception Occurred")
                false;
            }
        }, representedMarine.weaponType.toString())

        fieldControl  = CommandFieldControl()
        fields["MarinesCount"] = fieldControl
        fieldControl.initField(Internationalizer.createStringBinding("marinesCountColHeader"), {str, errorProp ->
            try {
                representedMarine.chapter.setMarinesCount(str)
                true;
            } catch (e: InvalidAttributeValueException) {
                errorProp.set(e.cause?.message ?: "Exception Occurred")
                false;
            }
        }, representedMarine.chapter.marinesCount.toString())

        fieldControl  = CommandFieldControl()
        fields["ParentLegion"] = fieldControl
        fieldControl.initField(Internationalizer.createStringBinding("parentLegionColHeader"), {str, errorProp ->
            try {
                representedMarine.chapter.parentLegion = str
                true;
            } catch (e: InvalidAttributeValueException) {
                errorProp.set(e.cause?.message ?: "Exception Occurred")
                false;
            }
        }, representedMarine.chapter.parentLegion ?: "")

        fieldControl  = CommandFieldControl()
        fields["World"] = fieldControl
        fieldControl.initField(Internationalizer.createStringBinding("worldColHeader"), {str, errorProp ->
            try {
                representedMarine.chapter.world = str
                true;
            } catch (e: InvalidAttributeValueException) {
                errorProp.set(e.cause?.message ?: "Exception Occurred")
                false;
            }
        }, representedMarine.chapter.world)

        fieldControl  = CommandFieldControl()
        fields["X"] = fieldControl
        fieldControl.initField(Internationalizer.createStringBinding("xColHeader"), {str, errorProp ->
            try {
                representedMarine.coordinates.setX(str)
                true;
            } catch (e: InvalidAttributeValueException) {
                errorProp.set(e.cause?.message ?: "Exception Occurred")
                false;
            }
        }, representedMarine.coordinates.x.toString())

        fieldControl  = CommandFieldControl()
        fields["Y"] = fieldControl
        fieldControl.initField(Internationalizer.createStringBinding("yColHeader"), {str, errorProp ->
            try {
                representedMarine.coordinates.setY(str)
                true;
            } catch (e: InvalidAttributeValueException) {
                errorProp.set(e.cause?.message ?: "Exception Occurred")
                false;
            }
        }, representedMarine.coordinates.y.toString())

        if(!isEditable){
            for(contrl in fields.values){
                contrl.isDisable(true)
            }
        }
        PopUpManager.getParamsByPopUp(fields, "")

        if(isEditable) onUpdateConsumer.accept(representedMarine)

    }



}