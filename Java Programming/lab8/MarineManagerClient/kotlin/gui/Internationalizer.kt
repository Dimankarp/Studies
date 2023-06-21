package gui

import javafx.beans.binding.Bindings
import javafx.beans.binding.StringBinding
import javafx.beans.property.ObjectProperty
import javafx.beans.property.SimpleObjectProperty
import java.text.DateFormat
import java.text.MessageFormat
import java.text.NumberFormat
import java.time.format.DateTimeFormatter
import java.util.*

class Internationalizer {

    companion object{
        val supportedLocales : Array<Locale> = arrayOf(Locale("en", "US"),  Locale("ru", "RU"), Locale("be", "BY"), Locale("es", "NI"), Locale("it","IT"));
        val locale : ObjectProperty<Locale> = SimpleObjectProperty(supportedLocales[0])
        init{
           locale.addListener { observable, oldValue, newValue -> Locale.setDefault(newValue) };
        }

        fun setLocale(newLocale : Locale){
            if(newLocale in supportedLocales) {
                Locale.setDefault(newLocale);
                locale.set(newLocale);
            }
            else throw IllformedLocaleException("The unsupported locale was provided -  ${newLocale}!")
        }

        fun getString(key : String, vararg args : Any) : String{
            var bundle : ResourceBundle = ResourceBundle.getBundle("TextResources", locale.value);
            return MessageFormat.format(bundle.getString(key), args);
        }


        fun createStringBinding(key: String, vararg  args:Any) : StringBinding{
            return Bindings.createStringBinding({ -> getString(key, args)}, locale);
        }

        fun createNumberFormatBinding(numProp : SimpleObjectProperty<Number>) : StringBinding{
           return Bindings.createStringBinding({ -> NumberFormat.getInstance(locale.value).format(numProp.value) }, numProp, locale);
        }

        fun createDateBinding(dateProp : SimpleObjectProperty<Date>) : StringBinding{
            return Bindings.createStringBinding({ -> DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.MEDIUM, locale.value).format(dateProp.value) }, dateProp, locale);
        }



    }



}