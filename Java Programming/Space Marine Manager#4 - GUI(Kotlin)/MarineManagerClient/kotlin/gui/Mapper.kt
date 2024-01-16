package gui

import java.lang.IllegalArgumentException
import kotlin.math.ceil

class Mapper {
    companion object{
        fun map(v : Double, x : Double, y : Double,  a : Double, b : Double) : Double{
            if(y < x || b < a) throw IllegalArgumentException()
            if(v > y) return b
            if(v < x) return a
            return (b-a)/(y-x) * (v-x) + a
        }

        fun map(v : Int, x : Int, y : Int,  a : Int, b : Int) : Int{
            if(y < x || b < a) throw IllegalArgumentException()
            if(v > y) return b
            if( v < x) return a
            return ceil((b-a)/(y-x).toDouble() * (v-x) + a).toInt()
        }

        fun map(v : Double, x : Double, y : Double,  a : Int, b : Int) : Int{
            if(y < x || b < a) throw IllegalArgumentException()
            if(v > y) return b
            if( v < x) return a
            return ceil((b-a)/(y-x) * (v-x) + a).toInt()
        }
    }
}