package models

import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.value.ChangeListener
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.collections.ObservableSet
import marine.structure.SpaceMarine

class DataModel {

     val marinesList : ObservableList<SpaceMarine?> = FXCollections.observableArrayList(
            {m -> arrayOf()}

    )

    fun addMarine(marine : SpaceMarine){
        synchronized(marinesList){
            marinesList.add(marine)
        }
    }
    fun setMarines(marineColl : Collection<out SpaceMarine>){
        synchronized(marinesList){
            marinesList.setAll(marineColl);
        }

    }
    fun clearMarines(){
        synchronized(marinesList){
            marinesList.clear()
        }
    }

    fun setMarines(marineArr : Array<out SpaceMarine>){
        setMarines(marineArr.toCollection(ArrayList()))
    }






}